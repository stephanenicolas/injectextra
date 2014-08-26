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
  public static final String EXTRA_ID_STRING = "EXTRA_ID_STRING";
  public static final String EXTRA_ID_INTEGER = "EXTRA_ID_INTEGER";
  public static final String EXTRA_ID_BOOLEAN = "EXTRA_ID_BOOLEAN";
  public static final String EXTRA_ID_BYTE = "EXTRA_ID_BYTE";
  public static final String EXTRA_ID_CHAR = "EXTRA_ID_CHAR";
  public static final String EXTRA_ID_BYTE_ARRAY = "EXTRA_ID_BYTE_ARRAY";
  public static final String EXTRA_ID_CHAR_ARRAY = "EXTRA_ID_CHAR_ARRAY";
  public static final String EXTRA_ID_STRING_ARRAY = "EXTRA_ID_STRING_ARRAY";
  public static final String EXTRA_ID_INTEGER_ARRAY = "EXTRA_ID_INTEGER_ARRAY";

  @Test
  public void shouldInjectExtra_simple() {
    Intent intent = new Intent();
    intent.putExtra(EXTRA_ID_STRING, "foo");
    intent.putExtra(EXTRA_ID_INTEGER, 2);
    intent.putExtra(EXTRA_ID_BOOLEAN, true);
    intent.putExtra(EXTRA_ID_BYTE, (byte)22);
    intent.putExtra(EXTRA_ID_CHAR, 'a');
    intent.putExtra(EXTRA_ID_STRING_ARRAY, new String[] { "foo", "bar" });
    intent.putExtra(EXTRA_ID_INTEGER_ARRAY, new int[] { 2, 3 });
    intent.putExtra(EXTRA_ID_CHAR_ARRAY, new char[] {'a'});
    intent.putExtra(EXTRA_ID_BYTE_ARRAY, new byte[] {(byte)2});

    System.out.println("Extras used for tests " + intent.getExtras().toString());
    TestActivity activity =
        Robolectric.buildActivity(TestActivity.class).withIntent(intent).create().get();
    assertThat(activity.string, is(intent.getStringExtra(EXTRA_ID_STRING)));
    assertThat(activity.intA, is(intent.getIntExtra(EXTRA_ID_INTEGER, 1)));
    assertThat(activity.intB, is(intent.getIntExtra(EXTRA_ID_INTEGER, 1)));
    assertThat(activity.boolA, is(intent.getBooleanExtra(EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.boolB, is(intent.getBooleanExtra(EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.byteA, is(intent.getByteExtra(EXTRA_ID_BYTE, (byte)1)));
    assertThat(activity.byteB, is(intent.getByteExtra(EXTRA_ID_BYTE, (byte)1)));
    assertThat(activity.charA, is(intent.getCharExtra(EXTRA_ID_CHAR, '\u0000')));
    assertThat(activity.charB, is(intent.getCharExtra(EXTRA_ID_CHAR, '\u0000')));
    assertThat(activity.arrayA, is(intent.getStringArrayExtra(EXTRA_ID_STRING_ARRAY)));
    assertThat(activity.arrayB, is(intent.getIntArrayExtra(EXTRA_ID_INTEGER_ARRAY)));
    assertThat(activity.arrayC, is(intent.getCharArrayExtra(EXTRA_ID_CHAR_ARRAY)));
    assertThat(activity.arrayD, is(intent.getByteArrayExtra(EXTRA_ID_BYTE_ARRAY)));
  }

  @Test(expected = RuntimeException.class)
  public void shouldInjectExtra_optional_false() {
     Robolectric.buildActivity(TestActivity.class).create().get();
  }

  @Test(expected = RuntimeException.class)
  public void shouldInjectExtra_optional_true() {
     Robolectric.buildActivity(TestActivityOptional.class).create().get();
  }

  @Test
  public void shouldInjectExtra_optional_true_with_defaults() {
     Robolectric.buildActivity(TestActivityOptionalWithDefaults.class).create().get();
  }

  @Test
  public void shouldInjectExtra_nullable() {
    Robolectric.buildActivity(TestActivityNullable.class).create().get();
  }

  public static class TestActivity extends Activity {

    @InjectExtra(EXTRA_ID_STRING)
    protected String string;
    @InjectExtra(EXTRA_ID_INTEGER)
    protected int intA;
    @InjectExtra(EXTRA_ID_INTEGER)
    protected Integer intB;
    @InjectExtra(EXTRA_ID_BOOLEAN)
    protected boolean boolA;
    @InjectExtra(EXTRA_ID_BOOLEAN)
    protected Boolean boolB;
    @InjectExtra(EXTRA_ID_BYTE)
    protected byte byteA;
    @InjectExtra(EXTRA_ID_BYTE)
    protected Byte byteB;
    @InjectExtra(EXTRA_ID_CHAR)
    protected char charA;
    @InjectExtra(EXTRA_ID_CHAR)
    protected Character charB;
    @InjectExtra(EXTRA_ID_STRING_ARRAY)
    protected String[] arrayA;
    @InjectExtra(EXTRA_ID_INTEGER_ARRAY)
    protected int[] arrayB;
    @InjectExtra(EXTRA_ID_CHAR_ARRAY)
    protected char[] arrayC;
    @InjectExtra(EXTRA_ID_BYTE_ARRAY)
    protected byte[] arrayD;
  }

  public static class TestActivityOptional extends Activity {
    @InjectExtra(value = EXTRA_ID_STRING, optional = true)
    protected String string;
    @InjectExtra(value = EXTRA_ID_INTEGER, optional = true)
    protected int intA;
    @InjectExtra(value = EXTRA_ID_INTEGER, optional = true)
    protected Integer intB;
    @InjectExtra(value = EXTRA_ID_BOOLEAN, optional = true)
    protected boolean boolA;
    @InjectExtra(value = EXTRA_ID_BOOLEAN, optional = true)
    protected Boolean boolB;
    @InjectExtra(value = EXTRA_ID_STRING_ARRAY, optional = true)
    protected String[] arrayA;
    @InjectExtra(value = EXTRA_ID_INTEGER_ARRAY, optional = true)
    protected int[] arrayB;
  }

  public static class TestActivityOptionalWithDefaults extends Activity {
    @InjectExtra(value = EXTRA_ID_STRING, optional = true)
    protected String string = "";
    @InjectExtra(value = EXTRA_ID_INTEGER, optional = true)
    protected int intA;
    @InjectExtra(value = EXTRA_ID_INTEGER, optional = true)
    protected Integer intB = 5;
    @InjectExtra(value = EXTRA_ID_BOOLEAN, optional = true)
    protected boolean boolA;
    @InjectExtra(value = EXTRA_ID_BOOLEAN, optional = true)
    protected Boolean boolB = false;
    @InjectExtra(value = EXTRA_ID_STRING_ARRAY, optional = true)
    protected String[] arrayA = new String[] {""};
    @InjectExtra(value = EXTRA_ID_INTEGER_ARRAY, optional = true)
    protected int[] arrayB = new int[] {5};
  }

  public static class TestActivityNullable extends Activity {
    @Nullable
    @InjectExtra(value = EXTRA_ID_STRING, optional = true)
    protected String string;
    @Nullable
    @InjectExtra(value = EXTRA_ID_INTEGER, optional = true)
    protected Integer intB;
    @Nullable
    @InjectExtra(value = EXTRA_ID_BOOLEAN, optional = true)
    protected Boolean boolB;
    @Nullable
    @InjectExtra(value = EXTRA_ID_STRING_ARRAY, optional = true)
    protected String[] arrayA;
    @Nullable
    @InjectExtra(value = EXTRA_ID_INTEGER_ARRAY, optional = true)
    protected int[] arrayB;
  }

  public static @interface Nullable {}
}
