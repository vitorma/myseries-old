package br.edu.ufcg.aweseries;

import android.app.Application;
import android.content.Context;

/**
 * The way to get the context from outside an Activity as seen at
 * http://stackoverflow.com/questions/4391720/android-how-can-i-get-a-resources-object-from-a-static-context
 */
public class App extends Application {

    private static Context context;

    private static Environment environment;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        environment = new Environment();
    }

    public static Context getContext() {
        return context;
    }

    public static Environment environment() {
    	return environment;
    }
}
