package com.introlabsystems.sheduledTasks;

import com.introlabsystems.domain.Article;
import com.introlabsystems.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private int count = 0;

    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
        LOG.info("{}. The time is now {}", count, dateFormat.format(new Date()));
        count++;
    }

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    private NewsService newsService;

    @Scheduled(fixedRate = 30000)
    public void sendOneArticle() {
        List<Article> articles = newsService.getOne();
        LOG.info("articles: {}", articles);
        messagingTemplate.convertAndSend("/topic/greetings", articles);
    }
}
