package com.deloitte.service; //package com.deloitte.service;

import com.deloitte.service.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class CamundaTaskService {

    @Value("${camunda.tasklist-url}")
    private String taskListBaseUrl;

    @Value("${camunda.form-url}")
    private String formUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TaskListDto> fetchTasks(String accessToken) {
        // Create a list to hold the DTOs
        List<TaskListDto> taskList = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // Create the DTO
        TaskSearchRequestDto requestDto = new TaskSearchRequestDto();
        SortCriteria sortCriteria = new SortCriteria();
        sortCriteria.setField("creationTime");
        sortCriteria.setOrder("DESC");
        requestDto.setSort(List.of(sortCriteria));
        requestDto.setPageSize(50);
        requestDto.setState("CREATED");

        // Convert DTO to JSON
        String requestBody = convertToJson(requestDto);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(taskListBaseUrl + "/search", HttpMethod.POST, requestEntity, String.class);

        // Process the response
        String jsonResponse = response.getBody();
        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse JSON response into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Iterate over the JSON array
            for (JsonNode node : rootNode) {
                TaskListDto tl = new TaskListDto();
                tl.setId(node.get("id").asText());
                tl.setTaskDefinitionId(node.get("taskDefinitionId").asText());
                tl.setName(node.get("name").asText());
                tl.setProcessName(node.get("processName").asText());
                tl.setTaskState(node.get("taskState").asText());
                tl.setAssignee(node.get("assignee").asText());
                tl.setFormId(node.get("formId").asText());
                tl.setProcessDefinitionKey(node.get("processDefinitionKey").asText());
                tl.setFormVersion(node.get("formVersion").asText());
                taskList.add(tl);
            }

            // Print the list of DTOs
            for (TaskListDto dto : taskList) {
                System.out.println(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return taskList;
    }

    private String convertToJson(TaskSearchRequestDto requestDTO) {
        try {
            return objectMapper.writeValueAsString(requestDTO);
        } catch (Exception e) {
            throw new RuntimeException("Error converting DTO to JSON", e);
        }
    }

    public String getFormSchema(String accessToken, String formId, String processDefinitionKey, Integer version) {
        RestTemplate restTemplate = new RestTemplate();
        String schema = null;

        // Construct the request URL with query parameters
        String requestUrl = String.format("%s/%s?processDefinitionKey=%s&version=%d", formUrl, formId, processDefinitionKey, version);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Make the GET request
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, String.class);

        // Process the response
        String jsonResponse = response.getBody();
        System.out.println(jsonResponse);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse JSON response
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Extract the "schema" field
            schema = rootNode.get("schema").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return schema;
    }

    //    public void assignTask(String accessToken, String taskId, AssignmentDto assignmentDto) {
    //        RestTemplate restTemplate = new RestTemplate();
    //
    //        // Construct the request URL
    //        String requestUrl = String.format("%s/%s/assign", taskListBaseUrl, taskId);
    //
    //        // Set up the headers
    //        HttpHeaders headers = new HttpHeaders();
    //        headers.setContentType(MediaType.APPLICATION_JSON);
    //        headers.set("Authorization", "Bearer " + accessToken);
    //
    //        // Create the request body with assignment data
    //        ObjectMapper objectMapper = new ObjectMapper();
    //        String requestBody;
    //
    //        try {
    //            // Convert AssignmentDto to JSON
    //            requestBody = objectMapper.writeValueAsString(assignmentDto);
    //        } catch (JsonProcessingException e) {
    //            throw new RuntimeException("Error serializing assignmentDto to JSON", e);
    //        }
    //
    //        // Create the request entity
    //        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    //
    //        // Make the PATCH request to assign the task
    //        ResponseEntity<Void> response;
    //        try {
    //            response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, requestEntity, Void.class);
    //        } catch (HttpClientErrorException | HttpServerErrorException e) {
    //            throw new RuntimeException("Error during PATCH request: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
    //        } catch (ResourceAccessException e) {
    //            throw new RuntimeException("I/O error during PATCH request: " + e.getMessage(), e);
    //        }
    //
    //        // Check the response status
    //        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
    //            throw new RuntimeException("Failed to assign task: " + response.getStatusCode());
    //        }
    //    }

    public void assignTask(String accessToken, String taskId, AssignmentDto assignmentDto, String userId) {
        String requestUrl = String.format("%s/%s/assign", taskListBaseUrl, taskId);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(requestUrl);
            httpPatch.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpPatch.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(assignmentDto);
            httpPatch.setEntity(new StringEntity(json, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                // Check for 200 OK or 204 No Content
                if (response.getCode() != HttpStatus.SC_OK && response.getCode() != HttpStatus.SC_NO_CONTENT) {
                    throw new RuntimeException("Failed to assign task: " + response.getReasonPhrase());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error assigning task", e);
        }
    }

    public void completeTask(String accessToken, String taskId, List<VariableDto> variables, String userId) {
        String requestUrl = String.format("%s/%s/complete", taskListBaseUrl, taskId);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPatch httpPatch = new HttpPatch(requestUrl);
            httpPatch.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            httpPatch.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(variables);
            httpPatch.setEntity(new StringEntity(json, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
                // Check for 200 OK or 204 No Content
                if (response.getCode() != HttpStatus.SC_OK && response.getCode() != HttpStatus.SC_NO_CONTENT) {
                    throw new RuntimeException("Failed to assign task: " + response.getReasonPhrase());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error assigning task", e);
        }
    }
}
