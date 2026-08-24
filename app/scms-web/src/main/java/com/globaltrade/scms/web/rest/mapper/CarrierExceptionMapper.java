package com.globaltrade.scms.web.rest.mapper;

import com.globaltrade.scms.common.exception.CarrierSystemException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CarrierExceptionMapper implements ExceptionMapper<CarrierSystemException> {
    @Override
    public Response toResponse(CarrierSystemException exception) {
        // Return HTTP 503 Service Unavailable with a clean, professional message
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Supply Chain Resilience Active: " + exception.getMessage())
                .build();
    }
}