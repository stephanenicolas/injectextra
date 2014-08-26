package com.github.stephanenicolas.injectextra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Movie;
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
@RunWith(InjectExtraTestRunner.class)
public class InjectExtraProcessorInBroadcastReceiverTest {
  public static final int RESOURCE_ID_STRING = R.string.string1;
  public static final int RESOURCE_ID_INTEGER = R.integer.integer1;
  public static final int RESOURCE_ID_BOOLEAN = R.bool.bool1;
  public static final int RESOURCE_ID_STRING_ARRAY = R.array.array1;
  public static final int RESOURCE_ID_INTEGER_ARRAY = R.array.intarray1;
  public static final int RESOURCE_ID_MOVIE = R.raw.small;
  public static final int RESOURCE_ID_ANIMATION = android.R.anim.fade_in;
  public static final int RESOURCE_COLOR_STATE_LIST = R.color.colorlist;

  @Test
  public void shouldInjectExtra_simple() {
    TestBroadcastReceiver broadcastReceiver = new TestBroadcastReceiver();
    broadcastReceiver.onReceive(Robolectric.application, null);
    assertThat(broadcastReceiver.string,
        is(Robolectric.application.getExtras().getString(RESOURCE_ID_STRING)));
    assertThat(broadcastReceiver.intA,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(broadcastReceiver.intB,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(broadcastReceiver.boolA,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(broadcastReceiver.boolB,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(broadcastReceiver.arrayA,
        is(Robolectric.application.getExtras().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(broadcastReceiver.arrayB,
        is(Robolectric.application.getExtras().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(broadcastReceiver.anim);
    //doesn't work on Robolectric..
    //assertNotNull(activity.movie);
    assertNotNull(broadcastReceiver.colorStateList);
  }

  public static class TestBroadcastReceiver extends BroadcastReceiver {
    @InjectExtra(RESOURCE_ID_STRING)
    protected String string;
    @InjectExtra(RESOURCE_ID_INTEGER)
    protected int intA;
    @InjectExtra(RESOURCE_ID_INTEGER)
    protected Integer intB;
    @InjectExtra(RESOURCE_ID_BOOLEAN)
    protected boolean boolA;
    @InjectExtra(RESOURCE_ID_BOOLEAN)
    protected Boolean boolB;
    @InjectExtra(RESOURCE_ID_STRING_ARRAY)
    protected String[] arrayA;
    @InjectExtra(RESOURCE_ID_INTEGER_ARRAY)
    protected int[] arrayB;
    @InjectExtra(RESOURCE_ID_MOVIE)
    protected Movie movie;
    @InjectExtra(RESOURCE_ID_ANIMATION)
    protected Animation anim;
    @InjectExtra(RESOURCE_COLOR_STATE_LIST)
    protected ColorStateList colorStateList;

    @Override
    public void onReceive(Context context, Intent intent) {
    }
  }
}
