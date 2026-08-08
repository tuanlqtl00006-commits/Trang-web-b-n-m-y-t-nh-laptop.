package com.example.computerstore.config;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Tính đường dẫn TUYỆT ĐỐI tới thư mục "uploads" dựa trên vị trí thực tế
 * của class đang chạy (target/classes khi chạy từ IDE), thay vì dùng
 * đường dẫn tương đối "uploads/" phụ thuộc vào "Working directory" mà
 * IntelliJ/terminal đang đứng — nguồn gốc của lỗi 404 ảnh trước đây.
 */
public class UploadUtil {

    private static final File UPLOADS_DIR = resolveUploadsDir();

    private static File resolveUploadsDir() {
        try {
            File classesDir = new File(
                    UploadUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            // Khi chạy từ IDE: .../Backend/target/classes -> lùi lên 2 cấp để ra .../Backend
            // Khi chạy từ jar: .../Backend/target -> lùi lên 1 cấp để ra .../Backend
            File backendDir = classesDir.getName().equals("classes")
                    ? classesDir.getParentFile().getParentFile()
                    : classesDir.getParentFile();

            File uploads = new File(backendDir, "uploads");
            if (!uploads.exists()) {
                uploads.mkdirs();
            }
            System.out.println("=== [UploadUtil] Thư mục uploads được xác định là: "
                    + uploads.getAbsolutePath() + " ===");
            return uploads;
        } catch (URISyntaxException e) {
            File fallback = new File("uploads").getAbsoluteFile();
            System.out.println("=== [UploadUtil] Không xác định được vị trí code, dùng fallback: "
                    + fallback.getAbsolutePath() + " ===");
            return fallback;
        }
    }

    public static File getUploadsDir() {
        return UPLOADS_DIR;
    }

    public static String getUploadsAbsolutePath() {
        return UPLOADS_DIR.getAbsolutePath();
    }
}
