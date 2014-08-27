package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;
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
  public static final String EXTRA_ID_PARCELABLE = "EXTRA_ID_PARCELABLE";
  public static final String EXTRA_ID_SERIALIZABLE = "EXTRA_ID_SERIALIZABLE";
  public static final String EXTRA_ID_INTEGER = "EXTRA_ID_INTEGER";
  public static final String EXTRA_ID_BOOLEAN = "EXTRA_ID_BOOLEAN";
  public static final String EXTRA_ID_BYTE = "EXTRA_ID_BYTE";
  public static final String EXTRA_ID_CHAR = "EXTRA_ID_CHAR";
  public static final String EXTRA_ID_CHAR_SEQUENCE = "EXTRA_ID_CHAR_SEQUENCE";
  public static final String EXTRA_ID_FLOAT = "EXTRA_ID_FLOAT";
  public static final String EXTRA_ID_DOUBLE = "EXTRA_ID_DOUBLE";
  public static final String EXTRA_ID_SHORT = "EXTRA_ID_SHORT";
  public static final String EXTRA_ID_BYTE_ARRAY = "EXTRA_ID_BYTE_ARRAY";
  public static final String EXTRA_ID_CHAR_ARRAY = "EXTRA_ID_CHAR_ARRAY";
  public static final String EXTRA_ID_STRING_ARRAY = "EXTRA_ID_STRING_ARRAY";
  public static final String EXTRA_ID_INTEGER_ARRAY = "EXTRA_ID_INTEGER_ARRAY";
  public static final String EXTRA_ID_FLOAT_ARRAY = "EXTRA_ID_FLOAT_ARRAY";
  public static final String EXTRA_ID_DOUBLE_ARRAY = "EXTRA_ID_DOUBLE_ARRAY";
  public static final String EXTRA_ID_SHORT_ARRAY = "EXTRA_ID_SHORT_ARRAY";
  public static final String EXTRA_ID_CHAR_SEQUENCE_ARRAY = "EXTRA_ID_CHAR_SEQUENCE_ARRAY";
  public static final String EXTRA_ID_PARCELABLE_ARRAY = "EXTRA_ID_PARCELABLE_ARRAY";
  public static final String EXTRA_ID_CHAR_SEQUENCE_ARRAY_LIST = "EXTRA_ID_CHAR_SEQUENCE_ARRAY_LIST";
  public static final String EXTRA_ID_STRING_ARRAY_LIST = "EXTRA_ID_STRING_ARRAY_LIST";
  public static final String EXTRA_ID_PARCELABLE_ARRAY_LIST = "EXTRA_ID_PARCELABLE_ARRAY_LIST";

  @Test
  public void shouldInjectExtra_simple() {
    Intent intent = new Intent();
    intent.putExtra(EXTRA_ID_STRING, "foo");
    intent.putExtra(EXTRA_ID_PARCELABLE, new Point());
    intent.putExtra(EXTRA_ID_SERIALIZABLE, "foo");
    intent.putExtra(EXTRA_ID_INTEGER, 2);
    intent.putExtra(EXTRA_ID_BOOLEAN, true);
    intent.putExtra(EXTRA_ID_BYTE, (byte)22);
    intent.putExtra(EXTRA_ID_CHAR, 'a');
    intent.putExtra(EXTRA_ID_CHAR_SEQUENCE, (CharSequence) new StringBuffer("foo"));
    intent.putExtra(EXTRA_ID_FLOAT, 12f);
    intent.putExtra(EXTRA_ID_DOUBLE, 12.0);
    intent.putExtra(EXTRA_ID_SHORT, (short)12);
    intent.putExtra(EXTRA_ID_STRING_ARRAY, new String[] { "foo", "bar" });
    intent.putExtra(EXTRA_ID_INTEGER_ARRAY, new int[] { 2, 3 });
    intent.putExtra(EXTRA_ID_CHAR_ARRAY, new char[] {'a'});
    intent.putExtra(EXTRA_ID_BYTE_ARRAY, new byte[] {(byte)2});
    intent.putExtra(EXTRA_ID_FLOAT_ARRAY, new float[] {12f});
    intent.putExtra(EXTRA_ID_DOUBLE_ARRAY, new double[] {12.0});
    intent.putExtra(EXTRA_ID_SHORT_ARRAY, new short[] {(short)12f});
    intent.putExtra(EXTRA_ID_CHAR_SEQUENCE_ARRAY, new CharSequence[] {new StringBuffer("foo")});
    intent.putExtra(EXTRA_ID_PARCELABLE_ARRAY, new Parcelable[] {new Point()});
    ArrayList<CharSequence> charSequenceArrayList = new ArrayList<>();
    charSequenceArrayList.add(new StringBuffer("foo"));
    intent.putExtra(EXTRA_ID_CHAR_SEQUENCE_ARRAY_LIST, charSequenceArrayList);
    ArrayList<String> stringArrayList = new ArrayList<>();
    stringArrayList.add(new String("foo"));
    intent.putExtra(EXTRA_ID_STRING_ARRAY_LIST, stringArrayList);
    ArrayList<Parcelable> pointArrayList = new ArrayList<>();
    pointArrayList.add(new Point());
    intent.putExtra(EXTRA_ID_PARCELABLE_ARRAY_LIST, pointArrayList);

    System.out.println("Extras used for tests " + intent.getExtras().toString());
    TestActivity activity =
        Robolectric.buildActivity(TestActivity.class).withIntent(intent).create().get();
    assertThat(activity.string, is(intent.getStringExtra(EXTRA_ID_STRING)));
    assertThat(activity.parcelable, is(intent.getParcelableExtra(EXTRA_ID_PARCELABLE)));
    assertThat(activity.serializable, is(intent.getSerializableExtra(EXTRA_ID_SERIALIZABLE)));
    assertThat(activity.intA, is(intent.getIntExtra(EXTRA_ID_INTEGER, 1)));
    assertThat(activity.intB, is(intent.getIntExtra(EXTRA_ID_INTEGER, 1)));
    assertThat(activity.boolA, is(intent.getBooleanExtra(EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.boolB, is(intent.getBooleanExtra(EXTRA_ID_BOOLEAN, false)));
    assertThat(activity.byteA, is(intent.getByteExtra(EXTRA_ID_BYTE, (byte) 1)));
    assertThat(activity.byteB, is(intent.getByteExtra(EXTRA_ID_BYTE, (byte)1)));
    assertThat(activity.charA, is(intent.getCharExtra(EXTRA_ID_CHAR, '\u0000')));
    assertThat(activity.charB, is(intent.getCharExtra(EXTRA_ID_CHAR, '\u0000')));
    assertThat(activity.charSeq, is(intent.getCharSequenceExtra(EXTRA_ID_CHAR_SEQUENCE)));
    assertThat(activity.floatA, is(intent.getFloatExtra(EXTRA_ID_FLOAT, 12f)));
    assertThat(activity.floatB, is(intent.getFloatExtra(EXTRA_ID_FLOAT, 12f)));
    assertThat(activity.doubleA, is(intent.getDoubleExtra(EXTRA_ID_DOUBLE, 12.0)));
    assertThat(activity.doubleB, is(intent.getDoubleExtra(EXTRA_ID_DOUBLE, 12.0)));
    assertThat(activity.shortA, is(intent.getShortExtra(EXTRA_ID_SHORT, (short) 12)));
    assertThat(activity.shortB, is(intent.getShortExtra(EXTRA_ID_SHORT, (short) 12)));

    assertThat(activity.arrayString, is(intent.getStringArrayExtra(EXTRA_ID_STRING_ARRAY)));
    assertThat(activity.arrayInteger, is(intent.getIntArrayExtra(EXTRA_ID_INTEGER_ARRAY)));
    assertThat(activity.arrayChars, is(intent.getCharArrayExtra(EXTRA_ID_CHAR_ARRAY)));
    assertThat(activity.arrayBytes, is(intent.getByteArrayExtra(EXTRA_ID_BYTE_ARRAY)));
    assertThat(activity.arrayFloats, is(intent.getFloatArrayExtra(EXTRA_ID_FLOAT_ARRAY)));
    assertThat(activity.arrayDoubles, is(intent.getDoubleArrayExtra(EXTRA_ID_DOUBLE_ARRAY)));
    assertThat(activity.arrayShorts, is(intent.getShortArrayExtra(EXTRA_ID_SHORT_ARRAY)));
    assertThat(activity.arrayCharSequences, is(intent.getCharSequenceArrayExtra(
        EXTRA_ID_CHAR_SEQUENCE_ARRAY)));
    assertThat(activity.arrayParcelables, is(intent.getParcelableArrayExtra(
        EXTRA_ID_PARCELABLE_ARRAY)));
    assertThat(activity.listChatSequences, is(intent.getCharSequenceArrayListExtra(
        EXTRA_ID_CHAR_SEQUENCE_ARRAY_LIST)));
    assertThat(activity.listStrings, is(intent.getStringArrayListExtra(EXTRA_ID_STRING_ARRAY_LIST)));
    assertThat(activity.listParcelables, is(intent.getParcelableArrayListExtra(
        EXTRA_ID_PARCELABLE_ARRAY_LIST)));
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
    @InjectExtra(EXTRA_ID_PARCELABLE)
    protected Parcelable parcelable;
    @InjectExtra(EXTRA_ID_SERIALIZABLE)
    protected Serializable serializable;
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
    @InjectExtra(EXTRA_ID_CHAR_SEQUENCE)
    protected CharSequence charSeq;
    @InjectExtra(EXTRA_ID_FLOAT)
    public float floatA;
    @InjectExtra(EXTRA_ID_FLOAT)
    public Float floatB;
    @InjectExtra(EXTRA_ID_DOUBLE)
    public double doubleA;
    @InjectExtra(EXTRA_ID_DOUBLE)
    public Double doubleB;
    @InjectExtra(EXTRA_ID_SHORT)
    public short shortA;
    @InjectExtra(EXTRA_ID_SHORT)
    public Short shortB;
    @InjectExtra(EXTRA_ID_STRING_ARRAY)
    protected String[] arrayString;
    @InjectExtra(EXTRA_ID_INTEGER_ARRAY)
    protected int[] arrayInteger;
    @InjectExtra(EXTRA_ID_CHAR_ARRAY)
    protected char[] arrayChars;
    @InjectExtra(EXTRA_ID_BYTE_ARRAY)
    protected byte[] arrayBytes;
    @InjectExtra(EXTRA_ID_FLOAT_ARRAY)
    protected float[] arrayFloats;
    @InjectExtra(EXTRA_ID_DOUBLE_ARRAY)
    protected double[] arrayDoubles;
    @InjectExtra(EXTRA_ID_SHORT_ARRAY)
    protected short[] arrayShorts;
    @InjectExtra(EXTRA_ID_CHAR_SEQUENCE_ARRAY)
    protected CharSequence[] arrayCharSequences;
    @InjectExtra(EXTRA_ID_STRING_ARRAY)
    protected CharSequence[] arrayStrings;
    @InjectExtra(EXTRA_ID_PARCELABLE_ARRAY)
    protected Parcelable[] arrayParcelables;
    @InjectExtra(EXTRA_ID_CHAR_SEQUENCE_ARRAY_LIST)
    protected ArrayList<CharSequence> listChatSequences;
    @InjectExtra(EXTRA_ID_STRING_ARRAY_LIST)
    protected ArrayList<String> listStrings;
    @InjectExtra(EXTRA_ID_PARCELABLE_ARRAY_LIST)
    protected ArrayList<Parcelable> listParcelables;
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
