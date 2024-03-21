package vn.fpt.web.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import vn.fpt.models.auth.AuthToken;
import vn.fpt.models.auth.DecryptAuth;
import vn.fpt.models.auth.DmCUserInfo;
import vn.fpt.models.users.IamPagination;
import vn.fpt.secure.AppSecurityContext;
import vn.fpt.secure.SecurityUtil;
import vn.fpt.services.AuthService;
import vn.fpt.web.errors.models.ErrorResponse;
import vn.fpt.web.errors.ErrorsEnum;
import vn.fpt.web.errors.exceptions.PermissionDeniedException;
import vn.fpt.web.errors.exceptions.UnauthorizedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Path("/app")
@Tag(name = "Auth Management", description = "Auth Management AI Camera Service")
public class AuthResource {

    @ConfigProperty(name = "application.secret-code")
    String IAM_SECRET;

    @Inject
    AuthService authService;

    @GET
    @Path("/auth")
    @Operation(
            operationId = "getAuth",
            summary = "Get list Users of AI Camera Service"
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
    public Response getAuth(
            @Context ContainerRequestContext requestContext,
            @HeaderParam(HttpHeaders.CONTENT_LANGUAGE) String language,
            @QueryParam("authorizeCode") String authorizeCode
    ) {

        ErrorsEnum error = null;

        if (authorizeCode == null) {
            // Define error key and setting new Message depending on http header Content-Language
            error = ErrorsEnum.AUTH_NO_ACCESS;
            error.setMessage("i18n/error_messages", language);

            throw new UnauthorizedException(error);
        }

        String encodedCode = URLEncoder.encode(authorizeCode, StandardCharsets.UTF_8);

        DecryptAuth deAuth = new DecryptAuth();
        deAuth.setEncryptedToken(encodedCode);
        deAuth.setSecretCode(IAM_SECRET);

        AuthToken authToken = authService.decryptToken(deAuth);

        String token = authToken.getToken();

        if(token.isBlank())
            throw new UnauthorizedException(ErrorsEnum.AUTH_FAILED);
        else {

            DmCUserInfo userInfo = authService.getUserPermission("churn", "cads", token);

            if (SecurityUtil.isUserHasPermission("churn", userInfo)) {

                requestContext.setSecurityContext(new AppSecurityContext(userInfo));

                NewCookie cookie = new NewCookie("accessToken", token);

                return Response
                        .ok()
                        .entity(userInfo)
                        .cookie(cookie)
                        .build();
            } else throw new PermissionDeniedException(ErrorsEnum.AUTH_NO_ACCESS);
        }
    }

    @GET
    @Path("/logout")
    @Operation(
            operationId = "logout",
            summary = "User logout of AI Camera Service"
    )
    @APIResponse(
            responseCode = "200"
    )
    @APIResponse(
            responseCode = "500",
            description = "",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public Response logout(
            @Context ContainerRequestContext requestContext
    ) throws URISyntaxException {

        NewCookie removeCookie = new NewCookie("accessToken", "");

        return Response
                .status(Response.Status.PERMANENT_REDIRECT)
                .location(new URI("/"))
                .cookie(removeCookie)
                .build();
    }
}