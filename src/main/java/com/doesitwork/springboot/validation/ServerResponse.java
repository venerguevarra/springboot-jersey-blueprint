package com.doesitwork.springboot.validation;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

public class ServerResponse {
    private static final String GENERIC_ERROR_MESSAGE = "internal_server_error";
    private static final String CONFLICT_ERROR_MESSAGE = "resource_already_exist";
    private static final String NOT_FOUND_ERROR_MESSAGE = "resource_not_found";

    public static Response NOT_FOUND() {
        return Response.status(HttpServletResponse.SC_NOT_FOUND).entity(ErrorResponse.instance(NOT_FOUND_ERROR_MESSAGE)).build();
    }

    public static Response NOT_FOUND(String errorMessage) {
        return Response.status(HttpServletResponse.SC_NOT_FOUND).entity(ErrorResponse.instance(errorMessage)).build();
    }

    public static Response INTERNAL_SERVER_ERROR() {
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(ErrorResponse.instance(GENERIC_ERROR_MESSAGE)).build();
    }

    public static Response INTERNAL_SERVER_ERROR(String errorMessage) {
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(ErrorResponse.instance(errorMessage)).build();
    }

    public static Response UNAUTHORIZED(Object entity) {
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(entity).build();
    }

    public static Response UNAUTHORIZED() {
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }

    public static Response OK() {
        return Response.ok().build();
    }

    public static Response OK(Object entity) {
        return Response.ok().entity(entity).build();
    }

    public static Response CONFLICT() {
        return Response.status(HttpServletResponse.SC_CONFLICT).entity(ErrorResponse.instance(CONFLICT_ERROR_MESSAGE)).build();
    }

    public static Response CONFLICT(String errorMessage) {
        return Response.status(HttpServletResponse.SC_CONFLICT).entity(ErrorResponse.instance(errorMessage)).build();
    }
    
    public static Response CONFLICT(Object errorMessage) {
        return Response.status(HttpServletResponse.SC_CONFLICT).entity(errorMessage).build();
    }

    public static Response CREATED(Object entity) {
        return Response.status(HttpServletResponse.SC_CREATED).entity(entity).build();
    }

    public static Response CREATED() {
        return Response.status(HttpServletResponse.SC_CREATED).build();
    }

    public static Response BAD_REQUEST(Set<ValidationMessage> errorMessages) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(new ValidationErrorResponse(errorMessages)).build();
    }

    public static Response BAD_REQUEST(String errorMessage) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(ErrorResponse.instance(errorMessage)).build();
    }

    public static Response FORBIDDEN(String errorMessage) {
        return Response.status(HttpServletResponse.SC_FORBIDDEN).entity(ErrorResponse.instance(errorMessage)).build();
    }

    public static Response GONE(String errorMessage) {
        return Response.status(HttpServletResponse.SC_GONE).entity(ErrorResponse.instance(errorMessage)).build();
    }

    public static Response GONE(Object errorMessage) {
        return Response.status(HttpServletResponse.SC_GONE).entity(errorMessage).build();
    }
}
