package com.hoaiphuong.demo.common.pagination;


import com.hoaiphuong.demo.common.exception.InvalidCursorException;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Encodes / decodes cursors for keyset pagination.
 * A cursor only stores the key values; decode always returns a forward position (forward-only scrolling).
 */
public final class CursorCodec {
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private CursorCodec() {}

    public static String encode(ScrollPosition pos){
        if (!(pos instanceof KeysetScrollPosition k)) {
            throw new IllegalArgumentException("CursorCodec only supports KeysetScrollPosition, got: "
                    + (pos == null ? "null" : pos.getClass().getSimpleName()));
        }
        byte[] json = MAPPER.writeValueAsBytes(k.getKeys());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
    }

    public static ScrollPosition decode(String token, Map<String, Class<?>> keyTypes) {
        if (token == null || token.isBlank()) {
            return ScrollPosition.keyset();
        }
        try {
            byte[] json = Base64.getUrlDecoder().decode(token);
            Map<String, Object> raw = MAPPER.readValue(json, new TypeReference<>() {});

            Map<String, Object> keys = new LinkedHashMap<>();
            for (var e : keyTypes.entrySet()) {
                Object value = raw.get(e.getKey());
                if (value == null) throw new InvalidCursorException();
                keys.put(e.getKey(), MAPPER.convertValue(value, e.getValue()));
            }
            return ScrollPosition.forward(keys);
        } catch (InvalidCursorException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCursorException();
        }
    }
}
