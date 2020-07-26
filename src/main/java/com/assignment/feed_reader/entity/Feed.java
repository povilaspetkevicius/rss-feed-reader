package com.assignment.feed_reader.entity;

import com.assignment.feed_reader.util.FeedReaderConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = FeedReaderConstants.TABLE_FEEDS,
        indexes = {
                @Index(name = FeedReaderConstants.INDEX_FEED_NAME, columnList = FeedReaderConstants.COLUMN_FEED_NAME)
        })
public class Feed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = FeedReaderConstants.COLUMN_URL)
    private String url;

    @Column(name = FeedReaderConstants.COLUMN_FEED_TITLE)
    private String title;

    @Temporal(value = TemporalType.DATE)
    @Column(name = FeedReaderConstants.COLUMN_LAST_UPDATED)
    private Date lastUpdate;

    @Column(name = FeedReaderConstants.COLUMN_FEED_NAME)
    private String feedName;

    @OneToMany(mappedBy = "feed")
    private List<Item> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdate = new Date();
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
