package com.example.injectextra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = new Intent(this, SecondActivity.class);
    intent.putExtra(SecondActivity.APP_NAME, "injected !");
    startActivity(intent);
  }
}
