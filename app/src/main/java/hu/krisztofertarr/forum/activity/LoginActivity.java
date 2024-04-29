package hu.krisztofertarr.forum.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.service.AuthService;
import lombok.RequiredArgsConstructor;

public class LoginActivity extends AppCompatActivity {

    private AuthService authService;

    public LoginActivity() {
        this.authService = AuthService.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        boolean loggedIn = authService.isLoggedIn();
        if (loggedIn) {
            menu.findItem(R.id.log_out_button).setVisible(true);
            menu.findItem(R.id.settings_button).setVisible(true);
        } else {
            menu.findItem(R.id.login_button).setVisible(true);
            menu.findItem(R.id.register_button).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.login_button) {
            return true;
        } else if (id == R.id.register_button) {
            Log.d("ForumApplication", "Register clicked");
            return true;
        } else if (id == R.id.log_out_button) {
            Log.d("ForumApplication", "Log out clicked");
            return true;
        } else if (id == R.id.settings_button) {
            Log.d("ForumApplication", "Settings clicked");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}