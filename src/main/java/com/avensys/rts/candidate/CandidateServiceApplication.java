package com.avensys.rts.candidate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
public class CandidateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CandidateServiceApplication.class, args);
    }

}
