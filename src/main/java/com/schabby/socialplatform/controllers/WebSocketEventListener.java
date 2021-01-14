package com.schabby.socialplatform.controllers;

import com.schabby.socialplatform.models.ChatMessage;
import static java.lang.String.format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        
        /*
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        GenericMessage connectHeader = (GenericMessage)stompAccessor.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) connectHeader.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
        
        String chatInitiator = nativeHeaders.get("username").toString();
*/
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
      StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

      String username = (String) headerAccessor.getSessionAttributes().get("username");
      String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
      if (username != null) {
        logger.info("User Disconnected: " + username);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender(username);

        messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
      }
    }
}