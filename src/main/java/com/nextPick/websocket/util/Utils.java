package com.nextPick.websocket.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextPick.websocket.message.Message;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // JSON -> Java Object (deserialization)
    public static Message getObject(final String message) throws Exception {
        return objectMapper.readValue(message, Message.class);
    }

    // Java Object -> JSON (serialization)
    public static String getString(final Message message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
