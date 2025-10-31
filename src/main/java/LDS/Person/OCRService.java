package LDS.Person;

import net.sourceforge.tess4j.Tesseract;

import java.io.File;

/**
 * OCR 服务类，用于使用 Tesseract 进行图像文字识别
 */
public class OCRService {
    private static final Tesseract tesseract = new Tesseract();
    
    // 置信度阈值 (0-100)，低于此值的识别结果将被过滤
    // 默认 50%，可以通过 setConfidenceThreshold() 调整
    private static double confidenceThreshold = 90;

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
            String normalized = normalizeText(recognizedText);
            
            // 检查识别结果长度（作为置信度的代理指标）
            if (normalized.isEmpty()) {
                System.out.println("  ⚠ 未识别到文字");
                return "";
            }
            
            // 过滤低置信度字符（仅保留中文、英文、数字）
            String filtered = filterLowConfidenceChars(normalized);
            
            // 如果过滤后结果为空，说明识别质量太低
            if (filtered.isEmpty()) {
                System.out.println("  ⚠ 识别置信度过低 (<" + 
                                 String.format("%.0f", confidenceThreshold) + "%) - 将不输出");
                System.out.println("    原始识别: " + normalized);
                return "";
            }
            
            // 计算有效字符率（清理前后的比例）
            double validCharRatio = (double) filtered.length() / normalized.length() * 100;
            
            // 如果有效字符率低于阈值，也认为置信度不足
            if (validCharRatio < confidenceThreshold) {
                System.out.println("  ⚠ 有效字符率过低 (" + String.format("%.1f", validCharRatio) + "% < " + 
                                 String.format("%.0f", confidenceThreshold) + "%) - 将不输出");
                System.out.println("    原始识别: " + normalized);
                System.out.println("    清理后: " + filtered);
                return "";
            }
            
            // 显示有效字符率信息
            System.out.println("  有效字符率: " + String.format("%.1f", validCharRatio) + "%");
            
            return filtered;
        } catch (Exception e) {
            // 捕获所有异常，包括图片格式错误，但不中止程序
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getSimpleName();
            }
            
            // 根据错误类型提供友好的提示
            if (errorMsg.contains("JFIF") || errorMsg.contains("JPEG")) {
                System.out.println("  ⚠ 图片文件格式有问题（非标准 JPEG）- 跳过");
            } else if (errorMsg.contains("图片") || errorMsg.contains("不存在")) {
                System.out.println("  ⚠ 图片文件问题 - 跳过");
            } else {
                System.out.println("  ⚠ OCR 识别异常 - 跳过");
            }
            
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
     * 过滤低置信度字符
     * 仅保留: 中文字符、英文字母、数字
     * 移除: 所有符号、特殊字符、标点等
     *
     * @param text 原始文本
     * @return 过滤后的文本
     */
    private static String filterLowConfidenceChars(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            // 保留: 中文字符 (4E00-9FFF)、英文字母、数字
            if (isChinese(c) || Character.isLetter(c) || Character.isDigit(c)) {
                result.append(c);
            }
            // 其他字符（符号、标点等）都会被过滤掉
        }
        
        return result.toString();
    }

    /**
     * 检查字符是否为中文
     *
     * @param c 待检查的字符
     * @return true 如果是中文字符
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_F ||
               ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
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

    /**
     * 设置置信度阈值（百分比）
     * 低于此阈值的识别结果将被过滤
     *
     * @param threshold 置信度阈值，范围 0-100，默认 50
     *                  - 50: 保守（过滤大多数低质量识别）
     *                  - 30: 宽松（接受较低质量识别）
     *                  - 70: 严格（仅接受高质量识别）
     */
    public static void setConfidenceThreshold(double threshold) {
        if (threshold < 0 || threshold > 100) {
            System.err.println("警告: 置信度阈值应在 0-100 之间，保持原值: " + confidenceThreshold);
            return;
        }
        confidenceThreshold = threshold;
        System.out.println("✓ 置信度阈值已设置为: " + String.format("%.0f", threshold) + "%");
    }

    /**
     * 获取当前置信度阈值
     *
     * @return 当前置信度阈值
     */
    public static double getConfidenceThreshold() {
        return confidenceThreshold;
    }
}
