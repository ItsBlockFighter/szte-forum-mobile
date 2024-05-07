package hu.krisztofertarr.forum.util.task;

public interface Callback<T> {
    default void onSuccess(T data) {
    }

    default void onFailure(Exception e) {
    }
}
