package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.model.adapter.ForumAdapter;
import hu.krisztofertarr.forum.model.adapter.PostAdapter;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.ConditionUtil;
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
                        Toast.makeText(getContext(), "Üzenetek betöltése sikertelen!", Toast.LENGTH_SHORT).show();
                    }
                });

        adapter = new PostAdapter(getContext(), thread,
                (post, v) -> {

                },
                (post, v) -> {
                    // safe check
                    if (post.getAuthorId().equals(AuthService.getInstance().getUser().getUid())) {
                        ForumService.getInstance().deletePost(post, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                thread.getPosts().remove(post);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), "Üzenet törlés sikertelen!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(adapter);

        title.setText(thread.getTitle());
        author.setText(thread.getAuthorId());

        if (!AuthService.getInstance().isLoggedIn() || AuthService.getInstance().isAnonymous()) {
            input.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
        }

        if (thread.isLocked()) {
            input.setEnabled(false);
            send.setEnabled(false);
        }

        if (AuthService.getInstance().isLoggedIn()
                && AuthService.getInstance().getUser().getUid().equals(thread.getAuthorId())) {
            delete.setVisibility(View.VISIBLE);
        }

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

    @FieldId("thread_send")
    private Button send;

    @FieldId("thread_delete")
    private ImageView delete;

    @ButtonId("thread_delete")
    public void delete() {
        final String forumId = thread.getForumId();
        ForumService.getInstance().deleteThread(thread, new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                ForumService.getInstance().findForumById(forumId, new Callback<Forum>() {
                    @Override
                    public void onSuccess(Forum data) {
                        ForumApplication.getInstance().replaceFragment(
                                new ForumFragment(ForumApplication.getInstance(), data)
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ForumApplication.getInstance().replaceFragment(
                                new HomeFragment(ForumApplication.getInstance())
                        );
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Sikertelen téma törlés!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @ButtonId("thread_send")
    public void send(View view) {
        String content = input.getText().toString();
        ConditionUtil.assertIsNotEmpty(getContext(), content, "Nem küldhetsz üres üzenetet!");

        Post post = new Post(thread.getId(), content, AuthService.getInstance().getUser().getUid());
        ForumService.getInstance()
                .addPost(post, new Callback<Post>() {
                    @Override
                    public void onSuccess(Post data) {
                        thread.getPosts().add(data);
                        adapter.notifyDataSetChanged();

                        input.clearFocus();
                        input.setText("");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Sikertelen üzenet küldés!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}