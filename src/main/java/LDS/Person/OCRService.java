package LDS.Person;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * OCR 服务类，用于使用 Tesseract 进行图像文字识别
 */
public class OCRService {
    private static final Tesseract tesseract = new Tesseract();

    static {
        // 设置 Tesseract 的 tessdata 路径
        // 使用项目根目录下的 tessdata 文件夹
        String projectRoot = System.getProperty("user.dir");
        String tessdataPath = projectRoot + File.separator + "tessdata";
        
        // 检查 tessdata 目录是否存在
        File tessdataDir = new File(tessdataPath);
        if (tessdataDir.exists() && tessdataDir.isDirectory()) {
            tesseract.setDatapath(tessdataPath);
            System.out.println("Tesseract 数据路径已设置为: " + tessdataPath);
        } else {
            System.err.println("警告: tessdata 目录不存在: " + tessdataPath);
            System.err.println("请确保在项目根目录下放置 tessdata 文件夹");
            System.err.println("或者将其放在系统的 Tesseract 安装目录中");
        }
    }

    /**
     * 识别单个图片文件中的文字
     *
     * @param imagePath 图片文件路径
     * @return 识别的文本（已规范化，去除空格和换行符）
     */
    public static String recognizeText(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("图片文件不存在: " + imagePath);
                return "";
            }

            // 检查文件格式是否支持
            if (!isSupportedFormat(imagePath)) {
                System.err.println("不支持的图片格式: " + imagePath);
                return "";
            }

            String recognizedText = tesseract.doOCR(imageFile);
            // 规范化文本：去除所有空格、换行符、制表符等空白字符
            return normalizeText(recognizedText);
        } catch (TesseractException e) {
            System.err.println("OCR识别失败 - " + imagePath);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 规范化识别的文本
     * 去除所有空白字符（空格、换行、制表符等），保持字符连续
     *
     * @param text 原始文本
     * @return 规范化后的文本
     */
    private static String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 使用正则表达式去除所有空白字符（包括空格、换行符 \n、回车符 \r、制表符 \t 等）
        return text.replaceAll("\\s+", "").trim();
    }

    /**
     * 检查文件是否为支持的图片格式
     *
     * @param filePath 文件路径
     * @return true 如果是支持的格式
     */
    private static boolean isSupportedFormat(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".png") ||
               lowerPath.endsWith(".jpg") ||
               lowerPath.endsWith(".jpeg") ||
               lowerPath.endsWith(".bmp") ||
               lowerPath.endsWith(".tiff") ||
               lowerPath.endsWith(".gif");
    }

    /**
     * 设置识别语言
     * 
     * @param language 语言代码 (例如: "chi_sim" 简体中文, "eng" 英文)
     */
    public static void setLanguage(String language) {
        tesseract.setLanguage(language);
    }
}
