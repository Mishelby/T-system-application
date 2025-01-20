package org.example.logisticapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class LogisticApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticApplication.class, args);
    }

//    @Bean
//    @Scope("prototype")
//    public StringBuilder sb(){
//        return new StringBuilder();
//    }

    @Bean
    public AtomicLong counter(){
        return new AtomicLong(1L);
    }

}
