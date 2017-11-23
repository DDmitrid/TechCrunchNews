package com.introlabsystems.controllers;

import com.introlabsystems.domain.Article;
import com.introlabsystems.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class TechCrunchNewsController {
    private static final Logger LOG = LoggerFactory.getLogger(TechCrunchNewsController.class);

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "/lastNews", method = RequestMethod.GET)
    public String getLastTenNews(Model model) {
        LOG.debug("Invoke getLastTenNews.");
        List<Article> articles = newsService.getLastTenArticles();
        model.addAttribute("articles", articles);
        return "mainPage";
    }
}
