package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import com.github.stephanenicolas.afterburner.AfterBurner;
import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

/**
 * Will inject all extras in the following supported classes :
 * <ul>
 * <li>{@link Activity}</li>
 * <li>{@link Service}</li>
 * <li>{@link Application}</li>
 * <li>{@link Fragment}</li>
 * <li>support Fragment</li>
 * <li>{@link View}</li>
 * <li>{@link ContentProvider}</li>
 * <li>and any other class that defines a constructor with an argument
 * that is in the list above</li>
 * <li>{@link BroadcastReceiver}</li>
 * </ul>
 *
 * <pre>
 * <ul>
 *   <li>for activities, services and applications:
 *     <ul>
 *       <li>right after super.onCreate in onCreate
 *     </ul>
 *   <li>for contentProviders:
 *     <ul>
 *       <li>at the beginning of onCreate
 *     </ul>
 *   <li>for broadcastReceivers:
 *     <ul>
 *       <li>at the beginning of onReceive
 *     </ul>
 *   <li>for fragments :
 *     <ul>
 *       <li>right after super.onAttach in onAttach
 *     </ul>
 *   <li>for views :
 *     <ul>
 *       <li>right after super.onFinishInflate in onFinishInflate
 *       <li>onFinishInflate is called automatically by Android when inflating a view from XML
 *       <li>onFinishInflate must be called manually in constructors of views with a single context
 * argument. You should invoke it after inflating your layout manually.
 *     </ul>
 *   <li>for other classes (namely MVP presenters and view holder design patterns) :
 *     <ul>
 *       <li>right before any constructor with an argument of a supported type
 *       (except BroadcastReceivers)
 *       <li>inner classes can only be processed if they are static
 *     </ul>
 * </ul>
 * </pre>
 *
 * @author SNI
 */
@Slf4j
public class InjectExtraProcessor implements IClassTransformer {

  @Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
  return false;
  }

  @Override
  public void applyTransformations(final CtClass classToTransform) throws JavassistBuildException {
  }


}
