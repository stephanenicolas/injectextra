package com.github.stephanenicolas.injectresource;

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
 * Will inject all resources in the following supported classes :
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
public class InjectResourceProcessor implements IClassTransformer {

  private final static String GET_APPLICATION_TAG = "GET_APPLICATION_TAG";
  private static final String GET_ROOT_TAG = "GET_ROOT";

  private AfterBurner afterBurner = new AfterBurner();

  @Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
    try {
      final List<CtField> fields =
          getAllInjectedFieldsForAnnotation(candidateClass, InjectResource.class);

      if (fields.isEmpty()) {
        return false;
      }

      boolean isAcceptedClass = isValidClass(candidateClass);

      if (!isAcceptedClass) {
        List<CtConstructor> ctConstructors = extractValidConstructors(candidateClass);
        isAcceptedClass = !ctConstructors.isEmpty();
      }

      log.debug(format("Class %s will get transformed: %b", candidateClass.getSimpleName(),
          isAcceptedClass));
      return isAcceptedClass;
    } catch (Exception e) {
      String message = format("Error while filtering class %s", candidateClass.getName());
      log.debug(message, e);
      throw new JavassistBuildException(message, e);
    }
  }

  @Override
  public void applyTransformations(final CtClass classToTransform) throws JavassistBuildException {
    // Actually you must test if it exists, but it's just an example...
    log.debug("Analyzing " + classToTransform.getSimpleName());

    try {
      final List<CtField> fields =
          getAllInjectedFieldsForAnnotation(classToTransform, InjectResource.class);
      String getResourceString = getResourceString();
      String injectViewStatements = injectResourceStatements(fields, classToTransform);

      StringBuffer buffer = new StringBuffer();
      buffer.append(getResourceString).append(injectViewStatements);

      String preliminaryBody = buffer.toString();
      log.debug("Preliminary body (before insertion) :" + preliminaryBody);
      injectStuff(classToTransform, preliminaryBody);
    } catch (Exception e) {
      String message = format("Error while processing class %s", classToTransform.getName());
      log.debug(message, e);
      throw new JavassistBuildException(message, e);
    }
  }

  //extension point for new classes
  private void injectStuff(CtClass targetClazz, String preliminaryBody)
      throws CannotCompileException, AfterBurnerImpossibleException, NotFoundException,
      InjectResourceException {

    if (isActivity(targetClazz)
        || isService(targetClazz)
        || isApplication(targetClazz)) {
      String body = createBodyWithInsertion(targetClazz, preliminaryBody, "this");
      afterBurner.afterOverrideMethod(targetClazz, "onCreate", body);
    } else if (isContentProvider(targetClazz)) {
      String body = createBodyWithInsertion(targetClazz, preliminaryBody, "this");
      CtMethod onReceiveMethod = afterBurner.extractExistingMethod(targetClazz, "onCreate");
      onReceiveMethod.insertBefore(body);
    } else if (isFragment(targetClazz) || isSupportFragment(targetClazz)) {
      String body = createBodyWithInsertion(targetClazz, preliminaryBody, "this");
      afterBurner.afterOverrideMethod(targetClazz, "onAttach", body);
    } else if (isView(targetClazz)) {
      String body = createBodyWithInsertion(targetClazz, preliminaryBody, "this");
      afterBurner.afterOverrideMethod(targetClazz, "onFinishInflate", body);
    } else if (isBroadCastReceiver(targetClazz)) {
      CtClass contextClass = targetClazz.getClassPool().get(Context.class.getName());
      String body = createBodyWithInsertion(contextClass, preliminaryBody, "$1");
      CtMethod onReceiveMethod = afterBurner.extractExistingMethod(targetClazz, "onReceive");
      onReceiveMethod.insertBefore(body);
    } else {
      List<CtConstructor> ctConstructors = extractValidConstructors(targetClazz);
      if (ctConstructors.isEmpty()) {
        throw new InjectResourceException(format(
            "Injecting resource in %s is not supported. Injection is supported in Activities, "
                + "Fragments (native and support), views and classes with a single constructor "
                + "that accept a single paramater which has to "
                + "be of one of the type listed earlier.", targetClazz));
      }
      for (CtConstructor ctConstructor : ctConstructors) {
        CtClass[] parameterTypes = ctConstructor.getParameterTypes();
        int indexParam = findValidParamIndex(parameterTypes);
        log.debug(
            format("Valid param in constructor with type %s: %d", ctConstructor.getSignature(),
                indexParam));

        CtClass ctParamClass = parameterTypes[indexParam];
        String bodyCopy =
            createBodyWithInsertion(ctParamClass, preliminaryBody, "$" + (1 + indexParam));
        log.debug(
            format("Body to be injected in constructor with type %s: %s", ctParamClass.getName(),
                bodyCopy));
        ctConstructor.insertBeforeBody(bodyCopy);
      }
    }
  }

  private String createBodyWithInsertion(CtClass targetClazz, String preliminaryBody, String root) {
    //need a fresh copy for each call (constructor loop for instance)
    String bodyCopy = new String(preliminaryBody);
    String getApplicationString = getApplicationString(targetClazz);
    getApplicationString =
        getApplicationString.replaceAll(GET_ROOT_TAG, Matcher.quoteReplacement(root));
    bodyCopy =
        bodyCopy.replaceAll(GET_APPLICATION_TAG, Matcher.quoteReplacement(getApplicationString));
    return bodyCopy;
  }

  private List<CtField> getAllInjectedFieldsForAnnotation(CtClass clazz,
      Class<? extends Annotation> annotationClazz) {
    List<CtField> result = new ArrayList<CtField>();
    CtField[] allFields = clazz.getDeclaredFields();
    log.debug("Scanning fields in " + clazz.getName());
    for (CtField field : allFields) {
      log.debug("Discovered field " + field.getName());
      if (field.hasAnnotation(annotationClazz)) {
        log.debug(
            "Field " + field.getName() + " has annotation " + annotationClazz.getSimpleName());
        result.add(field);
      }
    }
    return result;
  }

  private List<CtConstructor> extractValidConstructors(final CtClass classToTransform) {
    try {
      List<CtConstructor> constructors = new ArrayList<CtConstructor>();
      CtConstructor[] declaredConstructors = classToTransform.getDeclaredConstructors();
      for (CtConstructor constructor : declaredConstructors) {
        CtClass[] paramClasses = constructor.getParameterTypes();
        if (paramClasses.length >= 1) {
          for (CtClass paramClass : paramClasses) {
            if (isValidClass(paramClass)) {
              constructors.add(constructor);
              continue;
            }
          }
        }
      }
      return constructors;
    } catch (Exception e) {
      log.debug("Problem in extraction of constructors", e);
      return Collections.EMPTY_LIST;
    }
  }

  //private List<CtMethod> extractValidMethods(final CtClass classToTransform, String methodName) {
  //  try {
  //    List<CtMethod> ctMethods = new ArrayList<CtMethod>();
  //    CtMethod[] declaredMethods = classToTransform.getDeclaredMethods();
  //    for (CtMethod ctMethod : declaredMethods) {
  //      CtClass[] paramClasses = ctMethod.getParameterTypes();
  //      if (ctMethod.getName().equals(methodName) && paramClasses.length >= 1) {
  //        for (CtClass paramClass : paramClasses) {
  //          if (isValidClass(paramClass)) {
  //            ctMethods.add(ctMethod);
  //            continue;
  //          }
  //        }
  //      }
  //    }
  //    return ctMethods;
  //  } catch (Exception e) {
  //    log.debug("Problem in extraction of methods", e);
  //    return Collections.EMPTY_LIST;
  //  }
  //}

  private int findValidParamIndex(CtClass[] parameterTypes) {
    int indexParam = 0;
    for (CtClass paramClass : parameterTypes) {
      if (isValidClass(paramClass)) {
        return indexParam;
      } else {
        indexParam++;
      }
    }
    return -1;
  }

  //extension point for new classes
  private String getApplicationString(CtClass targetClazz) {
    if (isApplication(targetClazz)) {
      return GET_ROOT_TAG;
    } else if (isActivity(targetClazz) || isService(targetClazz)) {
      return GET_ROOT_TAG + ".getApplication()";
    } else if (isFragment(targetClazz) || isSupportFragment(targetClazz)) {
      return GET_ROOT_TAG + ".getActivity().getApplication()";
    } else if (isContext(targetClazz)) {
      //WARNING isContext should be last (after other more specific contexts)
      return "((android.app.Application) " + GET_ROOT_TAG + ".getApplicationContext())";
    } else if (isView(targetClazz) || isContentProvider(targetClazz)) {
      return "((android.app.Application) "
          + GET_ROOT_TAG
          + ".getContext().getApplicationContext())";
    } else {
      throw new RuntimeException("How did we get there ?");
    }
  }

  private String getResourceString() {
    return new StringBuilder().append("android.app.Application application = ")
        .append(GET_APPLICATION_TAG)
        .append(";\n")
        .append("android.content.res.Resources resources = application.getResources();\n")
        .toString();
  }

  private String injectResourceStatements(List<CtField> viewsToInject, CtClass targetClazz)
      throws ClassNotFoundException, NotFoundException {
    StringBuffer buffer = new StringBuffer();
    for (CtField field : viewsToInject) {
      Object annotation = field.getAnnotation(InjectResource.class);
      //must be accessed by introspection as I get a Proxy during tests.
      //this proxy comes from Robolectric
      Class annotionClass = annotation.getClass();

      //workaround for robolectric
      //https://github.com/robolectric/robolectric/pull/1240
      int id = 0;
      String tag = "";
      try {
        Method method = annotionClass.getMethod("value");
        id = (Integer) method.invoke(annotation);
        method = annotionClass.getMethod("name");
        tag = (String) method.invoke(annotation);
      } catch (Exception e) {
        throw new RuntimeException("How can we get here ?");
      }
      boolean isUsingId = id != -1;

      buffer.append(field.getName());
      buffer.append(" = ");

      String root = "resources";
      String findResourceString = "";
      ClassPool classPool = targetClazz.getClassPool();
      if (isSubClass(classPool, field.getType(), String.class)) {
        findResourceString = "getString(" + id + ")";
      } else if (field.getType().subtypeOf(CtClass.booleanType)) {
        findResourceString = "getBoolean(" + id + ")";
      } else if (isSubClass(classPool, field.getType(), Boolean.class)) {
        root = null;
        findResourceString = "new Boolean(resources.getBoolean(" + id + "))";
      } else if (isSubClass(classPool, field.getType(), ColorStateList.class)) {
        findResourceString = "getColorStateList(" + id + ")";
      } else if (field.getType().subtypeOf(CtClass.intType)) {
        findResourceString = "getInteger(" + id + ")";
      } else if (isSubClass(classPool, field.getType(), Integer.class)) {
        root = null;
        findResourceString = "new Integer(resources.getInteger(" + id + "))";
      } else if (isSubClass(classPool, field.getType(), Drawable.class)) {
        findResourceString = "getDrawable(" + id + ")";
      } else if (isStringArray(field, classPool)) {
        findResourceString = "getStringArray(" + id + ")";
      } else if (isIntArray(field)) {
        findResourceString = "getIntArray(" + id + ")";
      } else if (isSubClass(classPool, field.getType(), Animation.class)) {
        root = null;
        findResourceString =
            "android.view.animation.AnimationUtils.loadAnimation(" + "application, " + id + ")";
      } else if (isSubClass(classPool, field.getType(), Movie.class)) {
        findResourceString = "getMovie(" + id + ")";
      } else {
        throw new NotFoundException(
            format("InjectResource doen't know how to inject field %s of type %s in %s",
                field.getName(), field.getType().getName(), targetClazz.getName()));
      }
      if (root != null) {
        buffer.append(root);
        buffer.append(".");
      }
      buffer.append(findResourceString);
      buffer.append(";\n");
    }
    return buffer.toString();
  }

  private boolean isStringArray(CtField field, ClassPool classPool) throws NotFoundException {
    return field.getType().isArray() && isSubClass(classPool, field.getType().getComponentType(),
        String.class);
  }

  private boolean isIntArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.intType);
  }

  //extension point for new classes
  private boolean isValidClass(CtClass clazz) {
    return isView(clazz)
        || isActivity(clazz)
        || isService(clazz)
        || isBroadCastReceiver(clazz)
        || isContentProvider(clazz)
        || isApplication(clazz)
        || isFragment(clazz)
        || isSupportFragment(clazz);
  }

  private boolean isActivity(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Activity.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isService(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Service.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isContext(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Context.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isBroadCastReceiver(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, BroadcastReceiver.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isContentProvider(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, ContentProvider.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isApplication(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Application.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isFragment(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Fragment.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isSupportFragment(CtClass clazz) {
    try {
      Class<?> supportFragmentClass = Class.forName("android.support.v4.app.Fragment");
      return isSubClass(clazz.getClassPool(), clazz, supportFragmentClass);
    } catch (Exception e) {
      //can happen
      return false;
    }
  }

  private boolean isView(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, View.class);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isSubClass(ClassPool classPool, CtClass clazz, Class<?> superClass)
      throws NotFoundException {
    CtClass superclass = classPool.get(superClass.getName());
    return clazz.subclassOf(superclass);
  }
}
