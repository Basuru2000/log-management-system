//package com.example.logmanagementsystem.service;
//
//import com.example.logmanagementsystem.config.AppConfig;
//import com.example.logmanagementsystem.model.LogFile;
//import com.example.logmanagementsystem.util.ZipUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class FileService {
//
//    @Autowired
//    private AppConfig appConfig;
//
//    public List<String> getLogFolders() {
//        return appConfig.getLogFolders();
//    }
//
//    public List<LogFile> getFilesInFolder(String folderName) throws IOException {
//        String folderPath = appConfig.getBaseFolderPath() + "/" + folderName;
//        Path path = Paths.get(folderPath);
//
//        if (!Files.exists(path)) {
//            return new ArrayList<>();
//        }
//
//        return Files.list(path)
//                .map(this::convertToLogFile)
//                .collect(Collectors.toList());
//    }
//
//    private LogFile convertToLogFile(Path path) {
//        try {
//            File file = path.toFile();
//            return new LogFile(
//                    file.getName(),
//                    file.getAbsolutePath(),
//                    file.length(),
//                    LocalDateTime.ofInstant(
//                            Files.getLastModifiedTime(path).toInstant(),
//                            ZoneId.systemDefault()
//                    ),
//                    file.isDirectory()
//            );
//        } catch (IOException e) {
//            throw new RuntimeException("Error reading file: " + path, e);
//        }
//    }
//
//    public Resource downloadFile(String folderName, String fileName) throws IOException {
//        String filePath = appConfig.getBaseFolderPath() + "/" + folderName + "/" + fileName;
//        File file = new File(filePath);
//
//        if (!file.exists()) {
//            throw new IOException("File not found: " + fileName);
//        }
//
//        // Check if file is already a zip file
//        if (fileName.toLowerCase().endsWith(".zip")) {
//            return new FileSystemResource(file);
//        }
//
//        // Create zip file
//        String zipFilePath = filePath + ".zip";
//        ZipUtil.createZipFile(filePath, zipFilePath);
//
//        return new FileSystemResource(new File(zipFilePath));
//    }
//
//    public boolean isValidFolder(String folderName) {
//        return appConfig.getLogFolders().contains(folderName);
//    }
//}

package com.example.logmanagementsystem.service;

import com.example.logmanagementsystem.config.AppConfig;
import com.example.logmanagementsystem.model.LogFile;
import com.example.logmanagementsystem.util.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private AppConfig appConfig;

    public List<String> getLogFolders() {
        List<String> folders = new ArrayList<>();

        // Iterate over all base folder paths
        for (String basePath : appConfig.getBaseFolderPaths()) {
            Path baseDir = Paths.get(basePath);
            if (Files.exists(baseDir) && Files.isDirectory(baseDir)) {
                try {
                    folders.addAll(Files.list(baseDir)
                            .filter(Files::isDirectory)
                            .map(path -> path.getFileName().toString())
                            .collect(Collectors.toList()));
                } catch (IOException e) {
                    System.out.println("Warning: Unable to read folders from " + basePath + ": " + e.getMessage());
                }
            }
        }

        // Remove duplicates and sort
        return folders.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<LogFile> getFilesInFolder(String folderName) throws IOException {
        List<LogFile> allFiles = new ArrayList<>();

        // Iterate over all base folder paths
        for (String basePath : appConfig.getBaseFolderPaths()) {
            String folderPath = basePath + "/" + folderName;
            Path path = Paths.get(folderPath);

            if (Files.exists(path) && Files.isReadable(path)) {
                List<LogFile> files = Files.list(path)
                        .map(this::convertToLogFile)
                        .collect(Collectors.toList());
                allFiles.addAll(files);
            }
        }

        // Remove duplicates by file name, keeping the most recent
        return allFiles.stream()
                .collect(Collectors.toMap(
                        LogFile::getName,
                        file -> file,
                        (existing, replacement) -> existing.getLastModified().isAfter(replacement.getLastModified()) ? existing : replacement
                ))
                .values()
                .stream()
                .sorted((f1, f2) -> f1.getName().compareTo(f2.getName()))
                .collect(Collectors.toList());
    }

    private LogFile convertToLogFile(Path path) {
        try {
            File file = path.toFile();
            return new LogFile(
                    file.getName(),
                    file.getAbsolutePath(),
                    file.length(),
                    LocalDateTime.ofInstant(
                            Files.getLastModifiedTime(path).toInstant(),
                            ZoneId.systemDefault()
                    ),
                    file.isDirectory()
            );
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + path, e);
        }
    }

    public Resource downloadFile(String folderName, String fileName) throws IOException {
        // Try each base path until the file is found
        for (String basePath : appConfig.getBaseFolderPaths()) {
            String filePath = basePath + "/" + folderName + "/" + fileName;
            File file = new File(filePath);

            if (file.exists()) {
                // Check if file is already a zip file
                if (fileName.toLowerCase().endsWith(".zip")) {
                    return new FileSystemResource(file);
                }

                // Create zip file
                String zipFilePath = filePath + ".zip";
                ZipUtil.createZipFile(filePath, zipFilePath);

                return new FileSystemResource(new File(zipFilePath));
            }
        }
        throw new IOException("File not found: " + fileName);
    }

    public boolean isValidFolder(String folderName) {
        // Check if folder exists in any base path
        for (String basePath : appConfig.getBaseFolderPaths()) {
            Path folderPath = Paths.get(basePath, folderName);
            if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
                return true;
            }
        }
        return false;
    }
}