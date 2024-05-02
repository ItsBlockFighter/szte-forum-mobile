package hu.krisztofertarr.forum.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConditionUtil {


    public static void assertIsNotEmpty(String value, String message) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
