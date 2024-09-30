package com.laojiahuo.ictproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class IctProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IctProjectApplication.class, args);
    }

}
