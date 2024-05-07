package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Category;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.adapter.CategoryAdapter;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.service.NotificationService;
import hu.krisztofertarr.forum.util.task.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.FieldId;

public class HomeFragment extends Fragment {

    private final ForumApplication application;

    public HomeFragment() {
        this.application = ForumApplication.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ComponentUtil.load(this, view);

        List<Category> categories = new ArrayList<>();

        ForumService.getInstance()
                .findCategories(new Callback<List<Category>>() {
                    @Override
                    public void onSuccess(List<Category> data) {
                        categories.addAll(data);

                        AtomicInteger counter = new AtomicInteger(0);
                        for(Category category : categories) {
                            ForumService.getInstance()
                                    .findForumsByCategory(category.getId(), new Callback<List<Forum>>() {
                                        @Override
                                        public void onSuccess(List<Forum> data) {
                                            category.setForums(data);

                                            if(counter.incrementAndGet() == categories.size()) {
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });

        this.adapter = new CategoryAdapter(getContext(), categories, (value, v) -> {
            application.replaceFragment(new ForumFragment(value));
        });
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        application.refreshNavigationBar();
    }

    @FieldId("home_categories")
    private RecyclerView recyclerView;
}