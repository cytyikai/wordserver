package com.cytyk.wordserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author CYTYIKAI
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class WordserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordserverApplication.class, args);
    }

}
