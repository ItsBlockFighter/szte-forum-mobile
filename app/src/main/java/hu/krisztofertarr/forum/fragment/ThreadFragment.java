package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.model.adapter.PostAdapter;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.ConditionUtil;
import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import hu.krisztofertarr.forum.util.task.Callback;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ThreadFragment extends Fragment {

    private Thread thread;
    private PostAdapter adapter;

    public ThreadFragment(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void onResume() {
        super.onResume();
        ForumApplication.getInstance().refreshNavigationBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_thread, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        Toast.makeText(getContext(), R.string.thread_messages_load_error, Toast.LENGTH_SHORT).show();
                    }
                });

        adapter = new PostAdapter(getContext(), thread,
                (post, v) -> {
                    if (post.getAuthorId().equals(AuthService.getInstance().getUser().getUid())) {
                        final String content = ((EditText) v.findViewById(R.id.tv_content)).getText().toString();
                        ConditionUtil.assertIsNotEmpty(getContext(), content, getString(R.string.thread_message_empty_error));

                        post.setContent(content);
                        ForumService.getInstance().savePost(post, new Callback<Void>() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), R.string.thread_message_save_failure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
                                Toast.makeText(getContext(), R.string.thread_message_delete_failure, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsView.setAdapter(adapter);

        title.setText(thread.getTitle());
        author.setText("...");

        AuthService.getInstance().getUsernameByUserId(
                thread.getAuthorId(),
                new Callback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        author.setText(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        author.setText(R.string.error);
                    }
                }
        );

        if (!AuthService.getInstance().isLoggedIn() || AuthService.getInstance().isAnonymous()) {
            input.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
        }

        if (thread.isLocked()) {
            input.setEnabled(false);
            send.setEnabled(false);
        } else {
            if (AuthService.getInstance().isLoggedIn()
                    && AuthService.getInstance().getUser().getUid().equals(thread.getAuthorId())) {
                delete.setVisibility(View.VISIBLE);
            }
        }
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
                                new ForumFragment(data)
                        );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ForumApplication.getInstance().replaceFragment(
                                new HomeFragment()
                        );
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), R.string.thread_delete_failure, Toast.LENGTH_LONG).show();
            }
        });
    }

    @ButtonId("thread_send")
    public void send() {
        String content = input.getText().toString();
        ConditionUtil.assertIsNotEmpty(getContext(), content, getString(R.string.thread_message_send_requirement_error));

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
                        Toast.makeText(getContext(), R.string.thread_message_send_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}