package hu.krisztofertarr.forum.service;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import hu.krisztofertarr.forum.model.User;
import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.task.CustomTask;
import hu.krisztofertarr.forum.util.task.NamedThreadFactory;

public class AuthService {

    private static final String LOG_TAG = "AuthService";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool(
            NamedThreadFactory.builder()
                    .name("auth-service-<id>")
                    .daemon(true)
                    .priority(Thread.MIN_PRIORITY)
                    .uncaughtExceptionHandler((t, e) -> Log.e(LOG_TAG, "Uncaught exception in thread " + t.getName(), e))
                    .build()
    );
    private static AuthService instance;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private final FirebaseAuth auth;
    private final FirebaseFirestore database;

    private AuthService() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseFirestore.getInstance();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public boolean isAnonymous() {
        return isLoggedIn() && auth.getCurrentUser().isAnonymous();
    }

    public void logOut() {
        auth.signOut();
    }

    public void register(String email, String username, String password, Callback<User> callback) {
        CustomTask.<User>builder()
                .backgroundTask(() ->
                        auth.createUserWithEmailAndPassword(email, password)
                                .continueWithTask(task -> {
                                    final FirebaseUser fUser = task.getResult().getUser();
                                    if (fUser == null) {
                                        return null;
                                    }

                                    fUser.updateProfile(
                                            new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username)
                                                    .build()
                                    );

                                    final User user = new User(fUser.getUid(), fUser.getEmail(), username);
                                    return database.collection("users")
                                            .document(user.getId())
                                            .set(user)
                                            .continueWith(v -> user);
                                }))
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void login(String email, String password, Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() -> auth.signInWithEmailAndPassword(email, password).continueWithTask(task -> null))
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }


    public void loginGuest(Callback<Void> callback) {
        CustomTask.<Void>builder()
                .backgroundTask(() -> auth.signInAnonymously().continueWithTask(task -> null))
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    private final Cache<String, String> usernameCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public void getUsernameByUserId(String userId, Callback<String> callback) {
        String username = usernameCache.getIfPresent(userId);
        if(username != null) {
            callback.onSuccess(username);
            return;
        }

        CustomTask.<String>builder()
                .backgroundTask(() ->
                        database.collection("users")
                                .document(userId)
                                .get()
                                .continueWith(task -> {
                                    DocumentSnapshot result = task.getResult();
                                    if (result == null) {
                                        return "Unknown";
                                    }

                                    String name = result.getString("username");
                                    if (name == null) {
                                        name = "Unknown";
                                    }

                                    usernameCache.put(userId, name);
                                    return name;
                                })
                )
                .callback(callback)
                .execute(EXECUTOR_SERVICE);
    }

    public void loginWithGoogle() {
    }
}
