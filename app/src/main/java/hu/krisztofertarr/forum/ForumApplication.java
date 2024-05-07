package hu.krisztofertarr.forum;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;

import hu.krisztofertarr.forum.databinding.ActivityMainBinding;
import hu.krisztofertarr.forum.fragment.ForumFragment;
import hu.krisztofertarr.forum.fragment.HomeFragment;
import hu.krisztofertarr.forum.fragment.LoginFragment;
import hu.krisztofertarr.forum.fragment.ProfileFragment;
import hu.krisztofertarr.forum.fragment.ThreadFragment;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.BroadcastService;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.PermissionUtil;
import lombok.Getter;

import android.Manifest;

@Getter
public class ForumApplication extends AppCompatActivity {

    private static final String LOG_TAG = ForumApplication.class.getName();

    @Getter
    private static ForumApplication instance;

    private AuthService authService;
    private BroadcastService broadcastService;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        requestPermissions();

        // Firebase SafetyNet
        FirebaseApp.initializeApp(this);

        authService = AuthService.getInstance();

        broadcastService = new BroadcastService(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final String threadId = getIntent().getStringExtra("threadId");
        if (threadId != null) {
            if (authService.isLoggedIn()) {
                ForumService.getInstance().findThreadById(threadId, new Callback<Thread>() {
                    @Override
                    public void onSuccess(Thread data) {
                        if (data != null) {
                            replaceFragment(new ThreadFragment(data));
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        }

        if (authService.isLoggedIn()) {
            replaceFragment(new HomeFragment());
        } else {
            replaceFragment(new LoginFragment());
        }

        refreshNavigationBar();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                if (!authService.isLoggedIn()) {
                    return false;
                }
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.navigation_account) {
                if (authService.isLoggedIn()) {
                    replaceFragment(new ProfileFragment());
                } else {
                    replaceFragment(new LoginFragment());
                }
                return true;
            } else if (item.getItemId() == R.id.navigation_login) {
                replaceFragment(new LoginFragment());
                return true;
            }
            if (getCurrentFragment() instanceof NavigationBarView.OnItemSelectedListener) {
                return ((NavigationBarView.OnItemSelectedListener) getCurrentFragment()).onNavigationItemSelected(item);
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

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return;
        }
        PermissionUtil.checkAndRequestPermissions(this,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.POST_NOTIFICATIONS
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }
}