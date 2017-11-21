package com.introlabsystems.controllers;

import com.introlabsystems.domain.Article;
import com.introlabsystems.dto.NewsDTO;
import com.introlabsystems.factory.NewsFactory;
import com.introlabsystems.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/techCrunchNews")
public class TechCrunchNewsController {
    private static final Logger LOG = LoggerFactory.getLogger(TechCrunchNewsController.class);

    @Autowired
    private NewsService newsService;

    @RequestMapping(value = "/lastNews", method = RequestMethod.GET)
    public ResponseEntity<List<NewsDTO>> getLastTenNews() {
        LOG.debug("Invoke getLastTenNews.");
        List<Article> articles = newsService.getAll();
        List<NewsDTO> resource = NewsFactory.createDTOList(articles);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }
}
