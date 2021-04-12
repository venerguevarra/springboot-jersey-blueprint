package com.doesitwork.springboot.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Path(value = "/__api")
public class RamlResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getApi() throws IOException {

        InputStream stream = new ClassPathResource("static/web/index.html").getInputStream();
        String text = IOUtils.toString(stream, "UTF-8");
        String systemApi = "/api.raml";
        try {
            String textString = text.replace("{FILE_NAME}", systemApi);
            return Response.ok().entity(textString).build();
        } catch (Exception e) {
            String defaultFile = "../api.raml";
            return Response.ok().entity(text.replace("{FILE_NAME}", defaultFile)).build();
        }
    }

}
