package com.mahmm.mocktail.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahmm.mocktail.model.History;
import com.mahmm.mocktail.model.Route;
import com.mahmm.mocktail.utils.JSONUtils;
import com.mahmm.mocktail.utils.Rand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MockService {

    @Autowired
    private RouteService routeService;
    @Autowired
    private JSService jsService;
    @Autowired
    private HttpServletRequest request;
    private ObjectMapper mapper = new ObjectMapper();
    @Getter
    private List<History> history = new ArrayList<>();

    @Value("${mocktail.history.size:1000}")
    private int historySize = 1000;

    public ResponseEntity<Object> getMockResponse() {

        Optional<Route> matchingRoute = getMatchingRoute(request.getMethod(), request.getContentType(), request.getRequestURI());
        Route route = matchingRoute.orElseGet(() -> routeService.getDefaultRoute());

        // convert route to response
        Route response = new Route();
        response.setId(route.getId());
        response.setName(route.getName());
        response.setStatus(route.getStatus());
        response.setBody(route.getBody());
        response.getHeaders().put("content-type", route.getProduce());
        if(route.getHeaders() != null) {
            for (Map.Entry<String, String> header : route.getHeaders().entrySet()) {
                response.getHeaders().put(header.getKey(), header.getValue());
            }
        }
        // execute script
        if (StringUtils.isNotBlank(route.getScript())) {
            Route scriptRoute = jsService.execute(route.getScript());
            this.populate(scriptRoute, response);
        }

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusInt());
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            builder = builder.header(header.getKey(), header.getValue());
        }
        log(response);
        return builder.body(response.getBody());
    }

    public void log(Route route) {
        History his = new History();
        his.setId(Rand.small());
        his.setRouteId(route.getId());
        Map<String, String> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
        his.setPath(request.getRequestURI());
        his.setName(route.getName());
        his.setMethod(request.getMethod());
        his.setRequestHeaders(JSONUtils.toStr(headers));
        his.setRequestParams(request.getQueryString());
        try {
            his.setRequestBody(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (Exception e) {
            his.setRequestBody("MOCKTAIL ERROR : Unable to parse body");
        }
        his.setResponseBody(route.getBody());
        his.setResponseHeaders(JSONUtils.toStr(route.getHeaders()));
        his.setStatus(route.getStatus());
        his.setTime(new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss").format(Calendar.getInstance().getTime()));
        log.info(JSONUtils.toStr(his));
        this.history.add(0, his);
        try {
            if (this.history.size() > historySize) {
                this.history.remove(this.history.size() - 1);
            }
        } catch (Exception e) {
            log.error("error while removing items ", e);
        }
    }

    public void populate(Route src, Route dst) {
        if (src == null) return;
        if (StringUtils.isNotBlank(src.getStatus())) {
            dst.setStatus(src.getStatus());
        }
        if (StringUtils.isNotBlank(src.getBody())) {
            dst.setBody(src.getBody());
        }
        if (src.getHeaders() != null && src.getHeaders().size() > 0) {
            dst.setHeaders(src.getHeaders());
        }
    }


    private Optional<Route> getMatchingRoute(String method, String contentType, String requestPath) {
        for (Route route : routeService.getRoutes()) {
            // Check method
            if (!StringUtils.equalsIgnoreCase(route.getMethod(), method) && !StringUtils.equalsIgnoreCase(route.getMethod(), "*")) {
                continue;
            }
            // Check content type
            if (!StringUtils.equalsIgnoreCase(route.getAccept(), contentType) && !StringUtils.equalsIgnoreCase(route.getAccept(), "*")) {
                continue;
            }
            String pattern = route.getPath().replace("*", ".*");
            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(requestPath);
            if (matcher.matches()) {
                return Optional.of(route);
            }
        }
        return Optional.empty();
    }

}
