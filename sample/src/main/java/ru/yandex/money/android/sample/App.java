package ru.yandex.money.android.sample;

import android.app.Application;

import com.yandex.money.YandexMoney;
import com.yandex.money.net.IRequest;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class App extends Application {

    private static App app;

    private YandexMoney ym;
    private ExecutorService executor;

    public App() {
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupApp();
    }

    private void setupApp() {
        executor = Executors.newSingleThreadExecutor();
        setupYm();
    }

    private void setupYm() {
        ym = new YandexMoney();
        ym.setDebugLogging(true); // set up logging (set false for production!)
    }

    public <T> Future<T> execute(final IRequest<T> request) {
        return executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return ym.performRequest(request);
            }
        });
    }

    public static App getApp() {
        return app;
    }
}
