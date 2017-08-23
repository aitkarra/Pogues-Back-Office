package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.user.model.User;
import fr.insee.pogues.user.service.UserService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Component
@Path("/user")
@Api(value = "Pogues User")
public class PoguesUser {


    @Context
    HttpServletRequest request;

    @Autowired
    private UserService userService;

    final static Logger logger = LogManager.getLogger(PoguesUser.class);


    @GET
    @Path("id")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get Id",
            notes = "Get the user id of the connected user",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created"),
            @ApiResponse(code = 403, message = "Not authenticated")
    })
    public Response getID() throws Exception {
        try {
            JSONObject result = userService.getUserID(request);
            return Response.status(Status.OK).entity(result).build();
        } catch (PoguesException e) {
            throw e;
        }
    }


    @GET
    @Path("attributes")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get Id",
            notes = "Get the user id of the connected user",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Created"),
            @ApiResponse(code = 403, message = "Not authenticated")
    })
    public Response getAttribute() throws Exception {
        try {
            User user = userService.getNameAndPermission(request);
            return Response.status(Status.OK).entity(user).build();
        } catch(Exception e){
            throw e;
        }

    }


    @GET
    @Path("permissions")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get permissions",
            notes = "Get all available permissions",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
    })
    public Response getPermissions() throws Exception {
        try {
            return Response.ok(userService.getPermissions()).build();
        } catch (Exception e) {
            throw e;
        }
    }

}
