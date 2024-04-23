package hu.krisztofertarr.forum.model;


import java.util.Date;

import lombok.Data;

// Comment
@Data
public class Post {

    private String content;

    private User author;

    private Date creationDate;
    private Date lastUpdate;

    public Post() {
        this.creationDate = new Date();
        this.lastUpdate = new Date();
    }

    public Post(String content, User author) {
        this.content = content;
        this.author = author;
    }
}
