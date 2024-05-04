package hu.krisztofertarr.forum.service;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;

import hu.krisztofertarr.forum.util.Callback;

public class AvatarService {

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

    public void uploadAvatar(String userId, Uri uri, Callback<Uri> callback) {
        storage.getReference(String.format(AVATAR_PATH, userId))
                .putFile(uri)
                .addOnSuccessListener(taskSnapshot -> callback.onSuccess(taskSnapshot.getUploadSessionUri()))
                .addOnFailureListener(callback::onFailure);
    }

    public void getAvatar(String userId, Callback<Uri> callback) {
        storage.getReference(String.format(AVATAR_PATH, userId))
                .getDownloadUrl()
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(callback::onFailure);
    }
}
