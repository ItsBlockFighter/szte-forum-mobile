package hu.krisztofertarr.forum.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Category;
import hu.krisztofertarr.forum.model.Forum;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Category> categories;
    private ClickListener<Forum> forumClickListener;

    private int lastPosition = -1;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public CategoryAdapter(Context context, List<Category> categories, ClickListener<Forum> forumClickListener) {
        this(context, categories);
        this.forumClickListener = forumClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category != null) {
            holder.bindTo(category);

            if(holder.getBindingAdapterPosition() > lastPosition) {
                holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                lastPosition = holder.getBindingAdapterPosition();
            }
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        private final List<Forum> forums;

        public ViewHolder(View itemView) {
            super(itemView);

            this.forums = new ArrayList<>();

            final ForumAdapter adapter = new ForumAdapter(context, forums, forumClickListener);

            this.title = itemView.findViewById(R.id.category_title);
            this.description = itemView.findViewById(R.id.category_description);

            final RecyclerView forumsView = itemView.findViewById(R.id.category_forums);
            forumsView.setLayoutManager(new LinearLayoutManager(context));
            forumsView.setAdapter(adapter);
        }

        public void bindTo(Category category) {
            title.setText(category.getName());
            description.setText(category.getDescription());
            forums.addAll(category.getForums());
        }
    }
}