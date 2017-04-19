package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author c0665877
 */
@Path("/messages")
@ApplicationScoped
public class MessageREST {

    @Inject
    private MessageController messagesController;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(messagesController.getJsonAll()).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id) {
        JsonObject json = messagesController.getByIdJson(id);
        if (json != null) {
            return Response.ok(json).build();
        } else {
            return Response.ok(Response.Status.NOT_FOUND).build();
        }

    }

    @GET
    @Path("{from}/{to}")
    @Produces("application/json")
    public Response getByDate(@PathParam("from") String fromString, @PathParam("to") String toString) {
        try {
            return Response.ok(messagesController.getByDateJson(sdf.parse(fromString), sdf.parse(toString))).build();
        } catch (ParseException ex) {
            Logger.getLogger(MessageREST.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(JsonObject json) {
        return Response.ok(messagesController.addingJson(json)).build();
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response edit(@PathParam("id") int id, JsonObject json) {
        JsonObject jsonId = messagesController.editJson(id, json);
        if (jsonId != null) {
            return Response.ok(jsonId).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response removing(@PathParam("id") int id) {
        if (messagesController.deleteById(id)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
