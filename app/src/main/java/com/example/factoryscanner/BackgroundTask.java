package com.example.factoryscanner;

import android.app.Activity;

public abstract class BackgroundTask {

    private final Activity activity;
    public BackgroundTask(Activity activity) {
        this.activity = activity;
    }


    private void startBackground() {
        new Thread(new Runnable() {
            public void run() {

                String message = doInBackground();
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        onPostExecute(message);
                    }
                });
            }
        }).start();
    }
    public void execute(){
        startBackground();
    }

    public abstract String doInBackground(String... params);
    public abstract void onPostExecute(String string);

}
