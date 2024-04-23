package hu.krisztofertarr.forum.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

// Category
@Data
public class Category {

    private String name;
    private String description;

    private List<Forum> forums;

    public Category() {
        this.forums = new ArrayList<>();
    }

    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
