# 🖼️ Memes Renamer - OCR 图片文字识别和自动重命名工具

基于 Tesseract-OCR 的 Java 项目，自动识别图片文字并用识别结果重命名文件，旨在方便管理和查找保存的memes（上千个memes保存了但想发的时候找不到的痛我相信你懂）。但Tesseract OCR的数据包较为古老，识别准确度并不是很高。

## ⚡ 快速开始 (5分钟)

### 前置要求
- **Java 17+**
- **Maven 3.6+**  
- **Tesseract-OCR** (默认已经安装简体中文以及英文，如需其他文件可自行下面一步)

### 1 安装 tessdata（可选）

- 从 [tesseract-ocr](https://github.com/tesseract-ocr/tessdata) 下载相应数据包
- 安装到 `根目录\tessdata`


### 2 运行程序

```powershell
# OCR识别 + 重命名
mvn clean compile exec:java@run-main

# 恢复原始文件名
mvn exec:java@run-restorer
```

## 🎯 核心功能

### Main - OCR识别和文件重命名

**命令:**
```powershell
mvn clean compile exec:java@run-main
```

**流程:**
1. 扫描 `IMG` 文件夹
2. 记录原始文件名到 `OldNames.txt` (用 `/` 分割)
3. OCR 识别每张图片
4. 重命名为识别结果
5. 自动清理非法字符


### FileRestorer - 恢复原始文件名

**命令:**
```powershell
mvn exec:java@run-restorer
```

**流程:**
1. 读取 `OldNames.txt` 中的原始文件名
2. 恢复 `IMG` 文件夹中的文件名
3. 显示恢复统计信息


## 📁 项目结构

```
MemesRenamer/
├── IMG/                    # 放图片这里
├── tessdata/               # OCR语言数据
├── OldNames.txt            # 原始文件名备份 (自动生成)
├── pom.xml
├── README.md
└── src/main/java/LDS/Person/
    ├── Main.java           # 主程序
    ├── FileRestorer.java   # 文件恢复工具
    ├── OCRService.java     # OCR服务
    ├── FileRenamer.java    # 文件重命名
    └── ImageScanner.java   # 文件扫描
```

## 🛠️ 核心类说明

| 类 | 功能 |
|----|------|
| `OCRService` | Tesseract OCR 识别，支持多语言 |
| `ImageScanner` | 扫描文件夹找图片 |
| `FileRenamer` | 记录原名，根据识别结果重命名 |
| `FileRestorer` | 从 OldNames.txt 恢复原始文件名 |
| `Main` | 主程序，协调 OCR 和重命名 |

## ⚙️ 配置修改

### 改语言

编辑 `Main.java` 第 26 行:
```java
OCRService.setLanguage("chi_sim");  // "eng", "chi_tra" 等
```

### 改图片文件夹

编辑 `Main.java` 第 31 行:
```java
String imgFolder = projectRoot + File.separator + "IMG";
```

### 改分割符

编辑 `FileRenamer.java` 第 13 行:
```java
private static final String SEPARATOR = ",";  // 改为逗号等
```

## 📝 支持的格式

- **图片:** `.png`, `.jpg`, `.jpeg`, `.bmp`, `.tiff`, `.gif`
- **语言:** 英文、简体中文、繁体中文 等

## 🚫 自动清理的非法字符

```
< > : " / \ | ? *
```

## 🔗 相关资源

- [Tesseract 官方](https://github.com/tesseract-ocr/tesseract)
- [Tess4J 官方](https://tess4j.sourceforge.net/)
- [语言数据下载](https://github.com/tesseract-ocr/tessdata)

##  第三方组件
| 名称 | 仓库/链接  | 许可证（SPDX）  | 用途         |
|------|----------|------------|------------|
| tesseract-ocr | https://github.com/tesseract-ocr/tessdata | Apache-2.0 | 数据包的图片识别支持 |

---

**更新:** 2025年10月31日
