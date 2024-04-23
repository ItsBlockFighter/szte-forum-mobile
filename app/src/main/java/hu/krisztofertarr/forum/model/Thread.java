package hu.krisztofertarr.forum.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

// Theme
@Data
public class Thread {

    private String title;

    private User author;

    private List<Post> posts;

    private Date creationDate;
    private Date lastUpdate;

    public Thread() {
        this.posts = new ArrayList<>();
        this.creationDate = new Date();
        this.lastUpdate = new Date();
    }
}
