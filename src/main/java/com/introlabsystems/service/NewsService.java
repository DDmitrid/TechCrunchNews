package com.introlabsystems.service;

import com.introlabsystems.domain.Article;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsService {
    List<Article> getLastPageArticles();

    List<Article> getLastTenArticles();

    List<Article> getArticlesOlderThen(LocalDateTime lastArticleDate);

    LocalDateTime getLastArticleDate();
}
