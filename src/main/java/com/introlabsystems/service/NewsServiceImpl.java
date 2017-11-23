package com.introlabsystems.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.introlabsystems.domain.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);

    @Override
    public List<Article> getAll() {
        List<Article> articles = new ArrayList<>();
        String apiKey = "454c003f64c24921b278c1ba78d67dde";
        String url = "https://newsapi.org/v2/everything?sources=techcrunch&apiKey={apiKey}";
        url = url.replace("{apiKey}", apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            String statusNode = root.path("status").asText();
            if ("error".equals(statusNode)) {
                LOG.error("NewsApi response status is error.");
                return Collections.emptyList();
            }
            ArrayNode articlesNode = (ArrayNode) root.path("articles");
            for (JsonNode node : articlesNode) {
                articles.add(mapper.readValue(node.toString(), Article.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse error.");
        }

        return articles;
    }

    @Override
    public List<Article> getLastTenArticles() {
        List<Article> articles = new ArrayList<>();
        String apiKey = "454c003f64c24921b278c1ba78d67dde";
        String url = "https://newsapi.org/v2/everything?sources=techcrunch&apiKey={apiKey}";
        url = url.replace("{apiKey}", apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            String statusNode = root.path("status").asText();
            if ("error".equals(statusNode)) {
                LOG.error("NewsApi response status is error.");
                return Collections.emptyList();
            }
            ArrayNode articlesNode = (ArrayNode) root.path("articles");
            for (int i = 0; i < articlesNode.size() && i < 10; i++) {
                articles.add(mapper.readValue(articlesNode.get(i).toString(), Article.class));
            }

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse error.");
        }

        return articles;
    }
}