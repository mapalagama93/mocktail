package com.mahmm.mocktail.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    private String id;
    private String name;
    private String method;
    private String path;
    private String status;
    private String accept;
    private String produce;
    private String type;
    private String script;
    private String body;
    private String delay;
    private Map<String, String> headers = new HashMap<>();

    @JsonIgnore
    public int getStatusInt() {
        try {
            return Integer.parseInt(this.status);
        }catch (Exception e) {
            return 200;
        }
    }
    @JsonIgnore
    public int getDelayInt() {
        if(StringUtils.isNumeric(this.delay)) {
            return Integer.parseInt(this.delay);
        }
        return 0;
    }

}
