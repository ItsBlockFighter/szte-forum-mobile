package hu.krisztofertarr.forum.service;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

    private AuthService() {
        this.auth = FirebaseAuth.getInstance();
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

    public Task<AuthResult> register(String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password);
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

    public void updateAvatar(Uri uri, Callback<Void> callback) {
        getUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public String getUsernameByUserId(String userId) {
        return null;
    }
}
