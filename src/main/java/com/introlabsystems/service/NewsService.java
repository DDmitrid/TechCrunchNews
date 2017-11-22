package com.introlabsystems.service;

import com.introlabsystems.domain.Article;

import java.util.List;

public interface NewsService {
    List<Article> getAll();
    List<Article> getOne();
    List<Article> getTen();

    List<Article> getTwo();
}
