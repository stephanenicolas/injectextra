package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.view.View;
import android.view.animation.Animation;
import com.test.injectextra.R;
import javassist.ClassPool;
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
public class InjectExtraProcessorInOtherTest {
  public static final int RESOURCE_ID_STRING = R.string.string1;
  public static final int RESOURCE_ID_INTEGER = R.integer.integer1;
  public static final int RESOURCE_ID_BOOLEAN = R.bool.bool1;
  public static final int RESOURCE_ID_STRING_ARRAY = R.array.array1;
  public static final int RESOURCE_ID_INTEGER_ARRAY = R.array.intarray1;
  public static final int RESOURCE_ID_MOVIE = R.raw.small;
  public static final int RESOURCE_ID_ANIMATION = android.R.anim.fade_in;
  public static final int RESOURCE_COLOR_STATE_LIST = R.color.colorlist;
  public static final String TAG = "TAG";

  @Test
  public void shouldInjectExtra_inConstructorWithActivityParam() {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    TestPojo pojo = new TestPojo(activity);
    assertPojoHasAllFields(pojo);
  }

  @Test
  public void shouldInjectExtra_inConstructorWithActivityParamAndOtherParams() {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    TestPojo pojo = new TestPojo(activity, "");
    assertPojoHasAllFields(pojo);
  }

  @Test
  public void shouldInjectExtra_inConstructorWithFragment() {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    Fragment fragment = new Fragment();
    activity.getFragmentManager().beginTransaction().add(fragment, TAG).commit();
    TestPojo pojo = new TestPojo(fragment);
    assertPojoHasAllFields(pojo);
  }

  @Test
  public void shouldInjectExtra_inConstructorWithTextView() {
    Activity activity = Robolectric.buildActivity(Activity.class).create().get();
    View view = new View(activity);
    TestPojo pojo = new TestPojo(view);
    assertPojoHasAllFields(pojo);
  }

  @Test
  public void shouldInjectExtra_FailsWhenNoSuitableConstructors() throws Exception {
    ClassPool pool = ClassPool.getDefault();
    pool.appendSystemPath();
    InjectExtraProcessor processor = new InjectExtraProcessor();
    assertThat(processor.shouldTransform(pool.get(InvalidTestPojo.class.getName())), is(false));
  }

  private void assertPojoHasAllFields(TestPojo pojo) {
    assertThat(pojo.string, is(Robolectric.application.getText(RESOURCE_ID_STRING)));
    assertThat(pojo.intA,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(pojo.intB,
        is(Robolectric.application.getExtras().getInteger(RESOURCE_ID_INTEGER)));
    assertThat(pojo.boolA,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(pojo.boolB,
        is(Robolectric.application.getExtras().getBoolean(RESOURCE_ID_BOOLEAN)));
    assertThat(pojo.arrayA,
        is(Robolectric.application.getExtras().getStringArray(RESOURCE_ID_STRING_ARRAY)));
    assertThat(pojo.arrayB,
        is(Robolectric.application.getExtras().getIntArray(RESOURCE_ID_INTEGER_ARRAY)));
    assertNotNull(pojo.anim);
    //doesn't work on Robolectric..
    //assertNotNull(activity.movie);
    assertNotNull(pojo.colorStateList);
  }

  public static class TestPojo {
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

    public TestPojo(Activity activity) {
    }

    public TestPojo(Activity activity, String s) {
    }

    public TestPojo(Fragment fragment) {
    }

    public TestPojo(View view) {
    }
  }

  public static class InvalidTestPojo {
    @InjectExtra(RESOURCE_ID_STRING)
    protected String string;
  }
}
