package com.github.stephanenicolas.injectextra;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.view.animation.Animation;
import com.test.injectextra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author SNI
 */
@RunWith(InjectResourceTestRunner.class)
public class InjectResourceProcessorInContentProviderTest {
  public static final int RESOURCE_ID_STRING = R.string.string1;
  public static final int RESOURCE_ID_INTEGER = R.integer.integer1;
  public static final int RESOURCE_ID_BOOLEAN = R.bool.bool1;
  public static final int RESOURCE_ID_STRING_ARRAY = R.array.array1;
  public static final int RESOURCE_ID_INTEGER_ARRAY = R.array.intarray1;
  public static final int RESOURCE_ID_MOVIE = R.raw.small;
  public static final int RESOURCE_ID_ANIMATION = android.R.anim.fade_in;
  public static final int RESOURCE_COLOR_STATE_LIST = R.color.colorlist;

  @Test
  public void shouldInjectResource_simple() {
    TestContentProvider broadcastReceiver = new TestContentProvider();
    broadcastReceiver.attachInfo(Robolectric.application, null);
    broadcastReceiver.onCreate();
    assertThat(broadcastReceiver.string,
        is(Robolectric.application.getResources().getString(RESOURCE_ID_STRING)));
    assertThat(broadcastReceiver.intA,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(broadcastReceiver.intB,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(broadcastReceiver.boolA,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(broadcastReceiver.boolB,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(broadcastReceiver.arrayA,
        is(Robolectric.application.getResources().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(broadcastReceiver.arrayB,
        is(Robolectric.application.getResources().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(broadcastReceiver.anim);
    //doesn't work on Robolectric..
    //assertNotNull(activity.movie);
    assertNotNull(broadcastReceiver.colorStateList);
  }

  public static class TestContentProvider extends ContentProvider {
    @InjectResource(RESOURCE_ID_STRING)
    protected String string;
    @InjectResource(RESOURCE_ID_INTEGER)
    protected int intA;
    @InjectResource(RESOURCE_ID_INTEGER)
    protected Integer intB;
    @InjectResource(RESOURCE_ID_BOOLEAN)
    protected boolean boolA;
    @InjectResource(RESOURCE_ID_BOOLEAN)
    protected Boolean boolB;
    @InjectResource(RESOURCE_ID_STRING_ARRAY)
    protected String[] arrayA;
    @InjectResource(RESOURCE_ID_INTEGER_ARRAY)
    protected int[] arrayB;
    @InjectResource(RESOURCE_ID_MOVIE)
    protected Movie movie;
    @InjectResource(RESOURCE_ID_ANIMATION)
    protected Animation anim;
    @InjectResource(RESOURCE_COLOR_STATE_LIST)
    protected ColorStateList colorStateList;

    @Override public boolean onCreate() {
      return false;
    }

    @Override public Cursor query(Uri uri, String[] strings, String s, String[] strings2,
        String s2) {
      return null;
    }

    @Override public String getType(Uri uri) {
      return null;
    }

    @Override public Uri insert(Uri uri, ContentValues contentValues) {
      return null;
    }

    @Override public int delete(Uri uri, String s, String[] strings) {
      return 0;
    }

    @Override public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
      return 0;
    }
  }
}
