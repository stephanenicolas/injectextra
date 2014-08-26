package com.github.stephanenicolas.injectresource;

import android.app.Application;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.view.animation.Animation;

/**
* Created by administrateur on 2014-08-25.
*/
public class TestInjectResourceApplication extends Application {
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_STRING)
  protected String string;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_INTEGER)
  protected int intA;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_INTEGER)
  protected Integer intB;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_BOOLEAN)
  protected boolean boolA;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_BOOLEAN)
  protected Boolean boolB;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_STRING_ARRAY)
  protected String[] arrayA;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_INTEGER_ARRAY)
  protected int[] arrayB;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_MOVIE)
  protected Movie movie;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_ID_ANIMATION)
  protected Animation anim;
  @InjectResource(InjectResourceProcessorInApplicationTest.RESOURCE_COLOR_STATE_LIST)
  protected ColorStateList colorStateList;
}
