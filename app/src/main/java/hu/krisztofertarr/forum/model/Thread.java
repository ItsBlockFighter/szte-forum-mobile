package hu.krisztofertarr.forum.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

// Theme
@Data
public class Thread {

    private String title;

    private User author;

    @Exclude
    private List<Post> posts;

    @PropertyName("creation_date")
    private Date creationDate;

    @PropertyName("last_update")
    private Date lastUpdate;

    public Thread() {
        this.posts = new ArrayList<>();
        this.creationDate = new Date();
        this.lastUpdate = new Date();
    }
}
