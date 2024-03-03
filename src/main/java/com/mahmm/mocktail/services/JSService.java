package com.mahmm.mocktail.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahmm.mocktail.model.Route;
import com.mahmm.mocktail.utils.JSONUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JSService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ServletContext servletContext;

    ObjectMapper mapper = new ObjectMapper();

    public Route execute(String script, Route route) {

        Map<String, String> headers = new HashMap<>();
        try {
            headers = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(h -> h, request::getHeader));
        } catch (Exception e) {
            log.debug("error while getting headers {}", e.getLocalizedMessage());
        }
        String body = null;
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            log.debug("error while getting body {}", e.getLocalizedMessage());
        }
        String queryParam = JSONUtils.toStr(Collections.list(request.getParameterNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getParameter)));
        String url = request.getRequestURI().replace(servletContext.getContextPath(), "");
        ScriptEngine graalEngine = new ScriptEngineManager().getEngineByName("graal.js");
        try {
            graalEngine.put("Ae2590ef62ca9152696d4bd522077e4b7cd612019", JSONUtils.toStr(headers));
            graalEngine.put("A4f01cb098118c2861fcb763a53c643d2c081f3d9", body);
            graalEngine.put("A002eacc4ae8af5b20307b3f645942ba27f13d018", queryParam);
            graalEngine.put("Ae3f3574adcfb57b5ef54b6f8816c63c313035d66", url);
            graalEngine.put("Ae3f3574adcfb57b5ef54b6f8816c63c313035d96", request.getMethod());
            String prescript = """
                                var request = {
                                    "headers" : JSON.parse(Ae2590ef62ca9152696d4bd522077e4b7cd612019),
                                    "body" : A4f01cb098118c2861fcb763a53c643d2c081f3d9,
                                    "query" : JSON.parse(A002eacc4ae8af5b20307b3f645942ba27f13d018),
                                    "url" : Ae3f3574adcfb57b5ef54b6f8816c63c313035d66,
                                    "method" : Ae3f3574adcfb57b5ef54b6f8816c63c313035d96
                                };
                                var response = {};
                                """;
            script = prescript + "\n\n" + script + "\n\n" + "response";
            Object eval = graalEngine.eval(script);
            return mapper.convertValue(eval, Route.class);
        } catch (ScriptException e) {
            e.printStackTrace();
            Route res = new Route();
            res.setStatus("500");
            res.setHeaders(Map.of("content-type", "application/json"));
            res.setBody("{\"message\" : \"Error while executing javascript\", \"error\" : \"" + e.getLocalizedMessage() + "\"}");
            return res;
        }
    }

}
