package com.friendgithub.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendgithub.api.Util.ZipUtil;
import com.friendgithub.api.entity.FileEntity;
import com.friendgithub.api.model.FileRequest;
import com.friendgithub.api.model.Project;
import com.friendgithub.api.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Value("${root.repository}")
    private String ROOT_REPOSITORY;

    @Autowired
    private FileRepository fileRepository;

    public String saveMediaFile(String projectId, MultipartFile file, String userId) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new Exception("File must have a name");
        }

        String fileExtension = getFileExtension(originalFilename);
        String fileName = getFileNameWithoutExtension(originalFilename);

        try (InputStream inputStream = file.getInputStream()) {

            return writeFile(projectId, userId, fileName, inputStream, fileExtension);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }

    public String writeFile(String projectId, String userId, String fileName, InputStream file, String fileExtension)
            throws Exception {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be empty");
        }

        if (file == null || file.available() == 0) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String fullFileName = (fileExtension == null || fileExtension.trim().isEmpty()) ?
                fileName : fileName + "." + fileExtension;
        Path pathToSave = Paths.get(ROOT_REPOSITORY, fullFileName);

        createDirectory(pathToSave.getParent());

        try {
            ZipUtil.zipFile(file, pathToSave, fullFileName);

            String size = String.valueOf(Files.size(pathToSave));

            Date createdAt = new Date();
            String modifiedByUserId = null;

            boolean isFileExists = fileRepository.existsByProjectIdAndFileName(projectId, fileName);
            if (isFileExists) {
                modifiedByUserId = userId;
            }

            FileEntity fileEntity = createFile(projectId, fileName, size, userId,
                    modifiedByUserId, pathToSave, createdAt);
            System.out.println(fileEntity);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("An error while saving the file: " + e.getMessage(), e);
        }

        return pathToSave.toString();
    }

    public byte[] readFile(FileRequest request) throws IOException {
        Optional<String> jsonPath = fileRepository.findFilePathByProjectIdAndFileName(request.getProjectId(), request.getFileName());

        if (jsonPath.isPresent()) {
            String json = jsonPath.get();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode filePathNode = rootNode.get("file_path");

            if (filePathNode != null) {
                String fullPath = filePathNode.asText();
                System.out.println("Path found: " + fullPath);

                Path pathToRead = Paths.get(fullPath);
                if (Files.exists(pathToRead) && Files.isReadable(pathToRead)) {
                    return ZipUtil.unzipFile(pathToRead);
                } else {
                    throw new IOException("File does not exist or is not readable: " + pathToRead);
                }
            } else {
                throw new IOException("Unable to extract file_path from response JSON");
            }
        } else {
            throw new IOException("Path not found for projectId: " + request.getProjectId() + " and fileName: "
                    + request.getFileName());
        }
    }

    private void createDirectory(Path dirPath) throws Exception {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private FileEntity createFile(
            String projectId,
            String fileName,
            String size,
            String createdByUserId,
            String modifiedByUserId,
            Path pathToSave,
            Date createdAt) throws IOException {

        int version = 1;
        Optional<FileEntity> existingFile = fileRepository.findByProjectIdAndFileName(projectId, fileName);
        if (existingFile.isPresent()) {
            version = existingFile.get().getVersion() + 1;
        }

        FileEntity file = new FileEntity();
        file.setProjectId(projectId);
        file.setFileName(fileName);
        file.setSize(size);
        file.setCreatedByUserId(createdByUserId);
        file.setModifiedByUserId(modifiedByUserId);
        file.setVersion(version);
        file.setPath(String.valueOf(pathToSave));
        file.setCreateAt(createdAt);

        return fileRepository.save(file);
    }

    private String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, dotIndex);
    }

    public byte[] getMediaFile(FileRequest request) throws IOException {
        return readFile(request);
    }


    public List<Project> fetchListFileForProject(String projectId) {
        List<Project> fileDetails = new ArrayList<>();

        // Query list of files from database based on projectId
        List<FileEntity> files = fileRepository.findByProjectId(projectId);

        for (FileEntity file : files) {
            Project project = new Project(
                    file.getFileName(),
                    file.getSize(),
                    file.getPath()
            );
            fileDetails.add(project);
        }

        return fileDetails;
    }
}
