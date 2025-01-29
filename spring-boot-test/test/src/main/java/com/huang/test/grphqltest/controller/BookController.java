package com.huang.test.grphqltest.controller;

import com.huang.test.grphqltest.domain.Author;
import com.huang.test.grphqltest.domain.Book;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @QueryMapping("bookById")
    public Book bookById(@Argument String id) {
        return Book.getById(id);
    }

    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.getAuthorId());
    }
}
