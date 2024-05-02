package hu.krisztofertarr.forum.model;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;

import lombok.Data;

@Data
public class Post {

    @DocumentId
    private String id;

    private String threadId;

    private String content;

    private String authorId;

    private Date creationDate;
    private Date lastUpdate;

    public Post() {
        this.creationDate = new Date();
        this.lastUpdate = new Date();
    }

    public Post(String threadId, String content, String authorId) {
        this();
        this.threadId = threadId;
        this.content = content;
        this.authorId = authorId;
    }
}
