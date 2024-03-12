package vn.fpt.web.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

@Slf4j
@Provider
public class ErrorsHandler
        implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        String errorId = UUID.randomUUID().toString();
        String defaultErrorMessage = ResourceBundle.getBundle("validation-messages").getString("system.error");
        ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(defaultErrorMessage);
        ErrorResponse errorResponse = new ErrorResponse(errorId, errorMessage);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
