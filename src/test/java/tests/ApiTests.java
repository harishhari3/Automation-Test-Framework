package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTests {

    @BeforeClass
    public void setup() {
        // Set the Base URI for JSONPlaceholder API (no API key required)
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void testGetPost() {
        // GET request to retrieve a post and validate fields
        given()
            .when()
                .get("/posts/1")
            .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", containsString("sunt aut facere"))
                .body("body", notNullValue());
    }

    @Test
    public void testCreatePost() {
        // POST request to create a post
        String requestBody = "{\n" +
                "    \"title\": \"foo\",\n" +
                "    \"body\": \"bar\",\n" +
                "    \"userId\": 1\n" +
                "}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .body("title", equalTo("foo"))
            .body("body", equalTo("bar"))
            .body("userId", equalTo(1))
            .body("id", equalTo(101)); // JSONPlaceholder returns id 101 for newly created resource
    }

    @Test
    public void testUpdatePost() {
        // PUT request to update an existing post
        String requestBody = "{\n" +
                "    \"id\": 1,\n" +
                "    \"title\": \"foo updated\",\n" +
                "    \"body\": \"bar updated\",\n" +
                "    \"userId\": 1\n" +
                "}";

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .put("/posts/1")
        .then()
            .statusCode(200)
            .body("title", equalTo("foo updated"))
            .body("body", equalTo("bar updated"))
            .body("userId", equalTo(1));
    }

    @Test
    public void testDeletePost() {
        // DELETE request validation (JSONPlaceholder returns 200 OK on success)
        given()
        .when()
            .delete("/posts/1")
        .then()
            .statusCode(200);
    }
}
