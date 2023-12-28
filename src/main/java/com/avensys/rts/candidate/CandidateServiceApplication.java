package com.avensys.rts.candidate;

import com.avensys.rts.candidate.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.candidate.payloadnewresponse.user.UserGroupResponseDTO;
import com.avensys.rts.candidate.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
public class CandidateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CandidateServiceApplication.class, args);
    }

}
