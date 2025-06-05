//package com.example.logmanagementsystem.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//@ConfigurationProperties(prefix = "app")
//public class AppConfig {
//    private String username;
//    private String password;
//    private List<String> logFolders;
//    private String baseFolderPath;
//    private String uploadPath;
//    private boolean uploadEnabled;
//
//    // Getters and Setters
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public List<String> getLogFolders() {
//        return logFolders;
//    }
//
//    public void setLogFolders(List<String> logFolders) {
//        this.logFolders = logFolders;
//    }
//
//    public String getBaseFolderPath() {
//        return baseFolderPath;
//    }
//
//    public void setBaseFolderPath(String baseFolderPath) {
//        this.baseFolderPath = baseFolderPath;
//    }
//
//    public String getUploadPath() {
//        return uploadPath;
//    }
//
//    public void setUploadPath(String uploadPath) {
//        this.uploadPath = uploadPath;
//    }
//
//    public boolean isUploadEnabled() {
//        return uploadEnabled;
//    }
//
//    public void setUploadEnabled(boolean uploadEnabled) {
//        this.uploadEnabled = uploadEnabled;
//    }
//}


package com.example.logmanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String username;
    private String password;
    private List<String> baseFolderPaths;
    private String uploadPath;
    private boolean uploadEnabled;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getBaseFolderPaths() {
        return baseFolderPaths;
    }

    public void setBaseFolderPaths(List<String> baseFolderPaths) {
        this.baseFolderPaths = baseFolderPaths;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public boolean isUploadEnabled() {
        return uploadEnabled;
    }

    public void setUploadEnabled(boolean uploadEnabled) {
        this.uploadEnabled = uploadEnabled;
    }
}