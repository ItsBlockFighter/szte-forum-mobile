package hu.krisztofertarr.forum.util.task;

import android.os.AsyncTask;
import android.util.Pair;

import com.google.android.gms.tasks.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomTask<R> extends AsyncTask<Void, Void, Pair<R, Exception>> {

    private final Supplier<Task<R>> backgroundTask;
    private final Callback<R> callback;

    @Override
    protected Pair<R, Exception> doInBackground(Void... voids) {
        Task<R> task = backgroundTask.get();

        CountDownLatch latch = new CountDownLatch(1);

        task.addOnCompleteListener(result -> latch.countDown());

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if(task.isSuccessful()) {
            return Pair.create(task.getResult(), null);
        }
        return Pair.create(null, task.getException());
    }

    @Override
    protected void onPostExecute(Pair<R, Exception> result) {
        super.onPostExecute(result);

        if(result.second != null) {
            callback.onFailure(result.second);
            return;
        }

        callback.onSuccess(result.first);
    }

    @Override
    protected void onCancelled(Pair<R, Exception> r) {
        super.onCancelled(r);

        if(r != null && r.second != null) {
            callback.onFailure(r.second);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private Supplier<Task<T>> backgroundTask;
        private Callback<T> callback;

        public Builder<T> backgroundTask(Supplier<Task<T>> backgroundTask) {
            this.backgroundTask = backgroundTask;
            return this;
        }

        public Builder<T> callback(Callback<T> callback) {
            this.callback = callback;
            return this;
        }

        public CustomTask<T> build() {
            return new CustomTask<>(backgroundTask, callback);
        }

        public void execute(ExecutorService executorService) {
            build().executeOnExecutor(executorService);
        }
    }
}
