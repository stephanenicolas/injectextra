package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.os.IBinder;
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
public class InjectResourceProcessorInServiceTest {
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
    TestService activity = Robolectric.buildService(TestService.class).create().get();
    assertThat(activity.string,
        is(Robolectric.application.getResources().getString(RESOURCE_ID_STRING)));
    assertThat(activity.intA,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(activity.intB,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(activity.boolA,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(activity.boolB,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(activity.arrayA,
        is(Robolectric.application.getResources().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(activity.arrayB,
        is(Robolectric.application.getResources().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(activity.anim);
    //doesn't work on Robolectric..
    //assertNotNull(activity.movie);
    assertNotNull(activity.colorStateList);
  }

  public static class TestService extends Service {
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

    @Override public IBinder onBind(Intent intent) {
      return null;
    }
  }
}
