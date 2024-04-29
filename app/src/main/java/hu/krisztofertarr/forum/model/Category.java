package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

// Category
@Data
public class Category {

    @DocumentId
    private String name;
    private String description;

    @Exclude
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
