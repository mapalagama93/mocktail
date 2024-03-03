package com.mahmm.mocktail.controllers;

import com.mahmm.mocktail.model.Route;
import com.mahmm.mocktail.services.MockService;
import com.mahmm.mocktail.services.RouteService;
import com.mahmm.mocktail.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mocktail")
public class WebFrontController {

    @Autowired
    MockService mockService;

    @Autowired
    RouteService routeService;

    @RequestMapping
    public String logs(Model model) {
        model.addAttribute("history", mockService.getHistory());
        return "logs";
    }

    @RequestMapping("/routes")
    public String routes(Model model) {
        model.addAttribute("routes", routeService.getRoutes());
        return "routes";
    }


    @RequestMapping("/route")
    public String route(@RequestParam(required =false) String id, Model model) {
        model.addAttribute("route", routeService.getRoute(id));
        return "route";
    }

    @PostMapping("/route/add")
    @ResponseBody
    public String routeAdd(@RequestBody Route route) {
        routeService.addRoute(route);
        return "route";
    }

    @DeleteMapping("/route/delete")
    @ResponseBody
    public String routeDelete(@RequestParam String id) {
        routeService.delete(id);
        return "route";
    }



}
