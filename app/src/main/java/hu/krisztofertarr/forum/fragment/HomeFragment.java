package hu.krisztofertarr.forum.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import hu.krisztofertarr.forum.ForumApplication;
import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Category;
import hu.krisztofertarr.forum.model.Forum;
import hu.krisztofertarr.forum.model.adapter.CategoryAdapter;
import hu.krisztofertarr.forum.service.ForumService;
import hu.krisztofertarr.forum.util.Callback;
import hu.krisztofertarr.forum.util.ComponentUtil;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HomeFragment extends Fragment {

    private ForumApplication application;

    public HomeFragment(ForumApplication application) {
        this.application = application;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
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

                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

        this.adapter = new CategoryAdapter(getContext(), categories, (value, v) -> {
            application.replaceFragment(new ForumFragment(application, value));
        });
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        application.refreshNavigationBar();
    }

    @FieldId("home_categories")
    private RecyclerView recyclerView;
}