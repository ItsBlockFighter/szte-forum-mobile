package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.model.adapter.ForumAdapter;
import hu.krisztofertarr.forum.model.adapter.ThreadAdapter;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import hu.krisztofertarr.forum.util.task.Callback;

public class ForumFragment extends Fragment implements NavigationBarView.OnItemSelectedListener {

    private final ForumApplication application;
    private Forum forum;

    private final List<Forum> subForums = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    private ForumAdapter forumAdapter;
    private ThreadAdapter threadAdapter;

    public ForumFragment() {
        this.application = ForumApplication.getInstance();
    }

    public ForumFragment(Forum forum) {
        this();
        this.forum = forum;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);

        this.title.setText(forum.getName());
        this.description.setText(forum.getDescription());

        this.forumAdapter = new ForumAdapter(getContext(), subForums, (value, v) -> {
            application.replaceFragment(new ForumFragment(value));
        });

        this.subForumsView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.subForumsView.setAdapter(forumAdapter);

        this.threadAdapter = new ThreadAdapter(getContext(), threads, (value, v) -> {
            application.replaceFragment(new ThreadFragment(value));
        });

        this.threadsView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.threadsView.setAdapter(threadAdapter);

        ForumService.getInstance()
                .findSubForumsByForum(forum.getId(), new Callback<List<Forum>>() {
                    @Override
                    public void onSuccess(List<Forum> data) {
                        subForums.clear();
                        subForums.addAll(data);
                        forumAdapter.notifyDataSetChanged();

                        view.findViewById(R.id.divider).setVisibility(forum.getSubForums().isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        view.findViewById(R.id.divider).setVisibility(forum.getSubForums().isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });

        ForumService.getInstance()
                .findThreadsByForum(forum.getId(), new Callback<List<Thread>>() {
                    @Override
                    public void onSuccess(List<Thread> data) {
                        threads.clear();
                        threads.addAll(data);
                        threadAdapter.notifyDataSetChanged();

                        view.findViewById(R.id.divider2).setVisibility(forum.getThreads().isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        view.findViewById(R.id.divider2).setVisibility(forum.getThreads().isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        application.refreshNavigationBar();
    }

    @FieldId("forum_title")
    private MaterialTextView title;

    @FieldId("forum_description")
    private MaterialTextView description;

    @FieldId("forum_subforums")
    private RecyclerView subForumsView;

    @FieldId("forum_threads")
    private RecyclerView threadsView;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.navigation_create_thread) {
            application.replaceFragment(
                    new ThreadCreateFragment(forum)
            );
            return true;
        }
        return false;
    }
}