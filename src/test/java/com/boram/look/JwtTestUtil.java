package com.boram.look;

import com.boram.look.global.security.JwtProvider;

import java.util.List;

public class JwtTestUtil {
    public static String createToken(
            JwtProvider jwtProvider,
            String username,
            String userId,
            String roleString
    ) {
        return "Bearer " + jwtProvider.createAccessToken(username, userId, roleString);
    }
}
