package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Forum {

    @DocumentId
    private String id;

    private String parentId;

    private String name;
    private String description;

    private Date creationDate;

    private transient List<Forum> subForums = new ArrayList<>();

    private transient List<Thread> threads = new ArrayList<>();

    public Forum() {
        this.creationDate = new Date();
    }

    public Forum(String parentId, String name, String description) {
        this();
        this.parentId = parentId;
        this.name = name;
        this.description = description;
    }

    @Exclude
    public void setSubForums(List<Forum> subForums) {
        this.subForums = subForums;
    }

    @Exclude
    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

    @Exclude
    public List<Forum> getSubForums() {
        return subForums;
    }

    @Exclude
    public List<Thread> getThreads() {
        return threads;
    }
}
