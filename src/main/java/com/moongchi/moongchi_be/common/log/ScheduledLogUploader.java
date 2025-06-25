package com.moongchi.moongchi_be.common.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledLogUploader {

    private final LogUploadService logUploadService;

    private static final Path LOG_DIR = Paths.get("logs");

    // 1시간마다 실행 (이전 작업 시작 시점 기준)
//    @Scheduled(fixedRate = 60 * 60 * 1000)
    // 매 정시 0분에 실행 (cron 표현식)
    @Scheduled(cron = "0 0 * * * *")
    public void uploadLogsToS3() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(LOG_DIR, "activity-log.*.json");

            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                // S3에 저장할 키명 예: logs/2025/06/13/activity-log.2025-06-13.json
                String key = "logs/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + fileName;

                logUploadService.uploadFileToS3(path, key);

                Files.delete(path);
                log.info("로컬 로그 파일 삭제 완료: {}", fileName);
            }
        } catch (IOException e) {
            log.error("로그 업로드 중 에러 발생", e);
        }
    }
}

