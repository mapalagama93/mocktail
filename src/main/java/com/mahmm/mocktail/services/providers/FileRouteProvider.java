package com.mahmm.mocktail.services.providers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(
        value = "mocktail.provider",
        havingValue = "FileRouteProvider",
        matchIfMissing = true
)
public class FileRouteProvider implements RouteProvider {

    @Value("${mocktail.provider.FileRouteProvider.file}")
    private String file;

    @PostConstruct
    public void init() {
        if(!new File(file).exists()) {
            this.store("[]");
        }
    }

    @Override
    public String fetch() {
        Path path = Paths.get(this.file);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(String json) {
        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
