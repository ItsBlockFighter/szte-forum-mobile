package hu.krisztofertarr.forum.util.task;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private final boolean daemon;
    private final int priority;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    private transient final AtomicInteger id = new AtomicInteger();

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        if (name != null) {
            thread.setName(name.replace("<id>", this.id.getAndIncrement() + ""));
        }
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        if (uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
        return thread;
    }
}