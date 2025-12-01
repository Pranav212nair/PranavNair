package com.healthrx.bajaj.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthrx.bajaj.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    // Use the details the assignment asked for
    private final String name = "Pranav Nair";
    private final String regNo = "22BCE8728"; // change if needed
    private final String email = "pranav.22bce8728@vitapstudent.ac.in";

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void performFlow() {
        try {
            WebhookResponse resp = generateWebhook();
            if (resp == null || resp.getWebhook() == null || resp.getAccessToken() == null) {
                logger.error("Invalid response from generateWebhook: {}", resp);
                return;
            }

            String finalQuery = solveQuestionForRegNo(regNo);
            storeSolution(finalQuery);
            postFinalQuery(resp.getWebhook(), resp.getAccessToken(), finalQuery);

        } catch (Exception e) {
            logger.error("Error performing flow", e);
        }
    }

    private WebhookResponse generateWebhook() {
        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("regNo", regNo);
            body.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            logger.info("POST {} -> generateWebhook (startup)", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode node = mapper.readTree(response.getBody());
                WebhookResponse wr = new WebhookResponse();
                if (node.has("webhook")) wr.setWebhook(node.get("webhook").asText());
                if (node.has("accessToken")) wr.setAccessToken(node.get("accessToken").asText());
                logger.info("Received webhook URL: {}", wr.getWebhook());
                return wr;
            } else {
                logger.error("generateWebhook returned non-OK: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Exception calling generateWebhook", e);
        }
        return null;
    }

    private String solveQuestionForRegNo(String regNo) {
        // Last two digits determine question
        String digits = regNo.replaceAll("[^0-9]", "");
        int lastTwo = 0;
        if (digits.length() >= 2) {
            lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        } else if (digits.length() > 0) {
            lastTwo = Integer.parseInt(digits);
        }

        boolean isOdd = (lastTwo % 2) == 1;
        logger.info("regNo '{}' -> lastTwo {} -> {}", regNo, lastTwo, (isOdd ? "ODD" : "EVEN"));

        // Provide a sample SQL for each question. Replace these with actual computed SQLs if needed.
        if (isOdd) {
            // Question 1 (odd regNo)
            String sql = "-- Solution for Question 1 (odd regNo)\n"
                    + "SELECT customer_id, COUNT(*) AS orders_count\n"
                    + "FROM orders\n"
                    + "GROUP BY customer_id\n"
                    + "HAVING COUNT(*) > 5;";
            return sql;
        } else {
            // Question 2 (even regNo)
            String sql = "-- Solution for Question 2 (even regNo)\n"
                    + "SELECT e.employee_id, e.name\n"
                    + "FROM employees e\n"
                    + "WHERE e.salary > (SELECT AVG(salary) FROM employees);";
            return sql;
        }
    }

    private void storeSolution(String finalQuery) {
        try {
            File targetDir = new File("target");
            if (!targetDir.exists()) targetDir.mkdirs();
            File out = new File(targetDir, "solution.sql");
            try (FileWriter fw = new FileWriter(out, false)) {
                fw.write("-- Generated on: " + LocalDateTime.now() + "\n\n");
                fw.write(finalQuery);
            }
            logger.info("Solution stored to {}", out.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to store solution", e);
        }
    }

    private void postFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        try {
            // Endpoint from instructions
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

            Map<String, String> body = new HashMap<>();
            body.put("finalQuery", finalQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Use JWT in Authorization header; prefix with Bearer to be standard
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            logger.info("POST {} -> sending final query to webhook", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            logger.info("Final POST responded with status: {}", response.getStatusCode());
            if (response.getBody() != null) {
                logger.info("Response body: {}", response.getBody());
            }
        } catch (Exception e) {
            logger.error("Exception posting final query to webhook", e);
        }
    }
}
