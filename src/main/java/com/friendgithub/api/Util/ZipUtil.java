package com.friendgithub.api.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    public static void zipFile(InputStream inputStream, Path zipPath, String fileName) throws IOException {
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, bytesRead);
            }
            zipOut.closeEntry();
        }
    }

    public static byte[] unzipFile(Path zipPath) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipPath));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((zipEntry = zipIn.getNextEntry()) != null) {
                while ((bytesRead = zipIn.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                zipIn.closeEntry();
            }

            return outputStream.toByteArray();
        }
    }
}
