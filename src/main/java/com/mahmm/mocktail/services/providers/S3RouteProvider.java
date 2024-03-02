package com.mahmm.mocktail.services.providers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@ConditionalOnProperty(
        value = "mocktail.provider",
        havingValue = "S3RouteProvider"
)
public class S3RouteProvider implements RouteProvider {

    @Value("${mocktail.provider.S3RouteProvider.bucket}")
    private String bucketName;
    @Value("${mocktail.provider.S3RouteProvider.file}")
    private String file;

    private AmazonS3 s3Client = null;

    @PostConstruct
    public void init() {
        this.s3Client = AmazonS3ClientBuilder.standard().build();
        try {
            fetch();
        } catch (Exception e) {
            store("[]");
        }
    }

    @Override
    public String fetch() {
        log.info("reading file from s3 bucket, bucket = {}, file = {}", this.bucketName, this.file);
        S3Object object = this.s3Client.getObject(this.bucketName, this.file);
        try {
            return new String(object.getObjectContent().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("unable to read file from s3");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(String json) {
        log.info("store file in s3 bucket, bucket = {}, file = {}", this.bucketName, this.file);
        this.s3Client.putObject(this.bucketName, this.file, json);
    }
}
