package LDS.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件重命名工具类
 * 用于记录原始文件名和根据 OCR 识别结果重命名文件
 */
public class FileRenamer {
    private static final String OLD_NAMES_FILE = "OldNames.txt";
    private static final String SEPARATOR = "/";

    /**
     * 记录 IMG 文件夹内所有文件的原始文件名到 OldNames.txt
     *
     * @param projectRoot 项目根目录
     * @param imageFiles  图片文件列表
     * @return 是否成功记录
     */
    public static boolean recordOldFileNames(String projectRoot, List<File> imageFiles) {
        try {
            // 创建 OldNames.txt 文件路径
            String oldNamesPath = projectRoot + File.separator + OLD_NAMES_FILE;
            File oldNamesFile = new File(oldNamesPath);

            // 删除旧的文件（如果存在）
            if (oldNamesFile.exists()) {
                if (!oldNamesFile.delete()) {
                    System.err.println("警告: 无法删除旧的 OldNames.txt 文件");
                }
            }

            // 写入所有文件名
            try (FileWriter writer = new FileWriter(oldNamesFile, StandardCharsets.UTF_8)) {
                List<String> fileNames = new ArrayList<>();
                for (File file : imageFiles) {
                    fileNames.add(file.getName());
                }

                // 使用"/"分割符连接所有文件名
                String content = String.join(SEPARATOR, fileNames);
                writer.write(content);
                writer.flush();

                System.out.println("✓ 已记录原始文件名到: " + oldNamesPath);
                System.out.println("  文件数量: " + imageFiles.size());
                System.out.println("  内容: " + content);
                System.out.println();

                return true;
            }
        } catch (IOException e) {
            System.err.println("记录文件名失败");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据 OCR 识别结果重命名文件
     * 保留原始文件后缀，自动移除非法字符
     *
     * @param imageFile      原始文件
     * @param recognizedText OCR 识别的文本
     * @return 是否成功重命名
     */
    public static boolean renameFileWithOCRResult(File imageFile, String recognizedText) {
        if (recognizedText == null || recognizedText.isEmpty()) {
            System.err.println("  ✗ 重命名失败: 识别结果为空");
            return false;
        }

        try {
            // 获取文件的后缀名
            String fileName = imageFile.getName();
            int lastDotIndex = fileName.lastIndexOf('.');
            String fileExtension = (lastDotIndex > 0) ? fileName.substring(lastDotIndex) : "";

            // 清理文件名中的非法字符
            String cleanedText = cleanInvalidChars(recognizedText);

            if (cleanedText.isEmpty()) {
                System.err.println("  ✗ 重命名失败: 识别结果中无有效字符");
                return false;
            }

            // 创建新文件名
            String newFileName = cleanedText + fileExtension;

            // 创建新文件对象
            File parentDir = imageFile.getParentFile();
            File newFile = new File(parentDir, newFileName);

            // 检查新文件名是否已经存在
            if (newFile.exists() && !newFile.getAbsolutePath().equals(imageFile.getAbsolutePath())) {
                System.err.println("  ✗ 重命名失败: 新文件名已存在 - " + newFileName);
                return false;
            }

            // 重命名文件
            if (imageFile.renameTo(newFile)) {
                System.out.println("  ✓ 重命名成功");
                System.out.println("    原文件名: " + fileName);
                System.out.println("    新文件名: " + newFileName);
                if (!recognizedText.equals(cleanedText)) {
                    System.out.println("    (已自动移除非法字符)");
                }
                return true;
            } else {
                System.err.println("  ✗ 重命名失败: 无法重命名文件");
                return false;
            }
        } catch (Exception e) {
            System.err.println("  ✗ 重命名失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清理文件名中的非法字符
     * Windows 不允许的字符: < > : " / \ | ? *
     *
     * @param text 原始文本
     * @return 清理后的文本（移除所有非法字符）
     */
    private static String cleanInvalidChars(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 移除 Windows 不允许的所有字符: < > : " / \ | ? *
        return text.replaceAll("[<>:\"/\\\\|?*]", "");
    }

    /**
     * 获取 OldNames.txt 中的内容
     *
     * @param projectRoot 项目根目录
     * @return OldNames.txt 的内容，如果文件不存在则返回空字符串
     */
    public static String getOldNamesContent(String projectRoot) {
        try {
            String oldNamesPath = projectRoot + File.separator + OLD_NAMES_FILE;
            Path path = Paths.get(oldNamesPath);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path));
            }
        } catch (IOException e) {
            System.err.println("读取 OldNames.txt 失败");
            e.printStackTrace();
        }
        return "";
    }
}
