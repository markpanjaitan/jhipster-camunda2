package com.deloitte.web.rest;

import com.deloitte.security.AuthoritiesConstants;
import com.deloitte.service.CamundaAuthService;
import com.deloitte.service.CamundaTaskService;
import com.deloitte.service.dto.AssignmentDto;
import com.deloitte.service.dto.TaskListDto;
import com.deloitte.service.dto.VariableDto;
import com.deloitte.service.dto.VariableWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task-lists")
public class TaskListResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskListResource.class);

    private static final String ENTITY_NAME = "taskList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CamundaAuthService authService;
    private final CamundaTaskService taskService;

    public TaskListResource(CamundaAuthService authService, CamundaTaskService taskService) {
        this.authService = authService;
        this.taskService = taskService;
    }

    /**
     * {@code GET  /task-lists} : get all the taskLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskLists in body.
     */
    @GetMapping("")
    public List<TaskListDto> getAllTaskLists(Principal principal) {
        LOG.debug("REST request to get all TaskLists");
        //return taskListRepository.findAll();

        List<TaskListDto> tasksLists = new ArrayList<>();
        try {
            // Fetch the access token
            String accessToken = authService.getAccessToken();

            List<String> roles = extractRoles(principal);
            // Normalize roles to match candidate groups
            List<String> normalizedRoles = normalizeRoles(roles);

            // Fetch tasks from Camunda
            tasksLists = taskService.fetchTasks(accessToken, normalizedRoles);
            // Return the tasks as the response
            //return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            // Handle exceptions and return appropriate HTTP status
            //return ResponseEntity.status(500).body("Error fetching tasks: " + e.getMessage());

            LOG.error("Error fetching tasks: {}", e.getMessage(), e);
        }

        return tasksLists;
    }

    @GetMapping("/form/{formId}")
    public ResponseEntity<String> getFormById(
        @PathVariable("formId") String formId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("version") Integer version
    ) {
        LOG.debug(
            "REST request to get Form By Form ID: {}, Process Definition Key: {}, Version: {}",
            formId,
            processDefinitionKey,
            version
        );

        String formHtml;
        try {
            // Fetch the access token
            String accessToken = authService.getAccessToken();

            // Fetch tasks from Camunda with the new parameters
            formHtml = taskService.getFormSchema(accessToken, formId, processDefinitionKey, version);

            // Create headers if needed
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "YourValue"); // Example header

            return ResponseEntity.ok().headers(headers).body(formHtml);
        } catch (Exception e) {
            LOG.error("Error fetching tasks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tasks: " + e.getMessage());
        }
    }

    /**
     * {@code PATCH  /tasks/{taskId}/assign} : Assign an assignee to a task.
     *
     * @param taskId the id of the task to assign
     * @param principal the current user's authentication details
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} or {@code 400 (Bad Request)} if the assignment fails
     */
    @PatchMapping("/tasks/{taskId}/assign")
    public ResponseEntity<String> assignTask(@PathVariable String taskId, Principal principal) {
        LOG.debug("REST request to assign task: {}", taskId);

        try {
            // Get the user ID from the Principal
            String userId = principal.getName(); // Assuming the user ID is the principal name

            // Fetch the access token
            String accessToken = authService.getAccessToken();

            AssignmentDto assignmentDto = new AssignmentDto();
            assignmentDto.setAssignee(principal.getName());
            assignmentDto.setAllowOverrideAssignment(true);
            // Call service to assign the task, passing the userId if needed
            taskService.assignTask(accessToken, taskId, assignmentDto, userId);

            return ResponseEntity.ok("Task assigned successfully: " + taskId);
        } catch (Exception e) {
            LOG.error("Error assigning task: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error assigning task: " + e.getMessage());
        }
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public ResponseEntity<String> completeTask(@PathVariable String taskId, @RequestBody String strVariables, Principal principal) {
        LOG.debug("REST request to complete task: {}", taskId);

        try {
            // Get the user ID from the Principal
            String userId = principal.getName();

            // Fetch the access token if needed
            String accessToken = authService.getAccessToken();

            //            // Convert strVariables JSON string to VariableWrapper
            //            ObjectMapper objectMapper = new ObjectMapper();
            //            VariableWrapper variableWrapper = objectMapper.readValue(strVariables, VariableWrapper.class);
            //            List<VariableDto> variables = variableWrapper.getVariables();

            // Call service to complete the task with variables
            taskService.completeTask(accessToken, taskId, strVariables, userId);

            return ResponseEntity.ok("Task completed successfully: " + taskId);
        } catch (Exception e) {
            LOG.error("Error completing task: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error completing task: " + e.getMessage());
        }
    }

    private List<String> extractRoles(Principal principal) {
        // Assuming principal is of type OAuth2AuthenticationToken
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;

            // Get the authorities and extract role names
            return authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<String> normalizeRoles(List<String> roles) {
        List<String> normalized = new ArrayList<>();
        for (String role : roles) {
            switch (role) {
                case AuthoritiesConstants.ASSIGNEE:
                    normalized.add("assignee");
                    break;
                case AuthoritiesConstants.APPROVER:
                    normalized.add("approver");
                    break;
                case AuthoritiesConstants.USER:
                    normalized.add("user");
                    break;
                case AuthoritiesConstants.ADMIN:
                    normalized.add("admin");
                    break;
                // Add more mappings if necessary
                default:
                    normalized.add(role); // Add the role as-is if no mapping exists
            }
        }
        return normalized;
    }
}
