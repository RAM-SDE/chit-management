package com.chit_management.chit.util;

public class UuidUtil {

    private UuidUtil() {}

    /**
     * Validate and return uuid string.
     * Throws IllegalArgumentException if blank or invalid.
     */
    public static String validate(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new IllegalArgumentException(
                    "UUID cannot be blank");
        }
        if (!uuidStr.matches(
                "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
                        "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                        "[0-9a-fA-F]{12}")) {
            throw new IllegalArgumentException(
                    "Invalid UUID format: " + uuidStr);
        }
        return uuidStr.toLowerCase();
    }

    /**
     * Returns null if blank, validates if present.
     */
    public static String validateNullable(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) return null;
        return validate(uuidStr);
    }
}
