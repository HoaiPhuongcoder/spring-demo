package com.hoaiphuong.demo.config.i18n;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig {
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final List<Locale> SUPPORTED_LOCALES = List.of(Locale.ENGLISH, Locale.forLanguageTag("vi"));

    /**
     * Picks the locale from the Accept-Language header. Missing or unsupported languages fall back to English,
     * regardless of the server's default locale.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(DEFAULT_LOCALE);
        resolver.setSupportedLocales(SUPPORTED_LOCALES);
        return resolver;
    }
}
