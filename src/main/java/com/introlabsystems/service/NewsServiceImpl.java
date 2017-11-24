package com.introlabsystems.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.introlabsystems.constants.TechCrunchNewsConstants;
import com.introlabsystems.domain.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final String getLastArticlesUrl = "https://newsapi.org/v2/everything?sources=techcrunch&apiKey={apiKey}";
    private final String getArticlesOldesThenUrl = "https://newsapi.org/v2/everything?sources=techcrunch&from={dateFrom}&apiKey={apiKey}";

    @Autowired
    private TechCrunchNewsConstants techCrunchNewsConstants;

    @Override
    public List<Article> getLastPageArticles() {
        List<Article> articles = new ArrayList<>();
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
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
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
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

    @Override
    public List<Article> getArticlesOlderThen(Date lastArticleDate) {
        List<Article> articles = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastArticleDate);
        calendar.add(Calendar.MINUTE, 1);
        Date lastArticleDatePlusMinute = calendar.getTime();

        String url = getArticlesOldesThenUrl
                .replace("{apiKey}", techCrunchNewsConstants.getApiKey())
                .replace("{dateFrom}", dateFormat.format(lastArticleDatePlusMinute));
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
            for (int i = 0; i < articlesNode.size(); i++) {
                articles.add(mapper.readValue(articlesNode.get(i).toString(), Article.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse error.");
        }
        return articles;
    }

    @Override
    public Date getLastArticleDate() {
        Article lastArticle = null;
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            ArrayNode articlesNode = (ArrayNode) root.path("articles");
            lastArticle = mapper.readValue(articlesNode.get(0).toString(), Article.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse error.");
        }
        return lastArticle.getPublishedAt();
    }
}