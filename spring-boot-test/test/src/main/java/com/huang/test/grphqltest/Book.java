package com.huang.test.grphqltest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    private String id;
    private String name;
    private int pageCount;
    private String authorId;

    private static List<Book> books = Arrays.asList(
            new Book("1", "Java编程思想", 10, "1"),
            new Book("2", "Effective Java", 12, "2")
    );

    public static Book getById(String id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst().orElse(null);
    }

}
