package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.model.adapter.ForumAdapter;
import hu.krisztofertarr.forum.model.adapter.ThreadAdapter;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ForumFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private ForumApplication application;
    private Forum forum;


    private ForumAdapter forumAdapter;
    private ThreadAdapter threadAdapter;

    public ForumFragment(ForumApplication application, Forum forum) {
        this.application = application;
        this.forum = forum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        ComponentUtil.load(this, view);

        this.title.setText(forum.getName());
        this.description.setText(forum.getDescription());

        final List<Forum> _subForums = forum.getSubForums();

        this.forumAdapter = new ForumAdapter(getContext(), _subForums, (value, v) -> {
            application.replaceFragment(new ForumFragment(application, value));
        });

        this.subForums.setLayoutManager(new LinearLayoutManager(getContext()));
        this.subForums.setAdapter(forumAdapter);

        final List<Thread> _threads = forum.getThreads();

        this.threadAdapter = new ThreadAdapter(getContext(), _threads, (value, v) -> {
            application.replaceFragment(new ThreadFragment(value));
        });

        this.threads.setLayoutManager(new LinearLayoutManager(getContext()));
        this.threads.setAdapter(threadAdapter);

        ForumService.getInstance()
                .findSubForumsByForum(forum.getId(), new Callback<List<Forum>>() {
                    @Override
                    public void onSuccess(List<Forum> data) {
                        _subForums.addAll(data);
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
                        _threads.addAll(data);
                        threadAdapter.notifyDataSetChanged();

                        view.findViewById(R.id.divider2).setVisibility(forum.getThreads().isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        view.findViewById(R.id.divider2).setVisibility(forum.getThreads().isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });

        return view;
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
    private RecyclerView subForums;

    @FieldId("forum_threads")
    private RecyclerView threads;

    public void newThread(View view) {
        // TODO
    }
}