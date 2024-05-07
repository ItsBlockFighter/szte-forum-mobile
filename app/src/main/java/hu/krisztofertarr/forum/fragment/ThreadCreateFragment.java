package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.ConditionUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ThreadCreateFragment extends Fragment {

    private Forum forum;

    public ThreadCreateFragment(Forum forum) {
        this.forum = forum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thread_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);
    }

    @FieldId("thread_create_title")
    private EditText title;

    @FieldId("thread_create_content")
    private EditText content;

    @ButtonId("thread_create_button")
    public void create() {
        ConditionUtil.assertIsNotEmpty(getContext(), title.getText().toString(), "Kérlek adj meg egy címet!");
        ConditionUtil.assertIsNotEmpty(getContext(), content.getText().toString(), "Kérlek adj meg egy szöveget!");

        final String authorId = AuthService.getInstance().getUser().getUid();

        Thread thread = new Thread(
                forum.getId(),
                title.getText().toString(),
                authorId,
                false
        );
        Post post = new Post(
                content.getText().toString(),
                authorId
        );
        ForumService.getInstance().createThread(thread, post, new Callback<Thread>() {
            @Override
            public void onSuccess(Thread data) {
                ForumApplication.getInstance().replaceFragment(
                        new ThreadFragment(data)
                );
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ThreadCreateFragment", "Failed to create thread", e);
                Toast.makeText(getContext(), "Nem sikerült létrehozni a témát!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @ButtonId("thread_cancel_button")
    public void cancel() {
        ForumApplication.getInstance().replaceFragment(
                new ForumFragment(forum)
        );
    }
}