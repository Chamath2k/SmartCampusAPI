/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

/**
 *
 * @author Chamath
 */


import com.smartcampus.database.Database;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.smartcampus.exception.RoomNotEmptyException;
import javax.ws.rs.NotFoundException;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // GET all rooms
    @GET
    public List<Room> getRooms() {
        return Database.rooms;
    }

    // POST create new room
    @POST
    public Room addRoom(Room room) {
        Database.rooms.add(room);
        return room;
    }
    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") String id) {
        for (Room room : Database.rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    
    
    }
    
    @DELETE
    @Path("/{id}")
    public String deleteRoom(@PathParam("id") String id) {

        for (Room room : Database.rooms) {

            if (room.getId().equals(id)) {

                // check if sensors exist
                if (!room.getSensorIds().isEmpty()) {
                    throw new RoomNotEmptyException("Room has sensors assigned");
                }

                Database.rooms.remove(room);
                return "Room deleted successfully!";
            }
        }

        throw new NotFoundException("Room not found");
    }
}
