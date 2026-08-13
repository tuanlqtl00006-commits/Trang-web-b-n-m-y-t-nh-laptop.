package com.example.computerstore.util;

import java.util.regex.Pattern;

/**
 * Shared server-side validation helpers.
 * These mirror the client-side checks so the API stays safe even if a
 * request bypasses the UI (e.g. called directly).
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Vietnamese mobile numbers: exactly 10 digits, starting with 0.
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    public static final double MIN_PRODUCT_PRICE = 9_000_000d;   // 9 triệu VND
    public static final double MAX_PRODUCT_PRICE = 300_000_000d; // 300 triệu VND
    public static final int MAX_PRODUCT_STOCK = 200; // cửa hàng nhỏ, tồn kho không nhiều

    // Chỉ chấp nhận 2 hệ điều hành cho máy tính bán ra: Windows 10 / Windows 11.
    private static final java.util.Set<String> ALLOWED_OS =
            java.util.Set.of("Windows 10", "Windows 11");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void requireValidEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email không đúng định dạng (VD: ten@example.com)!");
        }
    }

    public static void requireValidPhone(String phone) {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Số điện thoại phải gồm đúng 10 chữ số và bắt đầu bằng 0!");
        }
    }

    public static void requireValidPrice(Double price) {
        if (price == null) {
            throw new IllegalArgumentException("Vui lòng nhập giá bán!");
        }
        if (price < MIN_PRODUCT_PRICE || price > MAX_PRODUCT_PRICE) {
            throw new IllegalArgumentException(
                    "Giá bán phải trong khoảng 9.000.000đ - 300.000.000đ!");
        }
    }

    public static void requireValidOs(String os) {
        if (isBlank(os) || !ALLOWED_OS.contains(os.trim())) {
            throw new IllegalArgumentException("Hệ điều hành chỉ được chọn Windows 10 hoặc Windows 11!");
        }
    }

    public static void requireValidStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm!");
        }
        if (stock > MAX_PRODUCT_STOCK) {
            throw new IllegalArgumentException("Số lượng tồn kho vượt quá giới hạn cho phép (" + MAX_PRODUCT_STOCK + ")!");
        }
    }

    public static void requireNotBlank(String value, String fieldLabel) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Vui lòng nhập " + fieldLabel + "!");
        }
    }
}
