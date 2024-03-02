package com.mahmm.mocktail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private String id;
    private String time;
    private String routeId;
    private String name;
    private String path;
    private String status;
    private String method;
    private String requestHeaders;
    private String responseHeaders;
    private String requestParams;
    private String requestBody;
    private String responseBody;
}
