package com.introlabsystems.controllers;


import com.introlabsystems.domain.Article;
import com.introlabsystems.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Controller
public class WebsocketController {
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketController.class);

    private List<Article> archivalTenArticles = new ArrayList<>();
    private Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap();

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private NewsService newsService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        if (archivalTenArticles.isEmpty()) {
            archivalTenArticles = newsService.getLastTenArticles();
        }
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(sendNewArticles(), 10_000);//start
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        tasks.put(sessionId, scheduledFuture);
        LOG.info("Open new websocket, sessionId={}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        ScheduledFuture<?> scheduledFuture = tasks.get(sessionId);
        scheduledFuture.cancel(false);
        tasks.remove(sessionId);
        LOG.info("Close websocket, sessionId={}", sessionId);
    }

    private Runnable sendNewArticles() {
        return () -> {
            List<Article> lastTenArticles = newsService.getLastTenArticles();
            ArrayList<Article> newArticles = new ArrayList<>(lastTenArticles);
            newArticles.removeAll(archivalTenArticles);
            if (!newArticles.isEmpty()) {
                messagingTemplate.convertAndSend("/topic/greetings", newArticles);
                this.archivalTenArticles = lastTenArticles;
            }
        };
    }
}