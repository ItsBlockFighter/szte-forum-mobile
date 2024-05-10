package hu.krisztofertarr.forum.service;

import android.net.Uri;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    private static final String AVATAR_PATH = "images/%s/%s";
    private static final String AVATAR_FILE_NAME = "avatar.jpg";

    private final FirebaseStorage storage;

    private AvatarService() {
        this.storage = FirebaseStorage.getInstance();
    }

    public void uploadAvatar(String userId, Uri uri, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() ->
                        storage.getReference(String.format(AVATAR_PATH, userId, AVATAR_FILE_NAME))
                                .putFile(uri)
                                .continueWith(task -> null)
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }


    private final Cache<String, Uri> avatarCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public void getAvatar(String userId, Callback<Uri> callback) {
        Uri uri = avatarCache.getIfPresent(userId);
        if (uri != null) {
            callback.onSuccess(uri);
        } else {
            CustomTask.<Uri>builder()
                    .backgroundTask(() ->
                            storage.getReference(String.format(AVATAR_PATH, userId, AVATAR_FILE_NAME))
                                    .getDownloadUrl()
                                    .continueWith(task -> {
                                        if (task.isSuccessful()) {
                                            Uri result = task.getResult();
                                            if (result != null) {
                                                avatarCache.put(userId, result);
                                                return result;
                                            }
                                        }
                                        return null;
                                    })
                    )
                    .callback(callback)
                    .execute(EXECUTOR_SERVICE);
        }
    }
}
