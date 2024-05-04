package hu.krisztofertarr.forum.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.krisztofertarr.forum.R;
import hu.krisztofertarr.forum.model.Thread;
import hu.krisztofertarr.forum.service.AuthService;
import hu.krisztofertarr.forum.util.Callback;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    private final Context context;
    private final List<Thread> threads;
    private ClickListener<Thread> clickListener;

    public ThreadAdapter(Context context, List<Thread> threads, ClickListener<Thread> clickListener) {
        this.context = context;
        this.threads = threads;
        this.clickListener = clickListener;
    }

    public ThreadAdapter(Context context, List<Thread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_thread, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Thread thread = threads.get(position);
        if (thread != null)
            holder.bindTo(thread, clickListener);
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView author;
        private final TextView stats;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.thread_title);
            this.author = itemView.findViewById(R.id.thread_author);
            this.stats = itemView.findViewById(R.id.thread_comments);
        }

        public void bindTo(Thread thread, ClickListener<Thread> clickListener) {
            this.title.setText(thread.getTitle());
            this.author.setText("...");
            this.stats.setText("Comments: " + thread.getPosts().size());

            AuthService.getInstance().getUsernameByUserId(
                    thread.getAuthorId(),
                    new Callback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            author.setText(data);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            author.setText("Error");
                        }
                    }
            );

            this.itemView.setOnClickListener(v -> clickListener.onClick(thread, v));
        }
    }
}