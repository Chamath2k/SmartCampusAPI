# Smart Campus REST API

Student Information

Name: Kanahelage Silva
UOW ID: W2119796
IIT ID: 20241128
Module: 5COSC022W – Client-Server Architectures

---

  Overview

This project is a RESTful API developed using **JAX-RS (Java EE)** and deployed on **Apache Tomcat**.
It is designed for a Smart Campus system to manage **Rooms, Sensors, and Sensor Readings**.

The API follows REST principles including:

* Resource-based design
* Proper HTTP methods
* Nested resources
* Structured error handling
* Logging

---

  Technologies Used

* Java EE (JAX-RS)
* Apache Tomcat
* Maven
* NetBeans
* Postman (for testing)

---

  How to Run the Project

1. Open the project in NetBeans
2. Ensure Apache Tomcat is configured
3. Right-click project → Run
4. Open browser:

```
http://localhost:8080/SmartCampusAPI/api/v1/
```

---

  API Endpoints

Rooms

| Method | Endpoint           | Description    |
| ------ | ------------------ | -------------- |
| GET    | /api/v1/rooms      | Get all rooms  |
| POST   | /api/v1/rooms      | Create room    |
| GET    | /api/v1/rooms/{id} | Get room by ID |
| DELETE | /api/v1/rooms/{id} | Delete room    |

---

Sensors

| Method | Endpoint                         | Description     |
| ------ | -------------------------------- | --------------- |
| GET    | /api/v1/sensors                  | Get all sensors |
| GET    | /api/v1/sensors?type=Temperature | Filter sensors  |
| POST   | /api/v1/sensors                  | Create sensor   |

---

Readings (Sub-resource)

| Method | Endpoint                                | Description    |
| ------ | --------------------------------------- | -------------- |
| GET    | /api/v1/sensors/{id}/readings           | Get readings   |
| POST   | /api/v1/sensors/{id}/readings           | Add reading    |
| GET    | /api/v1/sensors/{id}/readings?from=&to= | Filter by time |
| GET    | /api/v1/sensors/{id}/readings/average   | Get average    |

---

Error Handling

The API uses structured JSON error responses.

| Scenario               | Status Code |
| ---------------------- | ----------- |
| Room not found         | 404         |
| Room has sensors       | 409         |
| Invalid room reference | 422         |
| Sensor in maintenance  | 403         |
| Unexpected error       | 500         |

Example:

```json
{
  "errorMessage": "Room has sensors assigned",
  "errorCode": 409
}
```

This ensures the API never returns raw server errors, which is a best practice in enterprise systems .

---

  Design Decisions

1. RESTful Principles

The API is designed using resource-based architecture:

* Rooms → Sensors → Readings
* Uses HTTP methods (GET, POST, DELETE)

This aligns with REST best practices required in the coursework .

---

2. Sub-resource Locator Pattern

Readings are nested under sensors:

```
/sensors/{id}/readings
```

This improves:

* Logical structure
* Maintainability
* Scalability

---

3. Query Parameters

Filtering is implemented using query parameters:

```
/sensors?type=Temperature
```

This is preferred over path-based filtering because it is more flexible for searching collections.

---

4. Error Handling Strategy

Custom exceptions and exception mappers are used to:

* Return meaningful messages
* Use correct HTTP status codes
* Avoid exposing internal errors

---

5. Logging

A logging filter is implemented to log:

* Incoming requests
* Outgoing responses

This improves system observability and debugging .

---

Video Demonstration

The video demonstrates:

* All endpoints
* Error handling
* Filtering
* Sub-resource functionality

---

Conclusion

This project demonstrates:

* Strong understanding of REST principles
* Practical use of JAX-RS
* Clean API design with proper validation and error handling

---

Sample cURL Commands

Create Room

```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"R1","name":"Library","capacity":100}'
```

Get Rooms

