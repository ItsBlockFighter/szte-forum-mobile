package hu.krisztofertarr.forum.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

// Folder
@Data
public class Forum {

    private String name;
    private String description;

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
