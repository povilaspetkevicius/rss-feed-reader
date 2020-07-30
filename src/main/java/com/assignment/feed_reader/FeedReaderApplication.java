package com.assignment.feed_reader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class FeedReaderApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FeedReaderApplication.class, args);
	}

}
