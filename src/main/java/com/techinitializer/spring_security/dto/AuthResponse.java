package com.techinitializer.spring_security.dto;

public record AuthResponse(String accessToken, String refreshToken) {
}
