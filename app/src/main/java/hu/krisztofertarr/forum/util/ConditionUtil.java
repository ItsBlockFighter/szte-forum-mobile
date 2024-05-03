package hu.krisztofertarr.forum.util;

import android.content.Context;
import android.widget.Toast;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConditionUtil {


    public static void assertIsNotEmpty(Context context, String value, String message) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
    }
}
