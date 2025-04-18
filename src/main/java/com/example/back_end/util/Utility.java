package com.example.back_end.util;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
    public static final String CLIENT_SITE_URL = "http://localhost:3030";

    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

}
