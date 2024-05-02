package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Category {

    @DocumentId
    private String id;

    private String name;
    private String description;

    private Date creationDate;

    private transient List<Forum> forums = new ArrayList<>();

    public Category() {
        this.creationDate = new Date();
    }

    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    @Exclude
    public void setForums(List<Forum> forums) {
        this.forums = forums;
    }

    @Exclude
    public List<Forum> getForums() {
        return forums;
    }
}
