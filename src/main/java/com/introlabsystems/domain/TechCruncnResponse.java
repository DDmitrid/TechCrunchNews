package com.introlabsystems.domain;

import java.util.List;

public class TechCruncnResponse {
    private String status;
    private List<Article> articles;

    public TechCruncnResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
