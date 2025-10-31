package LDS.Person;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片扫描工具类
 * 用于递归扫描指定目录下的所有支持的图片文件
 */
public class ImageScanner {
    private static final String[] SUPPORTED_FORMATS = {".png", ".jpg", ".jpeg", ".bmp", ".tiff", ".gif"};

    /**
     * 扫描指定目录下的所有支持的图片文件
     *
     * @param directoryPath 目录路径
     * @return 找到的图片文件列表
     */
    public static List<File> scanImages(String directoryPath) {
        List<File> imageFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("目录不存在或不是目录: " + directoryPath);
            return imageFiles;
        }

        scanImagesRecursive(directory, imageFiles);
        return imageFiles;
    }

    /**
     * 递归扫描目录中的图片文件
     *
     * @param directory  当前目录
     * @param imageFiles 存储找到的图片文件的列表
     */
    private static void scanImagesRecursive(File directory, List<File> imageFiles) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanImagesRecursive(file, imageFiles);
            } else if (isSupportedFormat(file.getName())) {
                imageFiles.add(file);
            }
        }
    }

    /**
     * 检查文件是否为支持的图片格式
     *
     * @param fileName 文件名
     * @return true 如果是支持的格式
     */
    private static boolean isSupportedFormat(String fileName) {
        String lowerName = fileName.toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (lowerName.endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 扫描项目根目录的所有支持的图片文件
     *
     * @return 找到的图片文件列表
     */
    public static List<File> scanProjectRootImages() {
        return scanImages(System.getProperty("user.dir"));
    }
}
