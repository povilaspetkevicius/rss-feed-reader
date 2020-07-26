package com.assignment.feed_reader.controller;

import com.assignment.feed_reader.entity.Feed;
import com.assignment.feed_reader.entity.Item;
import com.assignment.feed_reader.repository.FeedRepository;
import com.assignment.feed_reader.repository.ItemRepository;
import com.assignment.feed_reader.util.FeedReaderConstants;
import com.assignment.feed_reader.util.ItemComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;


@Controller
@RequestMapping(path = "/api")
public class FeedController {

    FeedRepository feedRepository;

    ItemRepository itemRepository;

    @Autowired
    public FeedController(FeedRepository feedRepository, ItemRepository itemRepository) {
        this.feedRepository = feedRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping(path = "/feeds")
    public @ResponseBody Iterable<Feed> getAllFeeds() {
        return feedRepository.findAll();
    }

    @PostMapping(path = "/feeds")
    public @ResponseBody Feed addFeed(@RequestBody Feed feed) {
        return this.feedRepository.save(feed);
    }

    @GetMapping(path = "/feeds/{id}")
    public @ResponseBody Feed getFeed(@PathVariable Integer id) throws Exception {
        Optional<Feed> feed = feedRepository.findById(id);
        if(feed.isPresent()){
            return updateFeed(feed.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found");
        }
    }

    private Feed updateFeed(Feed feed) throws IOException, ParserConfigurationException, SAXException, ParseException {
        HttpURLConnection connection = (HttpURLConnection) new URL(feed.getUrl()).openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        InputStream input = connection.getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(input);
        Element element = document.getDocumentElement();
        NodeList nodes = element.getElementsByTagName(FeedReaderConstants.TAG_NAME_ITEM);
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Item> repositoryItems = new ArrayList<>();
        itemRepository.findAll().forEach(repositoryItems::add);
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            Node node = nodes.item(i);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                switch(childNodes.item(j).getNodeName()) {
                    case FeedReaderConstants.TAG_NAME_TITLE: {
                        item.setTitle(childNodes.item(j).getTextContent());
                        break;
                    }
                    case FeedReaderConstants.TAG_NAME_LINK: {
                        item.setLink(childNodes.item(j).getTextContent());
                        break;
                    }
                    case FeedReaderConstants.TAG_NAME_DESCRIPTION: {
                        item.setDescription(childNodes.item(j).getTextContent());
                        break;
                    }
                    case FeedReaderConstants.TAG_NAME_PUBLISHED: {
                        item.setPublished(convertStringToDate(childNodes.item(j).getTextContent()));
                    }
                }
            }
            item.setFeed(feed);

            if(!repositoryItems.contains(item)){
                items.add(item);
            }
        }
        ArrayList<Item> savedItems = new ArrayList<>();
        itemRepository.saveAll(items).forEach(savedItems::add);
        savedItems.addAll(repositoryItems);
        savedItems.sort(new ItemComparator());
        feed.setItems(savedItems);
        return feed;
    }

    public Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FeedReaderConstants.RFC822_DATE_FORMAT);
        return dateFormat.parse(date);
    }
}
