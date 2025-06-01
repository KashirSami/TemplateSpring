package com.template.template.utilities;

import java.text.Normalizer;

public class StringUtils {
    public static String normalizeNoAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase().trim();
    }
}
