package com.introlabsystems.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.introlabsystems.constants.TechCrunchNewsConstants;
import com.introlabsystems.domain.Article;
import com.introlabsystems.domain.TechCruncnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOG = LoggerFactory.getLogger(NewsServiceImpl.class);

    private final String getLastArticlesUrl = "https://newsapi.org/v2/everything?sources=techcrunch&apiKey={apiKey}";
    private final String getArticlesOldesThenUrl = "https://newsapi.org/v2/everything?sources=techcrunch&from={dateFrom}&apiKey={apiKey}";

    @Autowired
    private TechCrunchNewsConstants techCrunchNewsConstants;
    @Autowired
    ObjectMapper mapper;

    @Override
    public List<Article> getLastPageArticles() {
        TechCruncnResponse techCruncnResponse = null;
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            techCruncnResponse = mapper.readValue(jsonResponse, TechCruncnResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse exception.");
        }
        return techCruncnResponse.getArticles();
    }

    @Override
    public List<Article> getLastTenArticles() {
        TechCruncnResponse techCruncnResponse = null;
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            techCruncnResponse = mapper.readValue(jsonResponse, TechCruncnResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse exception.");
        }
        return techCruncnResponse.getArticles().subList(0, 10);
    }

    @Override
    public List<Article> getArticlesOlderThen(LocalDateTime lastArticleDate) {
        TechCruncnResponse techCruncnResponse = null;
        String lastArticleDatePlusMinute = lastArticleDate.plusMinutes(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String url = getArticlesOldesThenUrl
                .replace("{apiKey}", techCrunchNewsConstants.getApiKey())
                .replace("{dateFrom}", lastArticleDatePlusMinute);
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            techCruncnResponse = mapper.readValue(jsonResponse, TechCruncnResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse exception.");
        }
        return techCruncnResponse.getArticles();
    }

    @Override
    public LocalDateTime getLastArticleDate() {
        TechCruncnResponse techCruncnResponse = null;
        String url = getLastArticlesUrl.replace("{apiKey}", techCrunchNewsConstants.getApiKey());
        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(url, String.class);

        try {
            techCruncnResponse = mapper.readValue(jsonResponse, TechCruncnResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("NewsApi response parse exception.");
        }
        return techCruncnResponse.getArticles().get(0).getPublishedAt();
    }
}