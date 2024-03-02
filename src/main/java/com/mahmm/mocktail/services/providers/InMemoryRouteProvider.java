package com.mahmm.mocktail.services.providers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "mocktail.provider",
        havingValue = "InMemoryRouteProvider",
        matchIfMissing = true
)
public class InMemoryRouteProvider implements RouteProvider {

    private String store;

    @Override
    public String fetch() {
        return this.store;
    }

    @Override
    public void store(String json) {
        this.store = json;
    }
}
