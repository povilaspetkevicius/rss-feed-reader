package com.assignment.feed_reader.entity;

import com.assignment.feed_reader.util.FeedReaderConstants;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = FeedReaderConstants.TABLE_ITEMS)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = FeedReaderConstants.COLUMN_ITEM_TITLE)
    private String title;

    @Column(name = FeedReaderConstants.COLUMN_LINK)
    private String link;

    @Column(name = FeedReaderConstants.COLUMN_DESCRIPTION)
    private String description;

    @Column(name = FeedReaderConstants.COLUMN_PUBLISHED)
    private Date published;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return this.title + " " + this.link + " " + this.description + " " + this.published;
    }

    @Override
    public boolean equals(Object obj) {
        Item comparableItem = (Item) obj;
        return this.title.equals(comparableItem.title)
                && this.link.equals(comparableItem.link)
                && this.description.equals(comparableItem.description);
    }
}
