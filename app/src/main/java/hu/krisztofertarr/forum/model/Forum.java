package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

// Folder
@Data
public class Forum {

    @DocumentId
    private String name;
    private String description;

    @Exclude
    private List<Thread> threads;

    public Forum() {
        this.threads = new ArrayList<>();
    }

    public Forum(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
