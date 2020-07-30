package com.assignment.feed_reader.controller;

import com.assignment.feed_reader.entity.Feed;
import com.assignment.feed_reader.entity.Item;
import com.assignment.feed_reader.repository.FeedRepository;
import com.assignment.feed_reader.service.ItemManager;
import com.assignment.feed_reader.util.FeedReaderConstants;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping(path = "/api")
public class FeedController {

    private FeedRepository feedRepository;

    private ItemManager itemManager;

    @Autowired
    public FeedController(FeedRepository feedRepository, ItemManager itemManager) {
        this.feedRepository = feedRepository;
        this.itemManager = itemManager;
    }

    @GetMapping(path = "/feeds")
    public @ResponseBody
    Iterable<Feed> getAllFeeds() {
        return feedRepository.findAll();
    }

    @PostMapping(path = "/feeds")
    public @ResponseBody
    Feed addFeed(@RequestBody Feed feed) {
        if (isURLValid(feed.getUrl())) {
            return feedRepository.save(feed);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FeedReaderConstants.ERROR_MSG_URL_NOT_VALID);
        }
    }

    @GetMapping(path = "/feeds/{id}")
    public @ResponseBody
    Feed getFeed(@PathVariable Integer id) throws Exception {
        Optional<Feed> feed = feedRepository.findById(id);
        if (feed.isPresent()) {
            Feed savedFeed = feed.get();
            try {
                ArrayList<Item> savedItems = itemManager.updateItems(savedFeed);
                savedFeed.setItems(savedItems);
                return savedFeed;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, FeedReaderConstants.ERROR_MSG_FEED_UPDATE_FAILED);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, FeedReaderConstants.ERROR_MSG_FEED_NOT_FOUND);
        }
    }

    private boolean isURLValid(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(url);
    }

}
