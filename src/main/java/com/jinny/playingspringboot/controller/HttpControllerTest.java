package com.jinny.playingspringboot.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HttpControllerTest {
    //인터넷 브라우저 요청은 get요청밖에 되지 않음.
    @GetMapping("/http/get")
    public String getTest(){
        return "get요청";
    }

    //
    @PostMapping("/http/post")
    public String PostTest(){
        return "post요청";
    }

    @PutMapping("/http/put")
    public String putTest(){
        return "put요청";
    }

    @DeleteMapping("/http/delete")
    public String DeleteTest(){
        return "delete요청";
    }
}
