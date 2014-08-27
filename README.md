InjectExtra [![Travis Build](https://travis-ci.org/stephanenicolas/injectextra.svg?branch=master)](https://travis-ci.org/stephanenicolas/injectextra) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.injectextra/injectextra-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.stephanenicolas.injectextra/injectextra-plugin)
==========

***Injects everything passed by extra to an activity automatically.***

<!-- img src="https://raw.github.com/stephanenicolas/injectresource/master/assets/injectresource-logo.jpg"
width="150px" /-->

###Usage

Inside your `build.gradle` file, add : 

```groovy
apply plugin: 'injectextra'
```

And now, annotate every resource you want to inject.

```java

public class SecondActivity extends Activity {

  public static final String APP_NAME = "APP_NAME";

  @InjectExtra(APP_NAME)
  private String appName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    TextView view = (TextView) findViewById(R.id.textview_hello);
    view.setText("Injected :" + appName);
  }
}
```

```java

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = new Intent(this, SecondActivity.class);
    intent.putExtra(SecondActivity.APP_NAME, "injected !");
    startActivity(intent);
  }
}
```

###Example

You will find an example program in the repo.

###How does it work ?

Thanks to 
* [morpheus](https://github.com/stephanenicolas/morpheus), byte code weaver for android.
* [AfterBurner](https://github.com/stephanenicolas/afterburner), byte code weaving swiss army knife for Android.

###TODO

* [ ] CI + badge (depends on Robolectric fork)
* [ ] coverage
