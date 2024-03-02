package com.mahmm.mocktail.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahmm.mocktail.model.Route;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

@Service
public class JSService {

    ObjectMapper mapper = new ObjectMapper();

    public Route execute(String script) {
        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
        try {
            script = "var response = {};\n\n" + script +"\n\n" + "response";
            Object eval = graalEngine.eval(script);
            return mapper.convertValue(eval, Route.class);
        } catch (ScriptException e) {
            e.printStackTrace();
            Route route = new Route();
            route.setStatus("500");
            route.setHeaders(Map.of("content-type", "application/json"));
            route.setBody("{\"message\" : \"Error while executing javascript\", \"error\" : \"" + e.getLocalizedMessage() + "\"}");
            return route;
        }
    }

}
