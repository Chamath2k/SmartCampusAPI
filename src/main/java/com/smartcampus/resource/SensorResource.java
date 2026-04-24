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
import com.smartcampus.model.Sensor;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.ArrayList;
import com.smartcampus.exception.LinkedResourceNotFoundException;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {

        if (type == null) {
            return Database.sensors;
        }

        List<Sensor> filtered = new ArrayList<>();

        for (Sensor s : Database.sensors) {
            if (s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }

        return filtered;
    }

    @POST
    public String addSensor(Sensor sensor) {

        // validate room exists
        for (Room room : Database.rooms) {
            if (room.getId().equals(sensor.getRoomId())) {
                Database.sensors.add(sensor);
                room.getSensorIds().add(sensor.getId());
                return "Sensor added successfully!";
            }
        }

        throw new LinkedResourceNotFoundException("Room does not exist");
    }
    
    @Path("/{id}/readings")
    public ReadingResource getReadingResource(@PathParam("id") String sensorId) {
        return new ReadingResource(sensorId);
    }
}
