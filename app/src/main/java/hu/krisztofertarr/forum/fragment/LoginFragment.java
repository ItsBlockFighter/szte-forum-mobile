package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.util.ConditionUtil;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import hu.krisztofertarr.forum.util.task.Callback;
import lombok.NoArgsConstructor;

public class LoginFragment extends Fragment {

    private ForumApplication application;
    private AuthService authService;

    public LoginFragment() {
        this.application = ForumApplication.getInstance();
        this.authService = AuthService.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animation);
    }

    @FieldId("email")
    private EditText emailField;

    @FieldId("password")
    private EditText passwordField;

    @ButtonId("login")
    public void login(View view) {
        ConditionUtil.assertIsNotEmpty(getContext(), emailField.getText().toString(), "Kérlek adj meg egy e-mailt!");
        ConditionUtil.assertIsNotEmpty(getContext(), passwordField.getText().toString(), "Kérlek adj meg egy jelszót!");

        authService.login(
                emailField.getText().toString(), passwordField.getText().toString(),
                new Callback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        application.replaceFragment(new HomeFragment());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @ButtonId("register")
    public void register(View view) {
        application.replaceFragment(new RegisterFragment());
    }

    @ButtonId("guest_login")
    public void gustLogin(View view) {
        authService.loginGuest(new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                application.replaceFragment(new HomeFragment());
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Sikertelen bejelentkezés!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @ButtonId("login_google")
    public void loginGoogle(View view) {
    }
}