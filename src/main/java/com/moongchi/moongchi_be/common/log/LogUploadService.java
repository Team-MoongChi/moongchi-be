package com.moongchi.moongchi_be.common.log;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class LogUploadService {

    private final S3Client s3Client;

    @Value("${BUCKET_NAME}")
    private String bucket;

    public void uploadFileToS3(Path filePath, String keyName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(keyName)
                    .build();

            s3Client.putObject(putObjectRequest, filePath);
            System.out.println("S3 업로드 완료: " + keyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

