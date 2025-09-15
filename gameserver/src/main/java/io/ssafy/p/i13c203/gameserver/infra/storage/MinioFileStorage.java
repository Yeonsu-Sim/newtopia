package io.ssafy.p.i13c203.gameserver.infra.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorage implements FileStorage {

    private final MinioClient minioClient;

    @Value("${app.storage.minio.bucket-name}")
    private String bucketName;

    @Value("${app.storage.minio.public-base-url}")
    private String publicBaseUrl;


    @Override
    public void store(String key, InputStream input, long size, String contentType) throws IOException {
        try {
            String finalKey = key;
            
            // 버킷이 존재하지 않으면 생성
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                
                // 버킷을 public read로 설정
                String policy = String.format("""
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": "*",
                                "Action": "s3:GetObject",
                                "Resource": "arn:aws:s3:::%s/*"
                            }
                        ]
                    }
                    """, bucketName);
                
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policy)
                                .build()
                );
                
                log.info("Created bucket: {} with public read policy", bucketName);
            }

            // 객체 업로드
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(finalKey)
                            .stream(input, size, -1)
                            .contentType(contentType)
                            .build()
            );

            log.info("Successfully stored object: {} in bucket: {}", finalKey, bucketName);
        } catch (Exception e) {
            throw new IOException("Failed to store object in Minio: " + e.getMessage(), e);
        }
    }

    @Override
    public String publicUrl(String key) {
        String finalKey = key;
        return publicBaseUrl.endsWith("/") ? publicBaseUrl + finalKey : publicBaseUrl + "/" + finalKey;
    }

    @Override
    public boolean delete(String key) throws IOException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            log.info("Successfully deleted object: {} from bucket: {}", key, bucketName);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete object: {} from bucket: {}", key, bucketName, e);
            throw new IOException("Failed to delete object from Minio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean exists(String key) throws IOException {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            // StatObject는 객체가 존재하지 않으면 예외를 던지므로 false 반환
            return false;
        }
    }

}