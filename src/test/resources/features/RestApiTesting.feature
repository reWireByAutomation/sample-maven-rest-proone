@smoke
Feature: REST API Testing POC
  As a test automation engineer
  I want to test REST APIs using RestAssured with Cucumber
  So that I can validate API responses and behavior

  Background:
    Given I set the base URI to "https://jsonplaceholder.typicode.com"

  Scenario: Get all posts
    When I send a GET request to "/posts"
    Then the response status code should be 200
    And the response should be valid JSON
    And the response should contain "userId"

  Scenario: Get a specific post
    When I send a GET request to "/posts/1"
    Then the response status code should be 200
    And the response JSON should have field "id" with value "1"
    And the response JSON should have field "userId" with value "1"

  Scenario: Create a new post
    When I send a POST request to "/posts" with body:
    """
    {
      "title": "Test Post",
      "body": "This is a test post body",
      "userId": 1
    }
    """
    Then the response status code should be 201
    And the response JSON should have field "title" with value "Test Post"
    And the response JSON should have field "body" with value "This is a test post body"

  Scenario: Update an existing post
    When I send a PUT request to "/posts/1" with body:
    """
    {
      "id": 1,
      "title": "Updated Test Post",
      "body": "This is an updated test post body",
      "userId": 1
    }
    """
    Then the response status code should be 200
    And the response JSON should have field "title" with value "Updated Test Post"

  Scenario: Delete a post
    When I send a DELETE request to "/posts/1"
    Then the response status code should be 200
