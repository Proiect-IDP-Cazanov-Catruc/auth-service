package ro.idp.upb.authservice.utils;

public class StringUtils {

    public static String truncateString(String str) {
        return str.substring(0, Math.min(15, str.length()));
    }
}
