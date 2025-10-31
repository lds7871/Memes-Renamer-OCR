package LDS.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * OCR 配置加载器
 * 从 ocr.properties 文件中读取配置
 */
public class OcrConfig {
    private static final String CONFIG_FILE = "ocr.properties";
    private static final Properties properties = new Properties();
    
    // 默认值常量
    private static final String DEFAULT_LANGUAGE = "chi_sim+eng";
    private static final double DEFAULT_CONFIDENCE_THRESHOLD = 50.0;
    private static final boolean DEFAULT_ENABLE_CHAR_FILTER = true;
    private static final boolean DEFAULT_ENABLE_VERBOSE = false;

    static {
        loadConfig();
    }

    /**
     * 加载配置文件
     */
    private static void loadConfig() {
        try (InputStream input = OcrConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("警告: 未找到 " + CONFIG_FILE + " 文件，使用默认配置");
                return;
            }
            
            properties.load(input);
            System.out.println("✓ 已加载 OCR 配置文件");
            
        } catch (IOException e) {
            System.err.println("错误: 无法读取 " + CONFIG_FILE + " 文件");
            e.printStackTrace();
        }
    }

    /**
     * 获取 OCR 识别语言
     *
     * @return 语言代码（如 "chi_sim", "eng", "chi_sim+eng"）
     */
    public static String getLanguage() {
        return properties.getProperty("ocr.language", DEFAULT_LANGUAGE);
    }

    /**
     * 获取置信度阈值
     *
     * @return 置信度阈值（0-100）
     */
    public static double getConfidenceThreshold() {
        String threshold = properties.getProperty("ocr.confidence.threshold", 
                                                 String.valueOf(DEFAULT_CONFIDENCE_THRESHOLD));
        try {
            double value = Double.parseDouble(threshold);
            // 检查范围
            if (value < 0 || value > 100) {
                System.err.println("警告: 置信度阈值超出范围 (0-100)，使用默认值 " + DEFAULT_CONFIDENCE_THRESHOLD);
                return DEFAULT_CONFIDENCE_THRESHOLD;
            }
            return value;
        } catch (NumberFormatException e) {
            System.err.println("警告: 置信度阈值格式错误，使用默认值 " + DEFAULT_CONFIDENCE_THRESHOLD);
            return DEFAULT_CONFIDENCE_THRESHOLD;
        }
    }

    /**
     * 是否启用字符过滤
     *
     * @return true 表示启用（仅保留中文、英文、数字）
     */
    public static boolean isEnableCharFilter() {
        String value = properties.getProperty("ocr.enable.char.filter", 
                                             String.valueOf(DEFAULT_ENABLE_CHAR_FILTER));
        return Boolean.parseBoolean(value);
    }

    /**
     * 是否启用详细日志
     *
     * @return true 表示启用详细输出
     */
    public static boolean isEnableVerbose() {
        String value = properties.getProperty("ocr.enable.verbose", 
                                            String.valueOf(DEFAULT_ENABLE_VERBOSE));
        return Boolean.parseBoolean(value);
    }

    /**
     * 显示当前配置信息
     */
    public static void printConfig() {
        System.out.println("========================================");
        System.out.println("       当前 OCR 配置");
        System.out.println("========================================");
        System.out.println("语言: " + getLanguage());
        System.out.println("置信度阈值: " + String.format("%.0f", getConfidenceThreshold()) + "%");
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * 重新加载配置文件
     */
    public static void reload() {
        properties.clear();
        loadConfig();
    }
}
