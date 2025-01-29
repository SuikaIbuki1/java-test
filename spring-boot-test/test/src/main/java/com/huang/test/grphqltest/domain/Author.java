package com.huang.test.grphqltest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    private String id;
    private String firstName;
    private String lastName;
    private static List<Author> authors = Arrays.asList(
        new Author("1", "John", "Doe"),
        new Author("2", "Jane", "Doe")
    );
    public static Author getById(String id) {
        return authors.stream().filter(author -> author.getId().equals(id)).findFirst().orElse(null);
    }
}
