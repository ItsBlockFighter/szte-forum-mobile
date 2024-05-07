package hu.krisztofertarr.forum.service;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.task.CustomTask;
import hu.krisztofertarr.forum.util.task.NamedThreadFactory;

public class AvatarService {

    private static final String LOG_TAG = "AvatarService";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(
            NamedThreadFactory.builder()
                    .name("avatar-service-<id>")
                    .daemon(true)
                    .priority(Thread.MIN_PRIORITY)
                    .uncaughtExceptionHandler((t, e) -> Log.e(LOG_TAG, "Uncaught exception in thread " + t.getName(), e))
                    .build()
    );

    private static AvatarService instance;

    public static AvatarService getInstance() {
        if (instance == null) {
            instance = new AvatarService();
        }
        return instance;
    }

    private static final String AVATAR_PATH = "avatars/%s.jpg";

    private final FirebaseStorage storage;

    private AvatarService() {
        this.storage = FirebaseStorage.getInstance();
    }

    public void uploadAvatar(String userId, Uri uri, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() ->
                        storage.getReference(String.format(AVATAR_PATH, userId))
                                .putFile(uri)
                                .continueWith(task -> null)
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void getAvatar(String userId, Callback<Uri> callback) {
        CustomTask.<Uri>builder()
                .backgroundTask(() ->
                        storage.getReference(String.format(AVATAR_PATH, userId))
                                .getDownloadUrl())
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }
}
