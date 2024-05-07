package hu.krisztofertarr.forum.service;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.User;
import hu.krisztofertarr.forum.util.Callback;

public class AuthService {

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

    public Task<AuthResult> register(String email, String username, String password) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    final FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        return;
                    }

                    authResult.getUser()
                            .updateProfile(
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build()
                            )
                            .addOnSuccessListener(task -> {
                                database.collection("users")
                                        .document(user.getUid())
                                        .set(new User(user.getUid(), user.getEmail(), user.getDisplayName()));
                            });
                });
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }


    public Task<AuthResult> loginGuest() {
        return auth.signInAnonymously();
    }

    public void updateUsername(String string, Callback<Void> callback) {
        getUser().updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(string)
                        .build())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    private final Map<String, String> usernameCache = new ConcurrentHashMap<>();

    public void getUsernameByUserId(String userId, Callback<String> callback) {
        if (usernameCache.containsKey(userId)) {
            callback.onSuccess(usernameCache.get(userId));
            return;
        }

        database.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("username");
                    if (username == null) {
                        username = "Unknown";
                    }

                    usernameCache.put(userId, username);
                    callback.onSuccess(username);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void loginWithGoogle() {
    }
}
