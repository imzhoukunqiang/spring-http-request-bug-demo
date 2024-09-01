package com.github.imzhoukunqiang.spring_http_request_bug_demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangeRequestBodyTests {

    RestTemplate restTemplate;
    RestClient restClient;

    @LocalServerPort
    private int port;

    // length = 12
    String requestBody = "request body";

    @Test
    public void restTemplateTest() {
        String url = "http://localhost:" + this.port + "/echo";
        ResponseEntity<String> response = getRestTemplate().postForEntity(url, requestBody.getBytes(StandardCharsets.UTF_8), String.class);
        /**
         * org.opentest4j.AssertionFailedError:
         * Expected :changed requ
         * Actual   :request body
         */
        Assertions.assertEquals(response.getBody(), requestBody);
    }

    @Test
    public void restClientTest() {
        String url = "http://localhost:" + this.port + "/echo";
        String requestBody = "request body";// length = 12
        ResponseEntity<String> response = getRestClient().post()
                .uri(url)
                .body(requestBody.getBytes(StandardCharsets.UTF_8))
                .retrieve()
                .toEntity(String.class);
        /**
         * org.opentest4j.AssertionFailedError:
         * Expected :changed requ
         * Actual   :request body
         */
        Assertions.assertEquals(response.getBody(), requestBody);
    }

    public RestTemplate getRestTemplate() {
        if (this.restTemplate == null) {
            this.restTemplate = new RestTemplateBuilder()
                    .requestFactory(() -> new ReactorNettyClientRequestFactory())
                    .interceptors(getClientHttpRequestInterceptor())
                    .build();
        }
        return this.restTemplate;
    }

    public RestClient getRestClient() {
        if (this.restTemplate == null) {
            this.restClient = RestClient.builder()
                    .requestFactory(new ReactorNettyClientRequestFactory())
                    .requestInterceptor(getClientHttpRequestInterceptor())
                    .build();
        }
        return this.restClient;
    }

    private static ClientHttpRequestInterceptor getClientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            // The actual sent is 20 bytes, which is longer than the original length
            // but the server only received 12 bytes
            return execution.execute(request, "changed request body".getBytes(StandardCharsets.UTF_8));
        };
    }
}
