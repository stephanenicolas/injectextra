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
@RunWith(InjectResourceTestRunner.class)
public class InjectResourceProcessorInApplicationTest {
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
    TestInjectResourceApplication app = (TestInjectResourceApplication) Robolectric.application;
    app.onCreate();
    assertThat(app.string,
        is(Robolectric.application.getResources().getString(RESOURCE_ID_STRING)));
    assertThat(app.intA,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(app.intB,
        is(Robolectric.application.getResources().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(app.boolA,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(app.boolB,
        is(Robolectric.application.getResources().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(app.arrayA,
        is(Robolectric.application.getResources().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(app.arrayB,
        is(Robolectric.application.getResources().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(app.anim);
    //doesn't work on Robolectric..
    //assertNotNull(app.movie);
    assertNotNull(app.colorStateList);
  }
}
