package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Thread {

    @DocumentId
    private String id;

    private String forumId;

    private String title;

    private String authorId;

    private boolean locked;

    private Date creationDate;
    private Date lastUpdate;

    private transient List<Post> posts = new ArrayList<>();

    public Thread() {
        this.creationDate = new Date();
        this.lastUpdate = new Date();
    }

    public Thread(String forumId, String title, String authorId) {
        this();
        this.forumId = forumId;
        this.title = title;
        this.authorId = authorId;
    }

    public Thread(String forumId, String title, String authorId, boolean locked) {
        this(forumId, title, authorId);
        this.locked = locked;
    }

    @Exclude
    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Exclude
    public List<Post> getPosts() {
        return posts;
    }
}
