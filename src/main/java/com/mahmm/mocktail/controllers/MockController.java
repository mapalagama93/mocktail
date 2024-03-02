package com.mahmm.mocktail.controllers;

import com.mahmm.mocktail.services.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @Autowired
    private MockService mockService;

    @RequestMapping(method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.OPTIONS
    }, value = "/**")
    public ResponseEntity<Object> mockEndpoint() {
        return mockService.getMockResponse();
    }

}
