package com.friendgithub.api.controller;

import com.friendgithub.api.dto.request.ApiResponse;
import com.friendgithub.api.model.FileRequest;
import com.friendgithub.api.model.Project;
import com.friendgithub.api.model.Response;
import com.friendgithub.api.service.FileService;
import com.friendgithub.api.urls.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(Path.File.FILE)
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = Path.File.UPLOAD, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadFileNew(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("projectId") String projectId) throws Exception {

        if (projectId == null || projectId.isEmpty()) {
            throw new IllegalArgumentException("Project ID must not be empty.");
        }

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be empty.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }

        String path = fileService.saveMediaFile(projectId, file, userId);

        return ApiResponse.<String>builder()
                .data(path)
                .build();
    }

    @RequestMapping(value = Path.File.DOWNLOAD, method = RequestMethod.POST)
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestBody FileRequest request) throws IOException {

        byte[] data = fileService.getMediaFile(request);

        if (data == null || data.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + request.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }

    // API để lấy danh sách tất cả các file của project
    @RequestMapping(value = Path.File.LIST, method = RequestMethod.GET)
    public ResponseEntity<Response> listFileForProject(@RequestParam("projectId") String projectId) {
        try {
            List<Project> listFile = fileService.fetchListFileForProject(projectId);

            Response response = Response.builder()
                    .code("SUCCESS_CODE")
                    .message("Project files retrieved successfully")
                    .data(listFile)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Response response = Response.builder()
                    .code("ERROR_CODE")
                    .message("Failed to retrieve project files: " + ex.getMessage())
                    .build();
            return ResponseEntity.status(500).body(response);
        }
    }
}
