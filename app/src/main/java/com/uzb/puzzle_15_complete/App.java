package com.uzb.puzzle_15_complete;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalStorage.init(this);
        LocalStorage.getInstance().getArray();
    }
}
