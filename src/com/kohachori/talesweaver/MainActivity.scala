package com.kohachori.talesweaver

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.Window
import android.view.WindowManager

class MainActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_main);
    getFragmentManager().beginTransaction()
      .replace(R.id.container_main, new MainFragment()).commit();
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    return true
  }
}
