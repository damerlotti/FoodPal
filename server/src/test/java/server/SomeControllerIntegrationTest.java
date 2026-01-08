package server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SomeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetRootReturnsHelloWorld() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello world!", response.getBody());
    }
}
