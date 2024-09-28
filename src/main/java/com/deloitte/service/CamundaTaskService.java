package com.deloitte.service; //package com.deloitte.service;

import com.deloitte.service.dto.FormResponseDto;
import com.deloitte.service.dto.SortCriteria;
import com.deloitte.service.dto.TaskListDto;
import com.deloitte.service.dto.TaskSearchRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CamundaTaskService {

    @Value("${camunda.tasklist-url}")
    private String taskListUrl;

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

        ResponseEntity<String> response = restTemplate.exchange(taskListUrl, HttpMethod.POST, requestEntity, String.class);

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
}
