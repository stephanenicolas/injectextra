package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.animation.Animation;
import com.github.stephanenicolas.injectextra.InjectExtra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author SNI
 */
@RunWith(InjectExtraTestRunner.class)
public class InjectExtraProcessorInActivityTest {
  @Test
  public void shouldInjectExtra_simple() {
    Bundle bundle = new Bundle();
    bundle.putString(TestActivity.EXTRA_ID_STRING, "foo");
    bundle.putInt(TestActivity.EXTRA_ID_INTEGER, 2);
    bundle.putBoolean(TestActivity.EXTRA_ID_BOOLEAN, true);
    bundle.putStringArray(TestActivity.EXTRA_ID_STRING_ARRAY, new String[] {"foo","bar"});
    bundle.putIntArray(TestActivity.EXTRA_ID_INTEGER_ARRAY, new int[] {2,3});

    TestActivity activity = Robolectric.buildActivity(TestActivity.class).create(bundle).get();
    assertThat(activity.string,
        is(bundle.getString(TestActivity.EXTRA_ID_STRING)));
    assertThat(activity.intA,
        is(bundle.getInt(TestActivity.EXTRA_ID_INTEGER)));
    assertThat(activity.intB,
        is(bundle.getInt(TestActivity.EXTRA_ID_INTEGER)));
    assertThat(activity.boolA,
        is(bundle.getBoolean(TestActivity.EXTRA_ID_BOOLEAN)));
    assertThat(activity.boolB,
        is(bundle.getBoolean(TestActivity.EXTRA_ID_BOOLEAN)));
    assertThat(activity.arrayA,
        is(bundle.getStringArray(TestActivity.EXTRA_ID_STRING_ARRAY)));
    assertThat(activity.arrayB,
        is(bundle.getIntArray(TestActivity.EXTRA_ID_INTEGER_ARRAY)));
  }

  public static class TestActivity extends Activity {
    public static final String EXTRA_ID_STRING = "EXTRA_ID_STRING";
    public static final String EXTRA_ID_INTEGER = "EXTRA_ID_INTEGER";
    public static final String EXTRA_ID_BOOLEAN = "EXTRA_ID_BOOLEAN";
    public static final String EXTRA_ID_STRING_ARRAY = "EXTRA_ID_STRING_ARRAY";
    public static final String EXTRA_ID_INTEGER_ARRAY = "EXTRA_ID_INTEGER_ARRAY";

    @InjectExtra(TestActivity.EXTRA_ID_STRING)
    protected String string;
    @InjectExtra(TestActivity.EXTRA_ID_INTEGER)
    protected int intA;
    @InjectExtra(TestActivity.EXTRA_ID_INTEGER)
    protected Integer intB;
    @InjectExtra(TestActivity.EXTRA_ID_BOOLEAN)
    protected boolean boolA;
    @InjectExtra(TestActivity.EXTRA_ID_BOOLEAN)
    protected Boolean boolB;
    @InjectExtra(TestActivity.EXTRA_ID_STRING_ARRAY)
    protected String[] arrayA;
    @InjectExtra(TestActivity.EXTRA_ID_INTEGER_ARRAY)
    protected int[] arrayB;
  }
}
