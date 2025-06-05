//package com.example.logmanagementsystem.controller;
//
//import com.example.logmanagementsystem.model.LogFile;
//import com.example.logmanagementsystem.service.AuthService;
//import com.example.logmanagementsystem.service.FileService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
//@Controller
//public class DashboardController {
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private FileService fileService;
//
//    @GetMapping("/dashboard")
//    public String dashboard(HttpSession session, Model model) {
//        if (!authService.isLoggedIn(session)) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("folders", fileService.getLogFolders());
//        return "dashboard";
//    }
//
//    @GetMapping("/folder/{folderName}")
//    public String viewFolder(@PathVariable String folderName,
//                             HttpSession session,
//                             Model model) {
//        if (!authService.isLoggedIn(session)) {
//            return "redirect:/login";
//        }
//
//        if (!fileService.isValidFolder(folderName)) {
//            return "redirect:/dashboard?error=Invalid folder";
//        }
//
//        try {
//            List<LogFile> files = fileService.getFilesInFolder(folderName);
//            model.addAttribute("files", files);
//            model.addAttribute("folderName", folderName);
//            return "folder-view";
//        } catch (IOException e) {
//            model.addAttribute("error", "Error reading folder: " + e.getMessage());
//            return "dashboard";
//        }
//    }
//
//    @GetMapping("/download/{folderName}/{fileName}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String folderName,
//                                                 @PathVariable String fileName,
//                                                 HttpSession session) {
//        if (!authService.isLoggedIn(session)) {
//            return ResponseEntity.status(401).build();
//        }
//
//        if (!fileService.isValidFolder(folderName)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        try {
//            Resource resource = fileService.downloadFile(folderName, fileName);
//            String downloadFileName = fileName.endsWith(".zip") ? fileName : fileName + ".zip";
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .header(HttpHeaders.CONTENT_DISPOSITION,
//                            "attachment; filename=\"" + downloadFileName + "\"")
//                    .body(resource);
//        } catch (IOException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}

package com.example.logmanagementsystem.controller;

import com.example.logmanagementsystem.config.AppConfig;
import com.example.logmanagementsystem.model.LogFile;
import com.example.logmanagementsystem.service.AuthService;
import com.example.logmanagementsystem.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FileService fileService;

    @Autowired
    private AppConfig appConfig;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        model.addAttribute("folders", fileService.getLogFolders());
        model.addAttribute("basePaths", appConfig.getBaseFolderPaths());
        return "dashboard";
    }

    @GetMapping("/folder/{folderName}")
    public String viewFolder(@PathVariable String folderName,
                             HttpSession session,
                             Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        if (!fileService.isValidFolder(folderName)) {
            return "redirect:/dashboard?error=Invalid folder";
        }

        try {
            List<LogFile> files = fileService.getFilesInFolder(folderName);
            model.addAttribute("files", files);
            model.addAttribute("folderName", folderName);
            return "folder-view";
        } catch (IOException e) {
            model.addAttribute("error", "Error reading folder: " + e.getMessage());
            return "dashboard";
        }
    }

    @GetMapping("/download/{folderName}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String folderName,
                                                 @PathVariable String fileName,
                                                 HttpSession session) {
        if (!authService.isLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }

        if (!fileService.isValidFolder(folderName)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Resource resource = fileService.downloadFile(folderName, fileName);
            String downloadFileName = fileName.endsWith(".zip") ? fileName : fileName + ".zip";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + downloadFileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("basePath") String basePath,
                             @RequestParam("folder") String folderName,
                             HttpSession session,
                             Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!fileService.isValidFolder(folderName)) {
            model.addAttribute("error", "Invalid folder");
            return "redirect:/dashboard";
        }
        if (!appConfig.getBaseFolderPaths().contains(basePath)) {
            model.addAttribute("error", "Invalid base path");
            return "redirect:/dashboard";
        }
        if (!appConfig.isUploadEnabled()) {
            model.addAttribute("error", "File upload is disabled");
            return "redirect:/dashboard";
        }
        try {
            String uploadPath = basePath + "/" + folderName + "/" + file.getOriginalFilename();
            File destFile = new File(uploadPath);
            file.transferTo(destFile);
            model.addAttribute("message", "File uploaded successfully to " + uploadPath);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        return "redirect:/folder/" + folderName;
    }
}