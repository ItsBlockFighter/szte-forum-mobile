package hu.krisztofertarr.forum.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Post;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.util.DateUtil;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final Thread thread;
    private ClickListener<Post> editListener;
    private ClickListener<Post> deleteListener;

    public PostAdapter(Context context, Thread thread, ClickListener<Post> editListener, ClickListener<Post> deleteListener) {
        this(context, thread);
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public PostAdapter(Context context, Thread thread) {
        this.context = context;
        this.thread = thread;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = thread.getPosts().get(position);
        if (post != null)
            holder.bindTo(post, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return thread.getPosts().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView createdAt;
        private final TextView content;

        private final ImageView editButton;
        private final ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.tv_user);
            this.createdAt = itemView.findViewById(R.id.tv_date);
            this.content = itemView.findViewById(R.id.tv_content);
            this.editButton = itemView.findViewById(R.id.tv_post_edit);
            this.deleteButton = itemView.findViewById(R.id.tv_post_delete);
        }

        public void bindTo(Post post, ClickListener<Post> editListener, ClickListener<Post> deleteListener) {
            username.setText(post.getAuthorId());
            createdAt.setText(DateUtil.format(post.getCreationDate()));
            content.setText(post.getContent());

            final String authorId = post.getAuthorId();
            if(authorId != null && authorId.isEmpty()) {
                if(AuthService.getInstance().isLoggedIn() && authorId.equals(AuthService.getInstance().getUser().getUid())) {
                    editButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }

            if(editListener != null)
                editButton.setOnClickListener(v -> editListener.onClick(post, v));

            if (deleteListener != null)
                deleteButton.setOnClickListener(v -> deleteListener.onClick(post, v));
        }
    }
}
