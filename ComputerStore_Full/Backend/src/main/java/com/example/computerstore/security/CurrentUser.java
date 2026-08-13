package com.example.computerstore.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper để controller lấy người dùng đang đăng nhập (do AuthTokenFilter gắn
 * vào SecurityContext) và áp các quy tắc "chính chủ hoặc admin" mà một mình
 * URL-matcher trong SecurityConfig không diễn tả được (vd: khách được sửa hồ
 * sơ của CHÍNH MÌNH, nhưng không được sửa hồ sơ người khác trừ khi là admin).
 */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static UserPrincipal get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Bạn cần đăng nhập để thực hiện thao tác này!");
        }
        return principal;
    }

    /** Cho phép nếu là admin, hoặc nếu targetUserId chính là bản thân người gọi. */
    public static void requireSelfOrAdmin(Long targetUserId) {
        UserPrincipal me = get();
        if (!me.isAdmin() && !me.isSelf(targetUserId)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác trên tài khoản này!");
        }
    }

    /** Cho phép nếu là staff/admin, hoặc nếu resourceOwnerId chính là bản thân người gọi. */
    public static void requireSelfOrStaff(Long resourceOwnerId) {
        UserPrincipal me = get();
        if (!me.isAdminOrStaff() && !me.isSelf(resourceOwnerId)) {
            throw new AccessDeniedException("Bạn không có quyền xem/thao tác đơn hàng này!");
        }
    }
}
