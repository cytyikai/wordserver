package com.cytyk.wordserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author CYTYIKAI
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class, scanBasePackages = {"com.cytyk.wordserver"})
public class WordServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordServerApplication.class, args);
    }

}
