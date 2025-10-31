package LDS.Person;

import java.io.File;
import java.util.List;

/**
 * 主程序入口类
 * 演示如何使用 OCRService 和 ImageScanner 进行图片文字识别
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       Memes Renamer - OCR 工具");
        System.out.println("========================================");
        System.out.println();

        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        System.out.println("项目根目录: " + projectRoot);
        System.out.println();

        // 设置 OCR 语言
        // 可选语言: "eng" (英文), "chi_sim" (简体中文), "chi_tra" (繁体中文) 等
        // 注意: 首次使用时需要自动下载相应的语言数据文件
        OCRService.setLanguage("chi_sim");
        System.out.println("OCR 语言已设置为: chi_sim (简体中文)");
        System.out.println();

        // 扫描 IMG 文件夹中的所有图片
        String imgFolder = projectRoot + File.separator + "IMG";
        System.out.println("正在扫描 IMG 文件夹中的图片文件...");
        List<File> imageFiles = ImageScanner.scanImages(imgFolder);

        if (imageFiles.isEmpty()) {
            System.out.println("未找到任何支持的图片文件。");
            System.out.println("支持的格式: .png, .jpg, .jpeg, .bmp, .tiff, .gif");
            return;
        }

        System.out.println("找到 " + imageFiles.size() + " 个图片文件:");
        System.out.println();

        // 记录原始文件名
        System.out.println("========================================");
        System.out.println("       第一步: 记录原始文件名");
        System.out.println("========================================");
        FileRenamer.recordOldFileNames(projectRoot, imageFiles);
        System.out.println();

        // 对每个找到的图片进行 OCR 识别
        for (int i = 0; i < imageFiles.size(); i++) {
            File imageFile = imageFiles.get(i);
            System.out.println("-----------------------------------");
            System.out.println("[" + (i + 1) + "/" + imageFiles.size() + "] " + imageFile.getName());
            System.out.println("路径: " + imageFile.getAbsolutePath());
            System.out.println("大小: " + imageFile.length() + " 字节");

            // 执行 OCR 识别
            System.out.println("正在识别文字...");
            long startTime = System.currentTimeMillis();
            String recognizedText = OCRService.recognizeText(imageFile.getAbsolutePath());
            long duration = System.currentTimeMillis() - startTime;

            if (recognizedText.isEmpty()) {
                System.out.println("未识别到文字或识别失败");
            } else {
                System.out.println("识别结果 (耗时: " + duration + "ms):");
                System.out.println("字符数: " + recognizedText.length());
                System.out.println("内容: " + recognizedText);
                System.out.println();

                // 根据识别结果重命名文件
                System.out.println("正在重命名文件...");
                FileRenamer.renameFileWithOCRResult(imageFile, recognizedText);
            }
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("       OCR 识别和文件重命名完成");
        System.out.println("========================================");
        System.out.println();
        System.out.println("📝 原始文件名已保存到: " + projectRoot + File.separator + "OldNames.txt");
    }
}