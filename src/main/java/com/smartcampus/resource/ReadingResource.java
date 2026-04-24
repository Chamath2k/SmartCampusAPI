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
import com.smartcampus.model.SensorReading;
import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReadingResource {

    private String sensorId;

    public ReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @POST
    public String addReading(SensorReading reading) {

        reading.setSensorId(sensorId);

        for (Sensor s : Database.sensors) {
            if (s.getId().equals(sensorId)) {

                if ("MAINTENANCE".equals(s.getStatus())) {
                    throw new SensorUnavailableException("Sensor is under maintenance");
                }

                s.setCurrentValue(reading.getValue());
            }
        }

        Database.readings.add(reading);

        return "Reading added to sensor " + sensorId;
    }

    @GET
    public List<SensorReading> getReadings(
            @QueryParam("from") Long from,
            @QueryParam("to") Long to) {

        List<SensorReading> result = new ArrayList<>();

        for (SensorReading r : Database.readings) {

            if (!r.getSensorId().equals(sensorId)) {
                continue;
            }

            boolean afterFrom = (from == null) || (r.getTimestamp() >= from);
            boolean beforeTo = (to == null) || (r.getTimestamp() <= to);

            if (afterFrom && beforeTo) {
                result.add(r);
            }
        }

        return result;
    }
    
    @GET
    @Path("/average")
    public double getAverage() {

        double sum = 0;
        int count = 0;

        for (SensorReading r : Database.readings) {
            if (r.getSensorId().equals(sensorId)) {
                sum += r.getValue();
                count++;
            }
        }

        if (count == 0) return 0;

        return sum / count;
    }
}