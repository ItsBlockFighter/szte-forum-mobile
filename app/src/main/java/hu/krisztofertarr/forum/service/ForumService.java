package hu.krisztofertarr.forum.service;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hu.krisztofertarr.forum.model.Category;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.task.CustomTask;
import hu.krisztofertarr.forum.util.task.NamedThreadFactory;

public class ForumService {
    private static final String LOG_TAG = "ForumService";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(
            NamedThreadFactory.builder()
                    .name("forum-service-<id>")
                    .daemon(true)
                    .priority(java.lang.Thread.NORM_PRIORITY)
                    .uncaughtExceptionHandler((thread, throwable) -> Log.e(LOG_TAG, "Uncaught exception in thread " + thread.getName(), throwable))
                    .build()
    );

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
    }

    public void findCategories(Callback<List<Category>> callback) {
        CustomTask.<List<Category>>builder()
                .backgroundTask(() ->
                        database.collection("categories")
                                .orderBy("creationDate")
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObjects(Category.class);
                                    }
                                    return new ArrayList<>();
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findForumsByCategory(String categoryId, Callback<List<Forum>> callback) {
        CustomTask.<List<Forum>>builder()
                .backgroundTask(() ->
                        database.collection("forums")
                                .whereEqualTo("parentId", categoryId)
                                .orderBy("creationDate")
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObjects(Forum.class);
                                    }
                                    return new ArrayList<>();
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findForumById(String forumId, Callback<Forum> callback) {
        CustomTask.<Forum>builder()
                .backgroundTask(() ->
                        database.collection("forums")
                                .document(forumId)
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObject(Forum.class);
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findSubForumsByForum(String forumId, Callback<List<Forum>> callback) {
        CustomTask.<List<Forum>>builder()
                .backgroundTask(() ->
                        database.collection("forums")
                                .whereEqualTo("parentId", forumId)
                                .orderBy("creationDate")
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObjects(Forum.class);
                                    }
                                    return new ArrayList<>();
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findThreadsByForum(String forumId, Callback<List<Thread>> callback) {
        CustomTask.<List<Thread>>builder()
                .backgroundTask(() ->
                        database.collection("threads")
                                .whereEqualTo("forumId", forumId)
                                .orderBy("creationDate")
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObjects(Thread.class);
                                    }
                                    return new ArrayList<>();
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findThreadById(String threadId, Callback<Thread> callback) {
        CustomTask.<Thread>builder()
                .backgroundTask(() ->
                        database.collection("threads")
                                .document(threadId)
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObject(Thread.class);
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void createThread(Thread thread, Post post, Callback<Thread> callback) {
        CustomTask.<Thread>builder()
                .backgroundTask(() ->
                        database.collection("threads")
                                .add(thread)
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        thread.setId(task.getResult().getId());
                                        post.setThreadId(thread.getId());

                                        return post;
                                    }
                                    return null;
                                })
                                .continueWithTask(task ->
                                        database.collection("posts")
                                                .add(task.getResult())
                                                .continueWith(postTask -> {
                                                    if (postTask.isSuccessful()) {
                                                        return thread;
                                                    }
                                                    return null;
                                                })
                                )
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void deleteThread(Thread thread, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() ->
                        database.collection("threads")
                                .document(thread.getId())
                                .delete()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return null;
                                    }
                                    return null;
                                })
                                .continueWithTask(task ->
                                        database.collection("posts")
                                                .whereEqualTo("threadId", thread.getId())
                                                .get()
                                                .continueWith(postsTask -> {
                                                    if (postsTask.isSuccessful()) {
                                                        for (DocumentSnapshot snapshots : postsTask.getResult().getDocuments()) {
                                                            snapshots.getReference().delete();
                                                        }
                                                        return null;
                                                    }
                                                    return null;
                                                })
                                )
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void findPostsByThread(String threadId, Callback<List<Post>> callback) {
        CustomTask.<List<Post>>builder()
                .backgroundTask(() ->
                        database.collection("posts")
                                .whereEqualTo("threadId", threadId)
                                .orderBy("creationDate")
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return task.getResult().toObjects(Post.class);
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void addPost(Post post, Callback<Post> callback) {
        CustomTask.<Post>builder()
                .backgroundTask(() ->
                        database.collection("posts")
                                .add(post)
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        post.setId(task.getResult().getId());
                                        return post;
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void savePost(Post post, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() ->
                        database.collection("posts")
                                .document(post.getId())
                                .set(post)
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return null;
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void deletePost(Post post, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() ->
                        database.collection("posts")
                                .document(post.getId())
                                .delete()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        return null;
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    private static final Random RANDOM = new Random();

    public void randomThread(Callback<Thread> callback) {
        CustomTask.<Thread>builder()
                .backgroundTask(() ->
                        database.collection("threads")
                                .whereEqualTo("locked", false)
                                .get()
                                .continueWith(task -> {
                                    if (task.isSuccessful()) {
                                        List<Thread> threads = task.getResult().toObjects(Thread.class);
                                        if (threads.isEmpty()) {
                                            return null;
                                        }
                                        return threads.get(RANDOM.nextInt(threads.size()));
                                    }
                                    return null;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }
}
