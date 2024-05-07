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
import hu.krisztofertarr.forum.model.User;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.util.ConditionUtil;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import hu.krisztofertarr.forum.util.task.Callback;

public class RegisterFragment extends Fragment {

    private final ForumApplication application;
    private AuthService authService;

    public RegisterFragment() {
        this.application = ForumApplication.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authService = AuthService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animation);
    }

    @FieldId("username")
    private EditText usernameField;

    @FieldId("password")
    private EditText passwordField;

    @FieldId("email")
    private EditText emailField;

    @ButtonId("register")
    public void register(View view) {
        ConditionUtil.assertIsNotEmpty(getContext(), usernameField.getText().toString(), "Kérlek adj meg egy felhasználónevet!");
        ConditionUtil.assertIsNotEmpty(getContext(), passwordField.getText().toString(), "Kérlek adj meg egy jelszót!");
        ConditionUtil.assertIsNotEmpty(getContext(), emailField.getText().toString(), "Kérlek adj meg egy e-mailt!");

        authService.register(
                emailField.getText().toString(), usernameField.getText().toString(), passwordField.getText().toString(),
                new Callback<User>() {
                    @Override
                    public void onSuccess(User data) {
                        Toast.makeText(getContext(), "Regisztráció sikeres!", Toast.LENGTH_SHORT).show();

                        application.replaceFragment(new LoginFragment());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Nem sikerült a regisztráció!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @ButtonId("back_to_login")
    public void backToLogin(View view) {
        application.replaceFragment(new LoginFragment());
    }
}