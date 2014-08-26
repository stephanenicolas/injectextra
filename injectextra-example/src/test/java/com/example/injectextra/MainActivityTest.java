package com.example.injectextra;

import android.os.Bundle;
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

  @Test
  public void shouldInjectExtra() {
    Bundle bundle = new Bundle();
    bundle.putString(SecondActivity.APP_NAME, "foo");
    SecondActivity activity = Robolectric.buildActivity(SecondActivity.class).create(bundle).get();
    assertNotNull(activity.getView());
    assertThat(activity.getView().getText().toString(), is("foo"));
  }
}