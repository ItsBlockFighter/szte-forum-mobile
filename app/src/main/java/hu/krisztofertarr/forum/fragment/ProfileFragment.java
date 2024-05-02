package hu.krisztofertarr.forum.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProfileFragment extends Fragment {

    private ForumApplication application;

    public ProfileFragment(ForumApplication application) {
        this.application = application;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ComponentUtil.load(this, view);

        Uri avatar = AuthService.getInstance().user().getPhotoUrl();
        if(avatar != null) {
            Glide.with(this).load(avatar).into(profileImage);
        }

        return view;
    }

    @FieldId("profile_image")
    private ImageView profileImage;

    @ButtonId("profile_logout")
    public void logout(View view) {
        AuthService.getInstance().logOut();
        application.refreshNavigationBar();
        application.replaceFragment(new LoginFragment(application));
    }

    @ButtonId("profile_image_upload")
    public void uploadImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 200) {
            Uri selectedImage = data.getData();
            if(selectedImage != null) {
                profileImage.setImageURI(selectedImage);

                AuthService.getInstance().updateAvatar(selectedImage, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(getContext(), "Avatar uploaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}