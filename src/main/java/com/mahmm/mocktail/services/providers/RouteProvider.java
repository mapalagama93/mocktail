package com.mahmm.mocktail.services.providers;

import com.mahmm.mocktail.model.Route;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteProvider {

    public String fetch();

    public void store(String json);
}
