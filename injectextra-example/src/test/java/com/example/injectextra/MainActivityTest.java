package com.example.injectextra;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

  protected MainActivity activity;

  @Before
  public void setup() {
    activity = Robolectric.buildActivity(MainActivity.class).create().get();
  }

  @Test
  public void shouldInjectView() {
    assertNotNull(activity.getView());
    assertThat(activity.getView().getText(), is(activity.getText(R.string.hello_world)));
  }
}