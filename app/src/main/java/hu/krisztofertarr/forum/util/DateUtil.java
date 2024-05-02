package hu.krisztofertarr.forum.util;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtil {

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String format(@NotNull Date date) {
        return FORMAT.format(date);
    }
}
