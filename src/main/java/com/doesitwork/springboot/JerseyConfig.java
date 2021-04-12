package com.doesitwork.springboot;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doesitwork.springboot.resource.GtgResource;
import com.doesitwork.springboot.resource.RamlResource;
import com.doesitwork.springboot.util.LocalDateProvider;
import com.doesitwork.springboot.util.LocalDateTimeProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class JerseyConfig extends ResourceConfig {

    @Autowired
    public JerseyConfig(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        register(new ObjectMapperContextResolver(objectMapper));
        register(LocalDateProvider.class);
        register(LocalDateTimeProvider.class);

        register(GtgResource.class);
        register(RamlResource.class);

        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }

    @Provider
    public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

        private final ObjectMapper mapper;

        public ObjectMapperContextResolver(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        public ObjectMapper getContext(Class<?> type) {

            return mapper;

        }

    }
}
