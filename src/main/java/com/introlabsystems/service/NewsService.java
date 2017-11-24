package com.introlabsystems.service;

import com.introlabsystems.domain.Article;

import java.util.Date;
import java.util.List;

public interface NewsService {
    List<Article> getLastPageArticles();
    List<Article> getLastTenArticles();
    List<Article> getArticlesOlderThen(Date lastArticleDate);
    Date getLastArticleDate();
}
