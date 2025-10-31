package LDS.Person;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件恢复工具类
 * 用于读取 OldNames.txt 并将 IMG 文件夹中的文件恢复为原始文件名
 */
public class FileRestorer {
    private static final String OLD_NAMES_FILE = "OldNames.txt";
    private static final String SEPARATOR = "/";

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    文件名恢复工具 - File Restorer");
        System.out.println("========================================");
        System.out.println();

        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        System.out.println("项目根目录: " + projectRoot);
        System.out.println();

        // 读取 OldNames.txt 文件
        String oldNamesPath = projectRoot + File.separator + OLD_NAMES_FILE;
        List<String> originalFileNames = readOldFileNames(oldNamesPath);

        if (originalFileNames.isEmpty()) {
            System.out.println("✗ 无法读取原始文件名");
            System.out.println("  请确保 " + OLD_NAMES_FILE + " 文件存在且不为空");
            return;
        }

        System.out.println("✓ 已读取原始文件名列表:");
        System.out.println("  共 " + originalFileNames.size() + " 个文件");
        for (int i = 0; i < originalFileNames.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + originalFileNames.get(i));
        }
        System.out.println();

        // 扫描 IMG 文件夹中的所有文件
        String imgFolder = projectRoot + File.separator + "IMG";
        List<File> currentFiles = scanIMGFolder(imgFolder);

        if (currentFiles.isEmpty()) {
            System.out.println("✗ IMG 文件夹中没有找到任何文件");
            return;
        }

        System.out.println("✓ 当前 IMG 文件夹中的文件:");
        System.out.println("  共 " + currentFiles.size() + " 个文件");
        for (int i = 0; i < currentFiles.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + currentFiles.get(i).getName());
        }
        System.out.println();

        // 检查数量是否匹配
        if (currentFiles.size() != originalFileNames.size()) {
            System.out.println("⚠ 警告: 当前文件数量 (" + currentFiles.size() + 
                             ") 与原始文件数量 (" + originalFileNames.size() + ") 不匹配");
            System.out.println();
        }

        // 执行文件恢复
        System.out.println("========================================");
        System.out.println("       开始恢复文件名...");
        System.out.println("========================================");
        System.out.println();

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < currentFiles.size() && i < originalFileNames.size(); i++) {
            File currentFile = currentFiles.get(i);
            String originalFileName = originalFileNames.get(i);

            System.out.println("[" + (i + 1) + "/" + currentFiles.size() + "]");
            System.out.println("  当前文件名: " + currentFile.getName());
            System.out.println("  目标文件名: " + originalFileName);

            if (restoreFileName(currentFile, originalFileName)) {
                System.out.println("  ✓ 恢复成功");
                successCount++;
            } else {
                System.out.println("  ✗ 恢复失败");
                failCount++;
            }
            System.out.println();
        }

        // 输出统计信息
        System.out.println("========================================");
        System.out.println("       恢复完成");
        System.out.println("========================================");
        System.out.println("✓ 成功: " + successCount + " 个文件");
        System.out.println("✗ 失败: " + failCount + " 个文件");
        System.out.println();
    }

    /**
     * 读取 OldNames.txt 文件并解析原始文件名列表
     *
     * @param oldNamesPath OldNames.txt 文件路径
     * @return 原始文件名列表
     */
    private static List<String> readOldFileNames(String oldNamesPath) {
        List<String> fileNames = new ArrayList<>();

        try {
            Path path = Paths.get(oldNamesPath);
            if (!Files.exists(path)) {
                System.err.println("✗ " + OLD_NAMES_FILE + " 文件不存在: " + oldNamesPath);
                return fileNames;
            }

            // 读取文件内容
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

            if (content.trim().isEmpty()) {
                System.err.println("✗ " + OLD_NAMES_FILE + " 文件为空");
                return fileNames;
            }

            // 按照 "/" 分割符解析文件名
            String[] names = content.split(SEPARATOR);
            for (String name : names) {
                String trimmedName = name.trim();
                if (!trimmedName.isEmpty()) {
                    fileNames.add(trimmedName);
                }
            }

        } catch (IOException e) {
            System.err.println("✗ 读取 " + OLD_NAMES_FILE + " 失败");
            e.printStackTrace();
        }

        return fileNames;
    }

    /**
     * 扫描 IMG 文件夹中的所有文件
     *
     * @param imgFolder IMG 文件夹路径
     * @return 文件列表
     */
    private static List<File> scanIMGFolder(String imgFolder) {
        List<File> files = new ArrayList<>();

        File directory = new File(imgFolder);
        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("✗ IMG 文件夹不存在: " + imgFolder);
            return files;
        }

        File[] fileArray = directory.listFiles();
        if (fileArray != null) {
            for (File file : fileArray) {
                if (file.isFile()) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    /**
     * 将文件恢复为原始文件名
     *
     * @param currentFile       当前文件
     * @param originalFileName  原始文件名
     * @return 是否恢复成功
     */
    private static boolean restoreFileName(File currentFile, String originalFileName) {
        try {
            File parentDir = currentFile.getParentFile();
            File newFile = new File(parentDir, originalFileName);

            // 如果目标文件名和当前文件名相同，则不需要重命名
            if (currentFile.getName().equals(originalFileName)) {
                System.out.println("  (文件名已相同，无需恢复)");
                return true;
            }

            // 检查目标文件名是否已经存在（且不是当前文件）
            if (newFile.exists() && !newFile.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
                System.err.println("  目标文件名已存在: " + originalFileName);
                return false;
            }

            // 执行重命名
            if (currentFile.renameTo(newFile)) {
                return true;
            } else {
                System.err.println("  无法重命名文件");
                return false;
            }

        } catch (Exception e) {
            System.err.println("  恢复失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
