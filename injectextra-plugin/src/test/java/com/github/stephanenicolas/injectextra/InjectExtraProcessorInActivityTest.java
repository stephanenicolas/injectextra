package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author SNI
 */
@RunWith(InjectExtraTestRunner.class)
public class InjectExtraProcessorInActivityTest {
  @Test
  public void shouldInjectExtra_simple() {
    Intent intent = new Intent();
    intent.putExtra(TestActivity.EXTRA_ID_STRING, "foo");
    intent.putExtra(TestActivity.EXTRA_ID_INTEGER, 2);
    intent.putExtra(TestActivity.EXTRA_ID_BOOLEAN, true);
    intent.putExtra(TestActivity.EXTRA_ID_STRING_ARRAY, new String[] { "foo", "bar" });
    intent.putExtra(TestActivity.EXTRA_ID_INTEGER_ARRAY, new int[] { 2, 3 });

    TestActivity activity =
        Robolectric.buildActivity(TestActivity.class).withIntent(intent).create().get();
    assertThat(activity.string, is(intent.getStringExtra(TestActivity.EXTRA_ID_STRING)));
    assertThat(activity.intA, is(intent.getIntExtra(TestActivity.EXTRA_ID_INTEGER, -1)));
    assertThat(activity.intB, is(intent.getIntExtra(TestActivity.EXTRA_ID_INTEGER, -1)));
    assertThat(activity.boolA, is(intent.getBooleanExtra(TestActivity.EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.boolB, is(intent.getBooleanExtra(TestActivity.EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.arrayA, is(intent.getStringArrayExtra(TestActivity.EXTRA_ID_STRING_ARRAY)));
    assertThat(activity.arrayB, is(intent.getIntArrayExtra(TestActivity.EXTRA_ID_INTEGER_ARRAY)));
  }

  @Test(expected = RuntimeException.class)
  public void shouldInjectExtra_optional_false() {
     Robolectric.buildActivity(TestActivity.class).create().get();
  }

  @Test
  public void shouldInjectExtra_optional_true() {
     Robolectric.buildActivity(TestActivityOptional.class).create().get();
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

  public static class TestActivityOptional extends Activity {
    public static final String EXTRA_ID_STRING = "EXTRA_ID_STRING";
    public static final String EXTRA_ID_INTEGER = "EXTRA_ID_INTEGER";
    public static final String EXTRA_ID_BOOLEAN = "EXTRA_ID_BOOLEAN";
    public static final String EXTRA_ID_STRING_ARRAY = "EXTRA_ID_STRING_ARRAY";
    public static final String EXTRA_ID_INTEGER_ARRAY = "EXTRA_ID_INTEGER_ARRAY";

    @InjectExtra(value = TestActivity.EXTRA_ID_STRING, optional = true)
    protected String string;
    @InjectExtra(value = TestActivity.EXTRA_ID_INTEGER, optional = true)
    protected int intA;
    @InjectExtra(value = TestActivity.EXTRA_ID_INTEGER, optional = true)
    protected Integer intB;
    @InjectExtra(value = TestActivity.EXTRA_ID_BOOLEAN, optional = true)
    protected boolean boolA;
    @InjectExtra(value = TestActivity.EXTRA_ID_BOOLEAN, optional = true)
    protected Boolean boolB;
    @InjectExtra(value = TestActivity.EXTRA_ID_STRING_ARRAY, optional = true)
    protected String[] arrayA;
    @InjectExtra(value = TestActivity.EXTRA_ID_INTEGER_ARRAY, optional = true)
    protected int[] arrayB;
  }
}
