package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.model.adapter.PostAdapter;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ThreadFragment extends Fragment {

    private Thread thread;

    public ThreadFragment(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thread, container, false);
        ComponentUtil.load(this, view);

        ForumService.getInstance()
                .findPostsByThread(thread.getId(), new Callback<List<Post>>() {
                    @Override
                    public void onSuccess(List<Post> data) {
                        thread.setPosts(data);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

        adapter = new PostAdapter(getContext(), thread);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(adapter);

        title.setText(thread.getTitle());
        author.setText(thread.getAuthorId());

        return view;
    }

    @FieldId("thread_posts")
    private RecyclerView postsView;

    @FieldId("thread_title")
    private TextView title;

    @FieldId("thread_author")
    private TextView author;

    @FieldId("thread_input")
    private EditText input;

    @ButtonId("thread_send")
    public void send(View view) {
        String content = input.getText().toString();

        Post post = new Post(thread.getId(), content, AuthService.getInstance().user().getDisplayName());
        ForumService.getInstance()
                        .addPost(thread, post, new Callback<Post>() {
                            @Override
                            public void onSuccess(Post data) {
                                thread.getPosts().add(data);
                                adapter.notifyDataSetChanged();

                                input.clearFocus();
                                input.setText("");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), "Failed to send post", Toast.LENGTH_SHORT).show();
                            }
                        });
    }
}