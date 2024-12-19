package org.mentalk.common.dto;

public record ApiResponse(boolean success, Object payload) {

    private record Success<T>(String message, T data) {
    }

    private record Error(String code, String message) {
    }

    public static <T> ApiResponse success(String message, T data) {
        return new ApiResponse(true, new Success<>(message, data));
    }

    public static <T> ApiResponse success(T data) {
        return new ApiResponse(true, new Success<>("success", data));
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, new Success<>(message, null));
    }

    public static ApiResponse failure(String code, String message) {
        return new ApiResponse(false, new Error(code, message));
    }
}
