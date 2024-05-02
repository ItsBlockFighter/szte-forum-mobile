package hu.krisztofertarr.forum.model.adapter;

import android.view.View;

@FunctionalInterface
public interface ClickListener<T> {
    void onClick(T value, View view);
}
