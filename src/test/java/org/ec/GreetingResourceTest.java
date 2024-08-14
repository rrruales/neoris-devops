package org.ec;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class GreetingResourceTest {

    Greeting greeting = new Greeting("This is a test", "Juan Perez", "Rita Asturia", 45);

    @Test
    void testUnauthorized() {
        given()
                .when().get("/DevOps")
                .then()
                .statusCode(401);
    }

    @Test
    void testHelloEndpoint() {
        // String message = "Hello " + greeting.to + " your message will be send";
        given()
                .header("X-Parse-REST-API-Key", "2f5ae96c-b558-4c7b-a590-a501ae1c3f6c")
                .contentType(MediaType.APPLICATION_JSON)
                .body(greeting)
                .when().post("/DevOps")
                .then()
                .statusCode(401);
                // .body("message", is(message));
    }

    @Test
    void testGetEndpoint() {
        given()
                .header("X-Parse-REST-API-Key", "2f5ae96c-b558-4c7b-a590-a501ae1c3f6c")
                .when().get("/DevOps")
                .then()
                .statusCode(200)
                .body(is("ERROR"));
    }

    @Test
    void testPutEndpoint() {
        given()
                .header("X-Parse-REST-API-Key", "2f5ae96c-b558-4c7b-a590-a501ae1c3f6c")
                .when().put("/DevOps")
                .then()
                .statusCode(200)
                .body(is("ERROR"));
    }

    @Test
    void testDeleteEndpoint() {
        given()
                .header("X-Parse-REST-API-Key", "2f5ae96c-b558-4c7b-a590-a501ae1c3f6c")
                .when().delete("/DevOps")
                .then()
                .statusCode(200)
                .body(is("ERROR"));
    }

}