```
curl http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

Create Sensor

```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S1","type":"Temperature","status":"ACTIVE","roomId":"R1"}'
```

Add Reading

```
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/S1/readings \
-H "Content-Type: application/json" \
-d '{"value":26.5,"timestamp":1710000000000}'
```

---

Notes
* No database is used (as required)
* Data is stored using in-memory lists
* Designed to run on any machine with Tomcat

----------------------------------------------------------------------------------------------
  Coursework Answers

Question 1: JAX-RS Resource Lifecycle
In JAX-RS, resource classes are by default instantiated per request, meaning a new instance is created for each incoming HTTP request.

This design ensures:

Thread safety (no shared instance state)
Reduced risk of concurrency issues

However, in this project, shared data (Rooms, Sensors, Readings) is stored in static in-memory collections (e.g., ArrayList).

Because multiple requests can access these collections simultaneously, this introduces potential race conditions.

To mitigate this:

The API avoids complex concurrent modifications
Operations are kept simple (add/remove/search)

In a production system, synchronization or concurrent data structures would be required.

-----------------------------------------------------------------------------------------------

Question 2: HATEOAS (Hypermedia)

Hypermedia (HATEOAS) allows API responses to include links to related resources.

Example:

{
  "rooms": "/api/v1/rooms",
  "sensors": "/api/v1/sensors"
}

Benefits:

Clients do not need hardcoded URLs
API becomes self-discoverable
Easier to evolve API without breaking clients

Compared to static documentation, HATEOAS improves flexibility and usability.

------------------------------------------------------------------------------------------------

Question 3: Returning IDs vs Full Objects

Returning only IDs:

Reduces network usage
Faster responses
Requires additional requests from client

Returning full objects:

More convenient for client
Fewer API calls needed
Higher payload size

In this project, full objects are returned for simplicity and usability.

-------------------------------------------------------------------------------------------------

Question 4: Is DELETE Idempotent?

Yes, DELETE is idempotent.

In this implementation:
First DELETE → removes the room
Subsequent DELETE → returns "Room not found"

The result is consistent:
The room remains deleted

Therefore, the operation satisfies idempotency.

-------------------------------------------------------------------------------------------------

Question 5: @Consumes(JSON) Behavior

The annotation:

@Consumes(MediaType.APPLICATION_JSON)

means the API only accepts JSON input.

If a client sends:
text/plain
application/xml

JAX-RS will reject the request and return:
HTTP 415 – Unsupported Media Type

This ensures correct data format and prevents parsing errors.

--------------------------------------------------------------------------------------------------

Question 6: QueryParam vs PathParam for Filtering

Using query parameters:

/api/v1/sensors?type=Temperature

is better because:

Filtering is optional
Supports multiple filters easily
Cleaner and more flexible

Using path-based filtering:

/api/v1/sensors/type/Temperature

is less flexible and harder to extend.

Therefore, query parameters are preferred for searching and filtering.

--------------------------------------------------------------------------------------------------

Question 7: Sub-Resource Locator Benefits

The sub-resource locator pattern allows delegation:

@Path("/{id}/readings")
public ReadingResource getReadingResource(...)

Benefits:

Cleaner code structure
Separation of concerns
Easier maintenance
Scales better for large APIs

Without it, one class would become too large and complex.

-------------------------------------------------------------------------------------------------

Question 8: Why HTTP 422 instead of 404?

HTTP 422 is more accurate because:

The request format is valid JSON
But contains invalid data (non-existent roomId)

404 means resource not found at URL
422 means request cannot be processed

Thus, 422 better represents the error.

--------------------------------------------------------------------------------------------------

Question 9: Stack Trace Security Risk

Exposing stack traces is dangerous because attackers can learn:

Internal class names
File structure
Framework details
Potential vulnerabilities

This information can be used for targeted attacks.

Therefore, the API returns controlled error messages instead of raw exceptions.

--------------------------------------------------------------------------------------------------

Question 10: Why Use Filters for Logging?

Using JAX-RS filters:

ContainerRequestFilter
ContainerResponseFilter

is better because:

Centralized logging
Avoids duplicate code
Applies to all endpoints automatically

Compared to manual logging:

Cleaner design
Easier maintenance
Less error-prone

----------------------------------------------------------------------------------------------------
