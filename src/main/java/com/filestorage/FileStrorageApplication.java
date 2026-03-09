package com.filestorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class FileStrorageApplication {

    public static void main(String[] args) {

        log.info("################### ");

        SpringApplication.run(FileStrorageApplication.class, args);
    }

}
