package hu.krisztofertarr.forum.service;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import hu.krisztofertarr.forum.model.Category;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.util.Callback;

public class ForumService {

    private static ForumService instance;

    public static ForumService getInstance() {
        if (instance == null) {
            instance = new ForumService();
        }
        return instance;
    }

    private final FirebaseFirestore database;

    private ForumService() {
        this.database = FirebaseFirestore.getInstance();

        //this.initialize();
    }

    private void initialize() {
        // Initialize the database with some default data
        database.collection("categories")
                .add(new Category("General", "General discussion"))
                .addOnSuccessListener(documentReference -> {
                    database.collection("forums")
                            .add(new Forum(documentReference.getId(), "General", "General discussion"))
                            .addOnSuccessListener(documentReference1 -> {
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Welcome to the forum", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Welcome to the forum!", "admin"));
                                        });
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Forum rules", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Forum rules!", "admin"))
                                                    .addOnSuccessListener(documentReference3 -> {
                                                        database.collection("posts")
                                                                .add(new Post(documentReference2.getId(), "No spamming!", "admin"));
                                                        database.collection("posts")
                                                                .add(new Post(documentReference2.getId(), "No trolling!", "admin"));
                                                    });
                                        });
                            });
                });
        database.collection("categories")
                .add(new Category("Programming", "Programming discussion"))
                .addOnSuccessListener(documentReference -> {
                    database.collection("forums")
                            .add(new Forum(documentReference.getId(), "Programming", "Programming discussion"))
                            .addOnSuccessListener(documentReference1 -> {
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Welcome to the programming forum", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Welcome to the programming forum!", "admin"));
                                        });
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Programming is fun", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Programming is fun!", "admin"));
                                        });
                            });
                });
        database.collection("categories")
                .add(new Category("Android", "Android development discussion"))
                .addOnSuccessListener(documentReference -> {
                    database.collection("forums")
                            .add(new Forum(documentReference.getId(), "Android", "Android development discussion"))
                            .addOnSuccessListener(documentReference1 -> {
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Welcome to the Android forum", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Welcome to the Android forum!", "admin"));
                                        });
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Android development", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Android development is fun!", "admin"));
                                        });
                            });
                });
        database.collection("categories")
                .add(new Category("iOS", "iOS development discussion"))
                .addOnSuccessListener(documentReference -> {
                    database.collection("forums")
                            .add(new Forum(documentReference.getId(), "iOS", "iOS development discussion"))
                            .addOnSuccessListener(documentReference1 -> {
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "Welcome to the iOS forum", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "Welcome to the iOS forum!", "admin"));
                                        });
                                database.collection("threads")
                                        .add(new Thread(documentReference1.getId(), "iOS development", "admin", true))
                                        .addOnSuccessListener(documentReference2 -> {
                                            database.collection("posts")
                                                    .add(new Post(documentReference2.getId(), "iOS development is fun!", "admin"));
                                        });
                            });
                });
    }

    public void findCategories(Callback<List<Category>> callback) {
        database.collection("categories")
                .orderBy("creationDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Category> categories = task.getResult().toObjects(Category.class);
                        callback.onSuccess(categories);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findForumsByCategory(String categoryId, Callback<List<Forum>> callback) {
        database.collection("forums")
                .whereEqualTo("parentId", categoryId)
                .orderBy("creationDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Forum> forums = task.getResult().toObjects(Forum.class);
                        callback.onSuccess(forums);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findForumById(String forumId, Callback<Forum> callback) {
        database.collection("forums")
                .document(forumId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Forum forum = task.getResult().toObject(Forum.class);
                        callback.onSuccess(forum);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findSubForumsByForum(String forumId, Callback<List<Forum>> callback) {
        database.collection("forums")
                .whereEqualTo("parentId", forumId)
                .orderBy("creationDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Forum> forums = task.getResult().toObjects(Forum.class);
                        callback.onSuccess(forums);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findThreadsByForum(String forumId, Callback<List<Thread>> callback) {
        database.collection("threads")
                .whereEqualTo("forumId", forumId)
                .orderBy("creationDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Thread> threads = task.getResult().toObjects(Thread.class);
                        callback.onSuccess(threads);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findThreadById(String threadId, Callback<Thread> callback) {
        database.collection("threads")
                .document(threadId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Thread thread = task.getResult().toObject(Thread.class);
                        callback.onSuccess(thread);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void createThread(Thread thread, Post post, Callback<Thread> callback) {
        database.collection("threads")
                .add(thread)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        thread.setId(task.getResult().getId());
                        post.setThreadId(thread.getId());

                        addPost(post, new Callback<Post>() {
                            @Override
                            public void onSuccess(Post data) {
                                callback.onSuccess(thread);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                callback.onFailure(e);
                            }
                        });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void deleteThread(Thread thread, Callback<Void> callback) {
        database.collection("threads")
                .document(thread.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);

                        database.collection("posts")
                                .whereEqualTo("threadId", thread.getId())
                                .get()
                                .addOnSuccessListener(result -> {
                                    for (DocumentSnapshot snapshots : result.getDocuments()) {
                                        snapshots.getReference().delete();
                                    }
                                });
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void findPostsByThread(String threadId, Callback<List<Post>> callback) {
        database.collection("posts")
                .whereEqualTo("threadId", threadId)
                .orderBy("creationDate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> posts = task.getResult().toObjects(Post.class);
                        callback.onSuccess(posts);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void addPost(Post post, Callback<Post> callback) {
        database.collection("posts")
                .add(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        post.setId(task.getResult().getId());

                        callback.onSuccess(post);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void savePost(Post post, Callback<Void> callback) {
        database.collection("posts")
                .document(post.getId())
                .set(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });

    }

    public void deletePost(Post post, Callback<Void> callback) {
        database.collection("posts")
                .document(post.getId())
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
}
