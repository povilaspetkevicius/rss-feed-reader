package com.assignment.feed_reader.repository;

import com.assignment.feed_reader.entity.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends CrudRepository<Feed, Integer> {

}
