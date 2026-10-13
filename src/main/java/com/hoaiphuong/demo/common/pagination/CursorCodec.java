package com.hoaiphuong.demo.common.pagination;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;

import java.util.Base64;
import java.util.Map;

public final class CursorCodec {
    private static final ObjectMapper MAPPER =  new ObjectMapper().findAndRegisterModules();

    public static String encode(ScrollPosition pos){
        try {
            KeysetScrollPosition k = (KeysetScrollPosition) pos;
            byte[] json = MAPPER.writeValueAsBytes(k.getKeys());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    public static ScrollPosition decode(String token){
        if(token == null){
            return ScrollPosition.keyset();
        }
        try {
            byte[] json = Base64.getUrlDecoder().decode(token);
            Map<String, Object> keys = MAPPER.readValue(json, new TypeReference<>() {});
            return ScrollPosition.forward(keys);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
}
