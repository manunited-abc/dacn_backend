package nlu.dacn.dacn_backend.utils;

import javax.servlet.http.HttpServletRequest;

public class TokenUtils {
    public static String getTokenFromHeader(String authHeader) {
        if (null != authHeader && authHeader.startsWith("Bearer")) {
            return authHeader.replace("Bearer", "");
        }
        return null;
    }
}
