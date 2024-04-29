package hu.krisztofertarr.forum.model;


import com.google.firebase.firestore.PropertyName;

import java.util.Date;

import lombok.Data;

// Comment
@Data
public class Post {

    private String content;

    private User author;

    @PropertyName("creation_date")
    private Date creationDate;

    @PropertyName("last_update")
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
