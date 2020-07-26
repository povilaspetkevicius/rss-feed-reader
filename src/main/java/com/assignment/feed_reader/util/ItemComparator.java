package com.assignment.feed_reader.util;

import com.assignment.feed_reader.entity.Item;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        if (o1.getPublished().equals(o2.getPublished())){
            return 0;
        } else {
            return o1.getPublished().before(o2.getPublished()) ? 1 : -1;
        }
    }
}
