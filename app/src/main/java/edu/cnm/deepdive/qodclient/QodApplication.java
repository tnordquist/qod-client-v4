package edu.cnm.deepdive.qodclient;

import android.app.Application;

public class QodApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    // This is where we would initialize Stetho, Picasso, etc.
    // This is also where we could do some non-trivial DB operation to force database creation.
  }

}
