package com.assignment.feed_reader.repository;

import com.assignment.feed_reader.entity.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {
}
