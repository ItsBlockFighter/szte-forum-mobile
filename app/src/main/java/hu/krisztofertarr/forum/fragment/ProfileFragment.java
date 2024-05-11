package hu.krisztofertarr.forum.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.AvatarService;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import hu.krisztofertarr.forum.util.task.Callback;

public class ProfileFragment extends Fragment {

    private final ForumApplication application;

    public ProfileFragment() {
        this.application = ForumApplication.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        application.refreshNavigationBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);

        final FirebaseUser user = AuthService.getInstance().getUser();

        AvatarService.getInstance().getAvatar(user.getUid(), new Callback<Uri>() {
            @Override
            public void onSuccess(Uri data) {
                if (data != null)
                    updatePicture(data);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), R.string.avatar_load_failure, Toast.LENGTH_SHORT).show();
            }
        });

        profileName.setText(user.getDisplayName());
        profileEmail.setText(user.getEmail());
    }

    @FieldId("profile_image")
    private ImageView profileImage;

    @FieldId("profile_name")
    private TextView profileName;

    @FieldId("profile_email")
    private TextView profileEmail;

    @ButtonId("profile_logout")
    public void logout(View view) {
        AuthService.getInstance().logOut();
        application.replaceFragment(new LoginFragment());
    }

    @ButtonId("profile_image_upload")
    public void uploadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), 200);
    }

    private void updatePicture(Uri uri) {
        Glide.with(this)
                .load(uri)
                .fitCenter()
                .circleCrop()
                .into(profileImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 200) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                AvatarService.getInstance().uploadAvatar(
                        AuthService.getInstance().getUser().getUid(), selectedImage, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                Toast.makeText(getContext(), R.string.avatar_upload_success, Toast.LENGTH_SHORT).show();

                                updatePicture(selectedImage);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), R.string.avatar_upload_failure, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }
    }
}