package com.hoaiphuong.demo.config.openapi;

import com.hoaiphuong.demo.common.exception.ErrorCode;
import com.hoaiphuong.demo.config.i18n.LocaleConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {
    private static final String ERROR_SCHEMA = "ErrorResponse";
    private static final String EXAMPLE_TIMESTAMP = "2026-01-01T00:00:00Z";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Swagger Demo API")
                        .version("v1")
                        .description("""
                                Swagger Demo API.

                                **Response format:** every response is wrapped in `ApiResponse` \
                                (`success`, `code`, `message`, `data`, `meta`, `errors`, `timestamp`). \
                                Null fields are omitted from the JSON.

                                **Localization:** `message` and validation errors follow the `Accept-Language` \
                                header (`en`, `vi`). English is used when the header is missing or unsupported.

                                **Offset pagination:** `page` (zero-based), `size` (1-100, default 20), \
                                `sortBy`, `direction` (`asc`/`desc`, default `desc`). Page info is returned in `meta`.

                                **Cursor pagination:** `limit` (1-100, default 20) and `cursor` taken from \
                                `meta.nextCursor` of the previous response. Omit `cursor` to get the first batch.
                                """)
                        .contact(new Contact().name("Hoai Phuong")))
                .components(new Components()
                        .addSchemas(ERROR_SCHEMA, errorResponseSchema()));
    }

    /**
     * Adds to every endpoint: the optional Accept-Language header, and error responses built from
     * {@link ApiErrorCodes} (INTERNAL_ERROR is always included). Codes sharing an HTTP status are grouped
     * into one response, with one example per code using the default (English) message.
     */
    @Bean
    public OperationCustomizer errorResponsesCustomizer(MessageSource messageSource) {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(acceptLanguageHeader());

            Set<ErrorCode> codes = new LinkedHashSet<>();
            ApiErrorCodes declared = handlerMethod.getMethodAnnotation(ApiErrorCodes.class);
            if (declared != null) {
                codes.addAll(Arrays.asList(declared.value()));
            }
            codes.add(ErrorCode.INTERNAL_ERROR);

            Map<HttpStatus, List<ErrorCode>> byStatus = codes.stream()
                    .collect(Collectors.groupingBy(ErrorCode::getStatus, LinkedHashMap::new, Collectors.toList()));

            ApiResponses responses = operation.getResponses();
            byStatus.forEach((status, list) -> {
                MediaType mediaType = new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/" + ERROR_SCHEMA));
                list.forEach(ec -> {
                    String message = messageSource.getMessage(ec.getMessageKey(), null, LocaleConfig.DEFAULT_LOCALE);
                    mediaType.addExamples(ec.name(),
                            new Example().summary(message).value(errorExample(ec, message)));
                });
                responses.addApiResponse(String.valueOf(status.value()), new ApiResponse()
                        .description(status.getReasonPhrase())
                        .content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType)));
            });
            return operation;
        };
    }

    private static HeaderParameter acceptLanguageHeader() {
        HeaderParameter header = new HeaderParameter();
        header.name(HttpHeaders.ACCEPT_LANGUAGE)
                .description("Language of response messages. Defaults to English when missing or unsupported")
                .required(false)
                .schema(new StringSchema()
                        ._enum(LocaleConfig.SUPPORTED_LOCALES.stream().map(Locale::toLanguageTag).toList())
                        ._default(LocaleConfig.DEFAULT_LOCALE.toLanguageTag()));
        return header;
    }

    private static Schema<?> errorResponseSchema() {
        Schema<?> fieldError = new ObjectSchema()
                .addProperty("code", new StringSchema().description("Name of the invalid field / parameter").example("email"))
                .addProperty("message", new StringSchema().description("Error description").example("must be a well-formed email address"));

        return new ObjectSchema()
                .description("Error response: same format as ApiResponse but without data/meta")
                .addProperty("success", new BooleanSchema().description("Always false").example(false))
                .addProperty("code", new StringSchema()
                        .description("Error code")
                        ._enum(Arrays.stream(ErrorCode.values()).map(Enum::name).toList()))
                .addProperty("message", new StringSchema().description("Error message, localized by the Accept-Language header"))
                .addProperty("errors", new ArraySchema()
                        .items(fieldError)
                        .description("Per-field error details, only present when request validation fails"))
                .addProperty("timestamp", new DateTimeSchema().description("Time the error occurred (UTC)"))
                .required(List.of("success", "code", "message", "timestamp"));
    }

    private static Map<String, Object> errorExample(ErrorCode ec, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("code", ec.name());
        body.put("message", message);
        if (ec == ErrorCode.VALIDATION_ERROR) {
            body.put("errors", List.of(Map.of("code", "email", "message", "must be a well-formed email address")));
        }
        body.put("timestamp", EXAMPLE_TIMESTAMP);
        return body;
    }
}
