package ru.yandex.money.android.sample;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dvmelnikov on 12/02/14.
 */
public class App extends Application {

    private static App app;

    public App() {
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupApp();
    }

    private void setupApp() {
    }


    public static App getApp() {
        return app;
    }
}
