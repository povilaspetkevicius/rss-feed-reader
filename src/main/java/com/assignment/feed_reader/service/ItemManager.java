package com.assignment.feed_reader.service;

import com.assignment.feed_reader.entity.Feed;
import com.assignment.feed_reader.entity.Item;
import com.assignment.feed_reader.repository.ItemRepository;
import com.assignment.feed_reader.util.FeedReaderConstants;
import com.assignment.feed_reader.util.ItemComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
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
import java.util.Date;

@Service
public class ItemManager {

    private ItemRepository repository;

    @Autowired
    public ItemManager(ItemRepository repository) {
        this.repository = repository;
    }

    public ArrayList<Item> updateItems(Feed feed) throws IOException, ParserConfigurationException, SAXException, ParseException {
        Document document = fetchFeed(feed);
        Element element = document.getDocumentElement();
        NodeList nodes = element.getElementsByTagName(FeedReaderConstants.TAG_NAME_ITEM);
        ArrayList<Item> fetchedItems = new ArrayList<>();
        ArrayList<Item> repositoryItems = new ArrayList<>();
        repository.findAll().forEach(repositoryItems::add);
        parseFetchedItems(feed, nodes, fetchedItems, repositoryItems);
        ArrayList<Item> savedItems = new ArrayList<>();
        repository.saveAll(fetchedItems).forEach(savedItems::add);
        savedItems.addAll(repositoryItems);
        savedItems.sort(new ItemComparator());
        return savedItems;
    }

    private Document fetchFeed(Feed feed) throws IOException, ParserConfigurationException, SAXException {
        HttpURLConnection connection = (HttpURLConnection) new URL(feed.getUrl()).openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        InputStream input = connection.getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(input);
    }

    private void parseFetchedItems(Feed feed, NodeList nodes, ArrayList<Item> fetchedItems, ArrayList<Item> repositoryItems) throws ParseException {
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            Node node = nodes.item(i);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                switch (childNodes.item(j).getNodeName()) {
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

            //If such item is already saved, skip adding copy to DB (fetchedItems list will be saved as a whole)
            //HashList wouldn't solve a problem because DB allows for duplicates and items in rss feeds tend to not duplicate
            if (!repositoryItems.contains(item)) {
                fetchedItems.add(item);
            }
        }
    }

    public Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FeedReaderConstants.RFC822_DATE_FORMAT);
        return dateFormat.parse(date);
    }


}
