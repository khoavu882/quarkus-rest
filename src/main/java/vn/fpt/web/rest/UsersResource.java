package vn.fpt.web.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import vn.fpt.models.users.IamPagination;
import vn.fpt.models.users.IamUserInfo;
import vn.fpt.security.AppSecurityContext;
import vn.fpt.services.UsersService;
import vn.fpt.web.dto.InviteUserInput;
import vn.fpt.web.dto.PaginateRequest;
import vn.fpt.web.errors.ErrorsEnum;
import vn.fpt.web.errors.exceptions.PermissionDeniedException;
import vn.fpt.web.errors.models.ErrorResponse;

@Slf4j
@Path("/api/users")
@Tag(name = "Users Management", description = "Users Management AI Camera Service")
public class UsersResource {

    @Inject
    UsersService usersService;

    @GET
    @Path(("/invite"))
    @Operation(
            operationId = "inviteUser",
            summary = "Admin invite User access AI Camera Service"
    )
    @APIResponse(
            responseCode = "202"
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Internal Sever Error",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public Response inviteUser(@BeanParam InviteUserInput dto,
                               @Context ContainerRequestContext requestContext) {

        usersService.invite(dto);

        return Response
                .accepted()
                .build();
    }

    @GET
    @Operation(
            operationId = "getUsers",
            summary = "Get list Users of AI Camera Service"
    )
    @Parameters(
            value = {
                    @Parameter(name = "first", description = ""),
                    @Parameter(name = "max", description = ""),
                    @Parameter(name = "app", description = ""),
                    @Parameter(name = "order", description = "")
            }
    )
    @APIResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = IamPagination.class)
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public Response getUsers(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String token,
            @BeanParam PaginateRequest paginateRequest,
            @Context ContainerRequestContext requestContext) {

        AppSecurityContext appContext = (AppSecurityContext) requestContext.getSecurityContext();

        if (!appContext.isOwner("admin-churn"))
            throw new PermissionDeniedException(ErrorsEnum.AUTH_NO_ACCESS);

        IamPagination<IamUserInfo> users = usersService.getUsers(token, paginateRequest);
        return Response
                .ok(users)
                .build();
    }

    @GET
    @Path(("/{id}"))
    @Operation(
            operationId = "getUserById",
            summary = "Get details by User ID of AI Camera Service"
    )
    @APIResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = IamUserInfo.class)
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public Response getUserById(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                @PathParam("id") String id,
                                @QueryParam("app") String app,
                                @Context ContainerRequestContext requestContext) {

        IamUserInfo user = usersService.getUserById(token, id, app);
        return Response
                .ok(user)
                .build();
    }
}
