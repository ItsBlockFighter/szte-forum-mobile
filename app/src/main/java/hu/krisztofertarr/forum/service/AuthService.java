package hu.krisztofertarr.forum.service;

import com.google.firebase.auth.FirebaseAuth;

public class AuthService {

    private static AuthService instance;

    public static AuthService getInstance() {
        if(instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private final FirebaseAuth firebaseAuth;

    private AuthService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public void register(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password);
    }


}
