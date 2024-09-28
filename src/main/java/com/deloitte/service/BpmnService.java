package com.deloitte.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BpmnService {

    private static final Logger LOG = LoggerFactory.getLogger(BpmnService.class);

    public String processCsvToBpmn(MultipartFile file) throws IOException {
        // Generate a random process ID
        String processId = "Process_" + generateShortId();
        BpmnModelInstance modelInstance = Bpmn.createExecutableProcess(processId).done();

        // Add definitions and collaboration header
        Definitions definitions = modelInstance.getDefinitions();
        definitions.setId("definitions_" + generateShortId());
        definitions.setTargetNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");

        // Add collaboration with random ID
        Collaboration collaboration = modelInstance.newInstance(Collaboration.class);
        collaboration.setId("Collaboration_" + generateShortId());
        definitions.addChildElement(collaboration);

        // Add participant with random ID
        Participant participant = modelInstance.newInstance(Participant.class);
        participant.setId("Participant_" + generateShortId());
        //participant.setName("Bank Process");
        // Set process reference directly using an attribute
        participant.setAttributeValue("processRef", processId);
        collaboration.addChildElement(participant);

        //        // Create the Process instance
        //        Process process = modelInstance.newInstance(Process.class);
        //        process.setId(processId);
        //        definitions.addChildElement(process);

        // Store flow nodes and their corresponding sequence flows
        Map<String, FlowNode> nodes = new HashMap<>();
        List<SequenceFlow> listSequenceFlow = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] header = csvReader.readNext();

            if (header == null || header.length != 7) {
                throw new RuntimeException(
                    "CSV header must contain exactly seven columns: ID, Name, Type, Incoming, Outgoing, SourceNode, TargetNode"
                );
            }

            // Trim header values
            for (int i = 0; i < header.length; i++) {
                header[i] = header[i].trim();
            }

            // Log the header for debugging
            LOG.info("CSV Header: {}", Arrays.toString(header));

            String[] values;
            while ((values = csvReader.readNext()) != null) {
                if (values.length != 7) {
                    throw new RuntimeException("Each row must contain exactly seven values.");
                }

                String id = values[0];
                String name = values[1].replace("\"", "");
                String type = values[2];
                String incoming = values[3].isEmpty() ? null : values[3];
                String outgoing = values[4].isEmpty() ? null : values[4];
                String sourceNodeId = values[5].isEmpty() ? null : values[5];
                String targetNodeId = values[6].isEmpty() ? null : values[6];

                FlowNode flowNode = createElement(
                    modelInstance,
                    modelInstance.getModelElementsByType(Process.class).iterator().next(),
                    id,
                    name,
                    type,
                    incoming,
                    outgoing,
                    sourceNodeId,
                    targetNodeId
                );
                if (flowNode != null) {
                    nodes.put(id, flowNode);
                } else {
                    LOG.warn("Failed to create flow node for ID: {}", id);
                }

                // Create sequence flows after all nodes are created
                if (sourceNodeId != null && targetNodeId != null) {
                    FlowNode sourceNode = nodes.get(sourceNodeId);
                    FlowNode targetNode = nodes.get(targetNodeId);

                    SequenceFlow sf = createSequenceFlow(modelInstance, participant.getProcess(), sourceNode, targetNode, id);
                    listSequenceFlow.add(sf);

                    participant.getProcess().addChildElement(sf);
                    //                    // Add incoming and outgoing references for the nodes
                    //                    sourceNode.addChildElement(createOutgoing(modelInstance, sf.getId()));
                    //                    targetNode.addChildElement(createIncoming(modelInstance, sf.getId()));
                }
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException("CSV validation error: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Error processing CSV: {}", e.getMessage());
            throw new RuntimeException("File processing error", e);
        }

        LOG.info("BPMN XML generated successfully.");
        return Bpmn.convertToString(modelInstance);
    }

    private FlowNode createElement(
        BpmnModelInstance modelInstance,
        Process process,
        String id,
        String name,
        String type,
        String incoming,
        String outgoing,
        String sourceNodeId,
        String targetNodeId
    ) {
        FlowNode flowNode = null;

        switch (type) {
            case "StartEvent":
                StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
                startEvent.setId(id);
                startEvent.setName(name);
                process.addChildElement(startEvent);
                flowNode = startEvent;
                break;
            case "UserTask":
                UserTask userTask = modelInstance.newInstance(UserTask.class);
                userTask.setId(id);
                userTask.setName(name);
                process.addChildElement(userTask);
                flowNode = userTask;
                break;
            case "BusinessRuleTask":
                BusinessRuleTask businessRuleTask = modelInstance.newInstance(BusinessRuleTask.class);
                businessRuleTask.setId(id);
                businessRuleTask.setName(name);
                process.addChildElement(businessRuleTask);
                flowNode = businessRuleTask;
                break;
            case "EndEvent":
                EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
                endEvent.setId(id);
                endEvent.setName(name);
                process.addChildElement(endEvent);
                flowNode = endEvent;
                break;
            default:
                LOG.warn("Unknown task type: {}", type);
        }

        // Add incoming and outgoing references
        if (incoming != null) {
            flowNode.addChildElement(createIncoming(modelInstance, incoming));
        }
        if (outgoing != null) {
            flowNode.addChildElement(createOutgoing(modelInstance, outgoing));
        }

        return flowNode;
    }

    private SequenceFlow createSequenceFlow(
        BpmnModelInstance modelInstance,
        Process process,
        FlowNode sourceNode,
        FlowNode targetNode,
        String flowId
    ) {
        //        // Check if sourceNode and targetNode are not null
        //        if (sourceNode == null || targetNode == null) {
        //            LOG.warn("Cannot create sequence flow: one of the nodes is null. Source: {}, Target: {}", sourceNode, targetNode);
        //            return null;
        //        }

        SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
        sequenceFlow.setId(flowId);

        // Set source and target directly from the FlowNode instances
        sequenceFlow.setSource(sourceNode);
        sequenceFlow.setTarget(targetNode);

        //        // Adding incoming and outgoing references
        //        sourceNode.getOutgoing().add(sequenceFlow);
        //        targetNode.getIncoming().add(sequenceFlow);

        //        // Add incoming and outgoing elements directly
        //        targetNode.addChildElement(createIncoming(modelInstance, flowId));
        //        sourceNode.addChildElement(createOutgoing(modelInstance, flowId));

        LOG.info("Created sequence flow: {} from {} to {}", flowId, sourceNode.getId(), targetNode.getId());

        return sequenceFlow;
    }

    private Incoming createIncoming(BpmnModelInstance modelInstance, String flowId) {
        Incoming incoming = modelInstance.newInstance(Incoming.class);
        incoming.setTextContent(flowId); // Add flow ID as text content
        return incoming;
    }

    private Outgoing createOutgoing(BpmnModelInstance modelInstance, String flowId) {
        Outgoing outgoing = modelInstance.newInstance(Outgoing.class);
        outgoing.setTextContent(flowId); // Add flow ID as text content
        return outgoing;
    }

    private String generateShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }
}
