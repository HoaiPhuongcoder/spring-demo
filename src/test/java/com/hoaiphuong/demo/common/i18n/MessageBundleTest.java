package com.hoaiphuong.demo.common.i18n;

import com.hoaiphuong.demo.common.exception.ErrorCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessageBundleTest {

    private static Properties load(String path) throws Exception {
        var in = MessageBundleTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull(in, path + " not found");
        Properties props = new Properties();
        try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        return props;
    }

    @ParameterizedTest
    @ValueSource(strings = {"i18n/messages.properties", "i18n/messages_vi.properties"})
    void everyErrorCodeHasAMessage(String bundle) throws Exception {
        Properties props = load(bundle);
        List<String> missing = Arrays.stream(ErrorCode.values())
                .map(ErrorCode::getMessageKey)
                .filter(key -> !props.containsKey(key))
                .toList();
        assertEquals(List.of(), missing, "Missing keys in " + bundle);
    }

    @ParameterizedTest
    @ValueSource(strings = {"i18n/messages_vi.properties"})
    void translationHasEveryDefaultErrorKey(String bundle) throws Exception {
        Properties defaults = load("i18n/messages.properties");
        Properties translated = load(bundle);
        List<String> missing = defaults.stringPropertyNames().stream()
                .filter(key -> key.startsWith("error."))
                .filter(key -> !translated.containsKey(key))
                .sorted()
                .toList();
        assertEquals(List.of(), missing, "Missing keys in " + bundle);
    }
}
