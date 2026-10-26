package com.example.shortlink.shared.utils;

import com.example.shortlink.common.exception.AppException;

import java.net.URI;

import static com.example.shortlink.shared.ErrorCode.INVALID_URL;

public class UrlUtils {

    public static void isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new AppException(INVALID_URL);
        }
        URI uri = URI.create(url);
        try {
            if ( uri.getScheme() == null ||
                    (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https"))) {
                throw new AppException(INVALID_URL);
            }
        } catch (Exception e) {
            throw new AppException(INVALID_URL);
        }

    }

}
