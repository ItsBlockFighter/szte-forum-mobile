package hu.krisztofertarr.forum.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Forum;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {

    private final Context context;
    private final List<Forum> forums;
    private ClickListener<Forum> clickListener;

    private int lastPosition = -1;

    public ForumAdapter(Context context, List<Forum> forums, ClickListener<Forum> clickListener) {
        this.context = context;
        this.forums = forums;
        this.clickListener = clickListener;
    }

    public ForumAdapter(Context context, List<Forum> forums) {
        this.context = context;
        this.forums = forums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_forum, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forum forum = forums.get(position);
        if (forum != null) {
            holder.bindTo(forum, clickListener);

            if (holder.getBindingAdapterPosition() > lastPosition) {
                holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_left));
                lastPosition = holder.getBindingAdapterPosition();
            }
        }
    }

    @Override
    public int getItemCount() {
        return forums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.forum_title);
            this.description = itemView.findViewById(R.id.forum_description);
        }

        public void bindTo(Forum forum, ClickListener<Forum> clickListener) {
            this.title.setText(forum.getName());
            this.description.setText(forum.getDescription());

            if (clickListener != null)
                this.itemView.setOnClickListener(v -> clickListener.onClick(forum, v));
        }
    }
}