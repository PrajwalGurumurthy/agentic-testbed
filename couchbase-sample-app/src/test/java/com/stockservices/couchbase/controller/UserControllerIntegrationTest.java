package com.stockservices.couchbase.controller;

import com.stockservices.couchbase.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {

    private static final String BUCKET_NAME = "sample_bucket";

    @Container
    static CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase/server:7.2.2")
            .withBucket(new BucketDefinition(BUCKET_NAME))
            .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void couchbaseProperties(DynamicPropertyRegistry registry) {
        registry.add("couchbase.connectionString", couchbaseContainer::getConnectionString);
        registry.add("couchbase.username", couchbaseContainer::getUsername);
        registry.add("couchbase.password", couchbaseContainer::getPassword);
        registry.add("couchbase.bucketName", () -> BUCKET_NAME);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setupCouchbase() throws Exception {
        // Create primary index for N1QL queries
        couchbaseContainer.execInContainer(
            "cbq", "-e", "http://127.0.0.1:8093", "-u", couchbaseContainer.getUsername(),
            "-p", couchbaseContainer.getPassword(),
            "-s", "CREATE PRIMARY INDEX ON `" + BUCKET_NAME + "`"
        );
    }

    @Test
    void testMonoBlockAndFuture() {
        // Test POST /block
        User userToSave = new User(UUID.randomUUID().toString(), "John Doe", "john@example.com");
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/api/users/block", userToSave, User.class);

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertEquals("John Doe", postResponse.getBody().getName());

        // Test GET /{id}/future
        ResponseEntity<User> getResponse = restTemplate.getForEntity("/api/users/" + userToSave.getId() + "/future", User.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("John Doe", getResponse.getBody().getName());
    }

    @Test
    void testMonoCallback() {
        User userToSave = new User(UUID.randomUUID().toString(), "Jane Doe", "jane@example.com");
        ResponseEntity<User> postResponse = restTemplate.postForEntity("/api/users/callback", userToSave, User.class);

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertEquals("Jane Doe", postResponse.getBody().getName());
    }

    @Test
    void testFluxEndpoints() throws InterruptedException {
        // Save a couple of users first
        restTemplate.postForEntity("/api/users/block", new User(UUID.randomUUID().toString(), "Flux User 1", "f1@example.com"), User.class);
        restTemplate.postForEntity("/api/users/block", new User(UUID.randomUUID().toString(), "Flux User 2", "f2@example.com"), User.class);

        // Wait a tiny bit for the primary index to update in Couchbase
        Thread.sleep(1000);

        // Test GET /flux/block
        ResponseEntity<List<User>> blockResponse = restTemplate.exchange(
                "/api/users/flux/block",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, blockResponse.getStatusCode());
        assertNotNull(blockResponse.getBody());
        assertTrue(blockResponse.getBody().size() >= 2);

        // Test GET /flux/future
        ResponseEntity<List<User>> futureResponse = restTemplate.exchange(
                "/api/users/flux/future",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, futureResponse.getStatusCode());
        assertNotNull(futureResponse.getBody());
        assertTrue(futureResponse.getBody().size() >= 2);

        // Test GET /flux/callback
        ResponseEntity<List<User>> callbackResponse = restTemplate.exchange(
                "/api/users/flux/callback",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );

        assertEquals(HttpStatus.OK, callbackResponse.getStatusCode());
        assertNotNull(callbackResponse.getBody());
        assertTrue(callbackResponse.getBody().size() >= 2);
    }

    @Test
    void testLegacySimulations() {
        ResponseEntity<String> monoResponse = restTemplate.getForEntity("/api/users/legacy-simulate/mono", String.class);
        assertEquals(HttpStatus.OK, monoResponse.getStatusCode());
        assertEquals("legacy-mono-result", monoResponse.getBody());

        ResponseEntity<List<String>> fluxResponse = restTemplate.exchange(
                "/api/users/legacy-simulate/flux",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );
        assertEquals(HttpStatus.OK, fluxResponse.getStatusCode());
        assertNotNull(fluxResponse.getBody());
        assertEquals(2, fluxResponse.getBody().size());
        assertEquals("legacy-flux-1", fluxResponse.getBody().get(0));
    }
}
