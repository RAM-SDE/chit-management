package com.chit_management.chit.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public class LogUtil {
    public static String build(HttpServletRequest request, String action, UUID uuid) {
        String ip = request.getRemoteAddr();
        return String.format(
                "[%s] IP:%s User Id:%s",
                action,
                ip,
                uuid
        );
    }
}
