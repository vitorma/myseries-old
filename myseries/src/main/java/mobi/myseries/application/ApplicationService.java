package mobi.myseries.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class ApplicationService<L> implements Publisher<L> {
    private Environment environment;

    private ExecutorService executor;
    private Handler handler;

    private ListenerSet<L> listeners;

    public ApplicationService(Environment environment) {
        Validate.isNonNull(environment, "environment");

        this.environment = environment;

        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());

        this.listeners = new ListenerSet<L>();
    }

    protected Environment environment() {
        return this.environment;
    }

    protected void broadcast(String action) {
        this.environment.context().sendBroadcast(new Intent(action));
    }

    protected void run(Runnable runnable) {
        this.executor.execute(runnable);
    }

    protected void runInMainThread(Runnable runnable) {
        this.handler.post(runnable);
    }

    protected Future<?> submit(Runnable runnable) {
        return this.executor.submit(runnable);
    }

    protected ListenerSet<L> listeners() {
        return this.listeners;
    }

    @Override
    public boolean register(L listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(L listener) {
        return this.listeners.deregister(listener);
    }
}
