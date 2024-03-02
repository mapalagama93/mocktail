package com.mahmm.mocktail.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mahmm.mocktail.model.Route;
import com.mahmm.mocktail.services.providers.RouteProvider;
import com.mahmm.mocktail.utils.JSONUtils;
import com.mahmm.mocktail.utils.Rand;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class RouteService {

    @Autowired
    private RouteProvider routeProvider;

    private List<Route> routes = new ArrayList<>();

    @PostConstruct
    public void post() {
        this.fetchRoutes();
    }

    public Route getDefaultRoute() {
        return new Route("default", "Default Route", null, null, "200", null, "application/json", "fixed", null, "{\"message\" : \"No routes matched this request.\"}", Map.of());
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public Route getRoute(String id) {
        for (Route route : this.routes) {
            if (StringUtils.equals(route.getId(), id)) {
                return route;
            }
        }
        return null;
    }

    public void delete(String id) {
        Route tobeDeleted = null;
        for (Route route : this.routes) {
            if (StringUtils.equals(id, route.getId())) {
                tobeDeleted = route;
                break;
            }
        }
        if (tobeDeleted != null) {
            log.info("deleting route = {}", tobeDeleted.getId());
            this.routes.remove(tobeDeleted);
        }
    }

    public void addRoute(Route route) {
        if (StringUtils.isBlank(route.getId())) {
            route.setId(Rand.small());
            log.info("add new route = {}", route.getId());
        } else {
            log.info("update route = {}", route.getId());
            delete(route.getId());
        }
        this.routes.add(route);
        this.routeProvider.store(JSONUtils.toStr(this.routes));
        this.fetchRoutes();
    }

    public void fetchRoutes() {
        String fetch = this.routeProvider.fetch();
        if (StringUtils.isBlank(fetch)) {
            fetch = "[]";
        }
        List<Route> r = JSONUtils.toObj(fetch, new TypeReference<List<Route>>() {
        });
        this.routes = Objects.requireNonNullElseGet(r, ArrayList::new);
    }

}
