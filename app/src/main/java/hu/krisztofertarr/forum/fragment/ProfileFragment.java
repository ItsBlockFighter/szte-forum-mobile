package hu.krisztofertarr.forum.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.AvatarService;
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

public class ProfileFragment extends Fragment {

    private final ForumApplication application;

    public ProfileFragment() {
        this.application = ForumApplication.getInstance();
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

        Uri avatar = AuthService.getInstance().getUser().getPhotoUrl();
        if (avatar != null) {
            updatePicture(avatar);
        }
    }

    @FieldId("profile_image")
    private ImageView profileImage;

    @ButtonId("profile_logout")
    public void logout(View view) {
        AuthService.getInstance().logOut();
        application.refreshNavigationBar();
        application.replaceFragment(new LoginFragment());
    }

    @ButtonId("profile_image_upload")
    public void uploadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
    }

    private void updatePicture(Uri uri) {
        Glide.with(this).load(uri).into(profileImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 200) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                profileImage.setImageURI(selectedImage);

                AvatarService.getInstance().uploadAvatar(
                        AuthService.getInstance().getUser().getUid(), selectedImage, new Callback<Uri>() {
                            @Override
                            public void onSuccess(Uri data) {
                                Toast.makeText(getContext(), "Avatar uploaded", Toast.LENGTH_SHORT).show();

                                updatePicture(data);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        }
    }
}