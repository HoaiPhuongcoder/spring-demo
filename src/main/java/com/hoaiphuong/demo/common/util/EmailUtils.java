package com.hoaiphuong.demo.common.util;

import java.util.Locale;

public final class EmailUtils {
    private EmailUtils() {}

    /** Trims and lower-cases an email so that "A@Mail.com " and "a@mail.com" are treated as the same address. */
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
