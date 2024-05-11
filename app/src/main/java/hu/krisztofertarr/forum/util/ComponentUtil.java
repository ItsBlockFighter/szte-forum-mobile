package hu.krisztofertarr.forum.util;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

import hu.krisztofertarr.forum.util.annotation.ButtonId;
import hu.krisztofertarr.forum.util.annotation.FieldId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComponentUtil {

    private static final String LOG_TAG = "ComponentUtil";

    private static final BiFunction<Object, Method, View.OnClickListener> buttonClickListener = (instance, method) -> v -> {
        try {
            if (method.getParameterCount() == 1) {
                method.invoke(instance, v);
                return;
            }
            method.invoke(instance);
        } catch (IllegalArgumentException ex) {
            Log.d(LOG_TAG, "An exception occured", ex);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to invoke the click listener.", e);
        }
    };

    public void load(Object instance, View view) {
        loadButtons(instance, view);
        loadFields(instance, view);
    }

    public void loadFields(Object instance, View view) {
        final Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            final FieldId fieldId = field.getAnnotation(FieldId.class);
            if (fieldId == null) {
                continue;
            }
            final String name = fieldId.value();

            int id = findId(view, name);

            try {
                field.setAccessible(true);
                field.set(instance, view.findViewById(id));
            } catch (IllegalAccessException e) {
                Log.d(LOG_TAG, "Failed to set the field value.", e);
            }
        }
    }

    public void loadButtons(Object instance, View view) {
        final Class<?> clazz = instance.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            final ButtonId buttonId = method.getAnnotation(ButtonId.class);
            if (buttonId == null) {
                continue;
            }
            final String name = buttonId.value();

            int button = findId(view, name);

            view.findViewById(button)
                    .setOnClickListener(buttonClickListener.apply(instance, method));
        }
    }

    @SuppressLint("DiscouragedApi")
    public int findId(View view, String name) {
        return view.getResources().getIdentifier(name, "id", view.getContext().getPackageName());
    }
}
