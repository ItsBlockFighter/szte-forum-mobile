package hu.krisztofertarr.forum;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.internal.DefaultFirebaseAppCheck;

import hu.krisztofertarr.forum.databinding.ActivityMainBinding;
import hu.krisztofertarr.forum.fragment.ForumFragment;
import hu.krisztofertarr.forum.fragment.HomeFragment;
import hu.krisztofertarr.forum.fragment.LoginFragment;
import hu.krisztofertarr.forum.fragment.ProfileFragment;
import hu.krisztofertarr.forum.service.AuthService;
import lombok.Getter;

@Getter
public class ForumApplication extends AppCompatActivity {

    @Getter
    private static ForumApplication instance;

    private AuthService authService;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        // Firebase SafetyNet
        FirebaseApp.initializeApp(this);

        authService = AuthService.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (authService.isLoggedIn()) {
            replaceFragment(new HomeFragment(this));
        } else {
            replaceFragment(new LoginFragment(this));
        }

        refreshNavigationBar();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (!authService.isLoggedIn()) {
                    return false;
                }
                replaceFragment(new HomeFragment(this));
                return true;
            } else if (item.getItemId() == R.id.navigation_account) {
                if (authService.isLoggedIn()) {
                    replaceFragment(new ProfileFragment(this));
                } else {
                    replaceFragment(new LoginFragment(this));
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_login) {
                replaceFragment(new LoginFragment(this));
                return true;
            }
            return false;
        });
    }

    public void refreshNavigationBar() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        if (authService.isLoggedIn()) {
            menu.findItem(R.id.navigation_home).setVisible(true);
            menu.findItem(R.id.navigation_account).setVisible(true);
            menu.findItem(R.id.navigation_login).setVisible(false);
        } else {
            menu.findItem(R.id.navigation_home).setVisible(false);
            menu.findItem(R.id.navigation_account).setVisible(false);
            menu.findItem(R.id.navigation_login).setVisible(true);
        }

        if (getCurrentFragment() instanceof ForumFragment
                && !authService.isAnonymous()
        ) {
            menu.findItem(R.id.navigation_create_thread).setVisible(true);
        } else {
            if (menu.findItem(R.id.navigation_create_thread).isVisible()) {
                menu.findItem(R.id.navigation_create_thread).setVisible(false);
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }
}