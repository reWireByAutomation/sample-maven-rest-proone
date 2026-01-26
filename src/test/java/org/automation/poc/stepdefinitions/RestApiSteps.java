package org.automation.poc.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.hamcrest.Matchers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class RestApiSteps {

    private static final Logger log = LoggerFactory.getLogger(RestApiSteps.class);

    private Response response;

    private RequestSpecification getRequestSpec() {
        log.debug("Building request specification (baseUri={})", RestAssured.baseURI);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .log().all();
    }

    // Background step implementation: sets base URI and logs
    @Given("I set the base URI to {string}")
    public void setBaseUri(String baseUri) {
        if (baseUri == null || baseUri.trim().isEmpty()) {
            log.error("Base URI missing in feature Background");
            throw new IllegalArgumentException("Base URI must be provided in the feature Background");
        }
        RestAssured.baseURI = baseUri.trim();
        log.info("[Cucumber] Base URI set to: {}", RestAssured.baseURI);
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        log.info("Sending GET request to {}", endpoint);
        response = getRequestSpec()
                .when()
                .get(endpoint);
        log.debug("Received response with status: {}", response.getStatusCode());
    }

    @When("I send a POST request to {string} with body:")
    public void sendPostRequest(String endpoint, String body) {
        log.info("Sending POST request to {} with body: {}", endpoint, body);
        response = getRequestSpec()
                .body(body)
                .when()
                .post(endpoint);
        log.debug("Received response with status: {}", response.getStatusCode());
    }

    @When("I send a PUT request to {string} with body:")
    public void sendPutRequest(String endpoint, String body) {
        log.info("Sending PUT request to {} with body: {}", endpoint, body);
        response = getRequestSpec()
                .body(body)
                .when()
                .put(endpoint);
        log.debug("Received response with status: {}", response.getStatusCode());
    }

    @When("I send a DELETE request to {string}")
    public void sendDeleteRequest(String endpoint) {
        log.info("Sending DELETE request to {}", endpoint);
        response = getRequestSpec()
                .when()
                .delete(endpoint);
        log.debug("Received response with status: {}", response.getStatusCode());
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        if (response == null) {
            Assert.fail("Response is null. Did you send the request?");
        }
        log.info("Verifying status code equals {}", expectedStatusCode);
        int actual = response.getStatusCode();
        response.then().statusCode(expectedStatusCode);
        Assert.assertEquals(actual, expectedStatusCode, "Status code mismatch");
    }

    @Then("the response should contain {string}")
    public void verifyResponseContains(String expectedContent) {
        if (response == null) {
            Assert.fail("Response is null. Did you send the request?");
        }
        log.info("Verifying response contains: {}", expectedContent);
        String body = response.getBody().asString();
        response.then().body(containsString(expectedContent));
        Assert.assertTrue(body.contains(expectedContent), "Response body does not contain expected content: " + expectedContent);
    }

    @Then("the response JSON should have field {string} with value {string}")
    public void verifyJsonField(String field, String value) {
        if (response == null) {
            Assert.fail("Response is null. Did you send the request?");
        }
        log.info("Verifying JSON field {} equals {}", field, value);
        // Convert expected value to a proper type if possible (number or boolean), otherwise treat as string
        Object expectedObj = value;
        try {
            if (value != null) {
                String trimmed = value.trim();
                if (trimmed.equalsIgnoreCase("true") || trimmed.equalsIgnoreCase("false")) {
                    expectedObj = Boolean.valueOf(trimmed);
                } else if (trimmed.matches("^-?\\d+$")) {
                    // integer
                    expectedObj = Integer.valueOf(trimmed);
                } else if (trimmed.matches("^-?\\d+\\.\\d+$")) {
                    expectedObj = Double.valueOf(trimmed);
                }
            }
        } catch (Exception e) {
            log.debug("Could not coerce expected value to number/bool - treating as string: {}", value);
            expectedObj = value;
        }

        // Use RestAssured check first; then TestNG assert using the extracted value
        response.then().body(field, equalTo(expectedObj));

        // Extract from response and assert via TestNG for stronger failure messages
        Object actualValue = response.jsonPath().get(field);
        // actualValue may be Integer/Float/Double/Boolean/String etc.
        Assert.assertEquals(actualValue, expectedObj, "JSON field value mismatch for field: " + field);
    }

    @Then("the response should be valid JSON")
    public void verifyValidJson() {
        if (response == null) {
            Assert.fail("Response is null. Did you send the request?");
        }
        log.info("Verifying response content type is application/json");
        response.then()
                .contentType("application/json");
        String ct = response.getContentType();
        Assert.assertTrue(ct != null && ct.contains("application/json"), "Content-Type is not application/json: " + ct);
    }

}
