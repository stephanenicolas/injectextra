package com.github.stephanenicolas.injectextra;

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
public class InjectExtraProcessorInApplicationTest {
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
    TestInjectExtraApplication app = (TestInjectExtraApplication) Robolectric.application;
    app.onCreate();
    assertThat(app.string,
        is(Robolectric.application.getExtras().getString(RESOURCE_ID_STRING)));
    assertThat(app.intA,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(app.intB,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(app.boolA,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(app.boolB,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(app.arrayA,
        is(Robolectric.application.getExtras().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(app.arrayB,
        is(Robolectric.application.getExtras().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(app.anim);
    //doesn't work on Robolectric..
    //assertNotNull(app.movie);
    assertNotNull(app.colorStateList);
  }
}
