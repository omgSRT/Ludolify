package com.omgsrt.Ludolify.v1.test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/test")
@RequiredArgsConstructor
public class TestController {
    @Value("${spring.jwt.secret}")
    private String jwtSecret;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;
    @Value("${spring.data.mongodb.database}")
    private String database;

    @GetMapping("/something_sus")
    public List<String> getConfig() {
        List<String> list = new ArrayList<>();
        list.add(jwtSecret);
        list.add(username);
        list.add(password);
        list.add(database);
        return list;
    }
}
