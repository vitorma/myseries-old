package br.edu.ufcg.aweseries;

import android.app.Application;

/**
 * The way to get the context from outside an Activity as seen at
 * http://stackoverflow.com/questions/4391720/android-how-can-i-get-a-resources-object-from-a-static-context
 */
public class App extends Application {

    private static Environment environment;

    @Override
    public void onCreate() {
        super.onCreate();
        environment = Environment.newEnvironment(this);
    }

    /**
     * @return the application's environment
     * @see Environment
     */
    public static Environment environment() {
    	return environment;
    }

    /**
     * Sets the application's environment to newEnvironment. It's here for testing purposes.
     * @param newEnvironment the environment to be set
     */
    public static void setEnvironment(Environment newEnvironment) {
        environment = newEnvironment;
    }
}
