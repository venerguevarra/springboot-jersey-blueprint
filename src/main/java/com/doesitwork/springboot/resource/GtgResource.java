package com.doesitwork.springboot.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.doesitwork.springboot.bean.GtgBean;

@Component
@Path("/__gtg")
public class GtgResource {

    @Value("${applicationConfig.environment}")
    private String environment;

    @Value("${applicationConfig.version}")
    private String version;

    @Value("${applicationConfig.applicationName}")
    private String applicationName;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response gtg() {
        final GtgBean gtgResponseBean = new GtgBean(environment, GtgBean.OK, version, applicationName);
        return Response.ok(gtgResponseBean).build();
    }

}
