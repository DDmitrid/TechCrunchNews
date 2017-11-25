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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Controller
public class WebsocketController {
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketController.class);

    private Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap();
    private LocalDateTime archiveArticleDate;

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private NewsService newsService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        LocalDateTime lastArticleDate = newsService.getLastArticleDate();
        if (archiveArticleDate == null) {
            archiveArticleDate = lastArticleDate;
        } else if (lastArticleDate.isAfter(archiveArticleDate)) {
            List<Article> lastArticles = newsService.getArticlesOlderThen(archiveArticleDate);
            messagingTemplate.convertAndSend("/topic/greetings", lastArticles);
            archiveArticleDate = lastArticles.get(lastArticles.size() - 1).getPublishedAt();
        }
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(sendNewArticles(), 60_000);//start polling
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
            List<Article> lastArticles = newsService.getArticlesOlderThen(archiveArticleDate);
            if (!lastArticles.isEmpty()) {
                lastArticles.sort(Comparator.comparing(Article::getPublishedAt));
                messagingTemplate.convertAndSend("/topic/greetings", lastArticles);
                archiveArticleDate = lastArticles.get(lastArticles.size() - 1).getPublishedAt();
            }
        };
    }
}