package hu.krisztofertarr.forum.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hu.krisztofertarr.forum.R;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);
    }

    public static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public static class CategoryViewHolder extends RecyclerView.ViewHolder {

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            public void bind() {
                // Bind data to the view
            }
        }
    }
}