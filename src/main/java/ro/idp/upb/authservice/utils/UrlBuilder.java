package ro.idp.upb.authservice.utils;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public final class UrlBuilder {

    private UrlBuilder() {
        throw new RuntimeException("Cannot instantiate UrlBuilder class!");
    }

    public static String replacePlaceholdersInString(String stringTemplate, Map<String, Object> arguments) {
        return replacePlaceholdersInString(stringTemplate, arguments, "${", "}");
    }

    public static String replacePlaceholdersInString(String stringTemplate, Map<String, Object> arguments, String placeholderStart, String placeholderEnd) {
        return StringSubstitutor.replace(stringTemplate, arguments, placeholderStart, placeholderEnd);
    }
}
