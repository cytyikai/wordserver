package com.cytyk.wordserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author CYTYIKAI
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = {"com.cytyk.wordserver"})
public class WordServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordServerApplication.class, args);
    }

}
