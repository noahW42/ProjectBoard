package com.noah.projectboard.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BoardWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public BoardWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/project/{projectId}")
    public void handleBoardEvent(
            @DestinationVariable String projectId,
            String event) {
        messagingTemplate.convertAndSend("/topic/project/" + projectId, event);
    }
}