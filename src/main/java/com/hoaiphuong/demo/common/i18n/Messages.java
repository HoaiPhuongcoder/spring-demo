package com.hoaiphuong.demo.common.i18n;

import com.hoaiphuong.demo.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves messages from {@code i18n/messages*.properties} using the current request locale
 * (taken from the Accept-Language header, English by default).
 */
@Component
@RequiredArgsConstructor
public class Messages {
    private final MessageSource messageSource;

    public String get(ErrorCode code) {
        return get(code.getMessageKey());
    }

    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Resolves a binding/validation error: tries its codes (e.g. {@code typeMismatch}) in the bundles first,
     * then falls back to its default message (Bean Validation messages are already localized).
     */
    public String get(MessageSourceResolvable resolvable) {
        return messageSource.getMessage(resolvable, LocaleContextHolder.getLocale());
    }
}
