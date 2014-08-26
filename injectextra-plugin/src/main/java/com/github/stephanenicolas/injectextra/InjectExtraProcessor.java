package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import com.github.stephanenicolas.afterburner.AfterBurner;
import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

/**
 * TODO
 *
 * @author SNI
 */
@Slf4j
public class InjectExtraProcessor implements IClassTransformer {

  private final static String GET_APPLICATION_TAG = "GET_APPLICATION_TAG";
  private static final String GET_ROOT_TAG = "GET_ROOT";

  private AfterBurner afterBurner = new AfterBurner();

  @Override
  public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
    try {
      final List<CtField> fields =
          getAllInjectedFieldsForAnnotation(candidateClass, InjectExtra.class);

      if (fields.isEmpty()) {
        return false;
      }

      boolean isValid = isValidClass(candidateClass);

      log.debug(
          format("Class %s will get transformed: %b", candidateClass.getSimpleName(), isValid));
      return isValid;
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
          getAllInjectedFieldsForAnnotation(classToTransform, InjectExtra.class);
      String injectExtraStatements = createInjectExtraStatements(fields, classToTransform);

      log.debug("Preliminary body (before insertion) :" + injectExtraStatements);
      injectStuff(classToTransform, injectExtraStatements);
    } catch (Exception e) {
      String message = format("Error while processing class %s", classToTransform.getName());
      log.debug(message, e);
      throw new JavassistBuildException(message, e);
    }
  }

  //extension point for new classes
  private void injectStuff(CtClass targetClazz, String body)
      throws CannotCompileException, AfterBurnerImpossibleException, NotFoundException,
      InjectExtraException {
    afterBurner.afterOverrideMethod(targetClazz, "onCreate", body);
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

  private String createInjectExtraStatements(List<CtField> extrasToInject, CtClass targetClazz)
      throws ClassNotFoundException, NotFoundException {
    StringBuffer buffer = new StringBuffer();
    for (CtField field : extrasToInject) {
      Object annotation = field.getAnnotation(InjectExtra.class);
      //must be accessed by introspection as I get a Proxy during tests.
      //this proxy comes from Robolectric
      Class annotionClass = annotation.getClass();

      //workaround for robolectric
      //https://github.com/robolectric/robolectric/pull/1240
      String value = null;
      boolean optional = false;
      try {
        Method method = annotionClass.getMethod("value");
        value = (String) method.invoke(annotation);
        method = annotionClass.getMethod("optional");
        optional = (boolean) method.invoke(annotation);
      } catch (Exception e) {
        log.debug("Exception thrown during parsing of InjectExtra annotation", e);
      }

      buffer.append(field.getName());
      buffer.append(" = ");

      String root = "getIntent()";
      String findExtraString = "";
      ClassPool classPool = targetClazz.getClassPool();
      if (isSubClass(classPool, field.getType(), String.class)) {
        findExtraString = "getStringExtra(" + value + ")";
      } else if (field.getType().subtypeOf(CtClass.booleanType)) {
        findExtraString = "getBooleanExtra(" + value + ", false)";
      } else if (isSubClass(classPool, field.getType(), Boolean.class)) {
        root = null;
        findExtraString = "new Boolean(getIntent().getBooleanExtra(" + value + ", false))";
      } else if (field.getType().subtypeOf(CtClass.intType)) {
        findExtraString = "getIntExtra(" + value + ", -1)";
      } else if (isSubClass(classPool, field.getType(), Integer.class)) {
        root = null;
        findExtraString = "new Integer(getIntent().getInt(" + value + ", -1))";
      } else if (isStringArray(field, classPool)) {
        findExtraString = "getStringArrayExtra(" + value + ")";
      } else if (isIntArray(field)) {
        findExtraString = "getIntArrayExtra(" + value + ")";
      } else {
        throw new NotFoundException(
            format("InjectExtra doen't know how to inject field %s of type %s in %s",
                field.getName(), field.getType().getName(), targetClazz.getName()));
      }
      if (root != null) {
        buffer.append(root);
        buffer.append(".");
      }
      buffer.append(findExtraString);
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
    return isActivity(clazz);
  }

  private boolean isActivity(CtClass clazz) {
    try {
      return isSubClass(clazz.getClassPool(), clazz, Activity.class);
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
