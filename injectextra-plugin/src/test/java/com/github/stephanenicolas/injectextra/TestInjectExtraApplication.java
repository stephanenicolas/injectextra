package com.github.stephanenicolas.injectextra;

import android.app.Application;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.view.animation.Animation;

/**
* Created by administrateur on 2014-08-25.
*/
public class TestInjectExtraApplication extends Application {
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_STRING)
  protected String string;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_INTEGER)
  protected int intA;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_INTEGER)
  protected Integer intB;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_BOOLEAN)
  protected boolean boolA;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_BOOLEAN)
  protected Boolean boolB;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_STRING_ARRAY)
  protected String[] arrayA;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_INTEGER_ARRAY)
  protected int[] arrayB;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_MOVIE)
  protected Movie movie;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_ID_ANIMATION)
  protected Animation anim;
  @InjectExtra(InjectExtraProcessorInApplicationTest.RESOURCE_COLOR_STATE_LIST)
  protected ColorStateList colorStateList;
}
