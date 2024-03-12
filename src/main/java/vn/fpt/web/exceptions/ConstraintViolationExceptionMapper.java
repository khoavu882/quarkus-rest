package vn.fpt.web.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ErrorResponse.ErrorMessage> errorMessages = e.getConstraintViolations().stream()
                .map(constraintViolation -> new ErrorResponse.ErrorMessage(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()))
                .toList();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(errorMessages))
                .build();
    }

}
