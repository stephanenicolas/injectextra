package com.github.stephanenicolas.injectextra;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import com.github.stephanenicolas.afterburner.AfterBurner;
import com.github.stephanenicolas.afterburner.exception.AfterBurnerImpossibleException;
import java.io.Serializable;
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
import javassist.bytecode.BadBytecode;
import javassist.bytecode.SignatureAttribute;
import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

/**
 * Injects all extras form intent inside an activity.
 *
 * @author SNI
 * @see {@link InjectExtra}
 */
@Slf4j
public class InjectExtraProcessor implements IClassTransformer {

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
    StringBuffer buffer = new StringBuffer('\n');
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
        log.debug("How did we get there ?", e);
      }

      String assignment = field.getName() + " = ";
      String fieldName = field.getName();
      String findExtraString = "";
      ClassPool classPool = targetClazz.getClassPool();
      //please note that default values when reading extras are not used.
      if (isSubClass(classPool, field.getType(), String.class)) {
        findExtraString = "getIntent().getStringExtra(\"" + value + "\")";
      } else if (isSubClass(classPool, field.getType(), Parcelable.class)) {
        findExtraString = "getIntent().getParcelableExtra(\"" + value + "\")";
      } else if (isSubClass(classPool, field.getType(), Serializable.class)) {
        findExtraString = "getIntent().getSerializableExtra(\"" + value + "\")";
      } else if (field.getType().subtypeOf(CtClass.intType)) {
        findExtraString = "getIntent().getIntExtra(\"" + value + "\", -1)";
      } else if (isSubClass(classPool, field.getType(), Integer.class)) {
        findExtraString = "new Integer(getIntent().getIntExtra(\"" + value + "\", -1))";
      } else if (isBoolArray(field)) {
        findExtraString = "getIntent().getBooleanArrayExtra(\"" + value + "\")";
      } else if (isIntArray(field)) {
        findExtraString = "getIntent().getIntArrayExtra(\"" + value + "\")";
      } else if (isByteArray(field)) {
        findExtraString = "getIntent().getByteArrayExtra(\"" + value + "\")";
      } else if (isCharArray(field)) {
        findExtraString = "getIntent().getCharArrayExtra(\"" + value + "\")";
      } else if (isFloatArray(field)) {
        findExtraString = "getIntent().getFloatArrayExtra(\"" + value + "\")";
      } else if (isDoubleArray(field)) {
        findExtraString = "getIntent().getDoubleArrayExtra(\"" + value + "\")";
      } else if (isShortArray(field)) {
        findExtraString = "getIntent().getShortArrayExtra(\"" + value + "\")";
      } else if (isCharSequenceArray(field, classPool)) {
        findExtraString = "getIntent().getCharSequenceArrayExtra(\"" + value + "\")";
      } else if (isArrayList(field, classPool)) {
        findExtraString = "getIntent().getCharSequenceArrayListExtra(\"" + value + "\")";
      } else if (isStringArray(field, classPool)) {
        findExtraString = "getIntent().getStringArrayExtra(\"" + value + "\")";
      } else if (isParcelableArray(field, classPool)) {
        findExtraString = "getIntent().getParcelableArrayExtra(\"" + value + "\")";
      } else if (field.getType().subtypeOf(CtClass.booleanType)) {
        findExtraString = "getIntent().getBooleanExtra(\"" + value + "\", false)";
      } else if (isSubClass(classPool, field.getType(), Boolean.class)) {
        findExtraString = "new Boolean(getIntent().getBooleanExtra(\"" + value + "\", false))";
      } else if (isSubClass(classPool, field.getType(), Bundle.class)) {
        findExtraString = "getIntent().getBundleExtra(\"" + value + "\")";
      } else if (field.getType().subtypeOf(CtClass.byteType)) {
        findExtraString = "getIntent().getByteExtra(\"" + value + "\", (byte)-1)";
      } else if (isSubClass(classPool, field.getType(), Byte.class)) {
        findExtraString = "new Byte(getIntent().getByteExtra(\"" + value + "\", (byte)-1))";
      } else if (field.getType().subtypeOf(CtClass.charType)) {
        findExtraString = "getIntent().getCharExtra(\"" + value + "\", '\\u0000')";
      } else if (field.getType().subtypeOf(CtClass.floatType)) {
        findExtraString = "getIntent().getFloatExtra(\"" + value + "\", -1f)";
      } else if (isSubClass(classPool, field.getType(), Float.class)) {
        findExtraString = "new Float(getIntent().getFloatExtra(\"" + value + "\", (float)-1f))";
      } else if (field.getType().subtypeOf(CtClass.shortType)) {
        findExtraString = "getIntent().getShortExtra(\"" + value + "\", (short)1)";
      } else if (isSubClass(classPool, field.getType(), Short.class)) {
        findExtraString = "new Short(getIntent().getShortExtra(\"" + value + "\", (short)1))";
      } else if (field.getType().subtypeOf(CtClass.doubleType)) {
        findExtraString = "getIntent().getDoubleExtra(\"" + value + "\", 1.0)";
      } else if (isSubClass(classPool, field.getType(), Double.class)) {
        findExtraString = "new Double(getIntent().getDoubleExtra(\"" + value + "\", 1.0))";
      } else if (isSubClass(classPool, field.getType(), Character.class)) {
        findExtraString = "new Character(getIntent().getCharExtra(\"" + value + "\", '\\u0000'))";
      } else if (isSubClass(classPool, field.getType(), CharSequence.class)) {
        findExtraString = "getIntent().getCharSequenceExtra(\"" + value + "\")";
      } else {
        throw new NotFoundException(
            format("InjectExtra doesn't know how to inject field %s of type %s in %s",
                field.getName(), field.getType().getName(), targetClazz.getName()));
      }
      buffer.append(checkOptional(assignment, value, optional, findExtraString, fieldName));
      buffer.append("\n");
      buffer.append(checkNullable(field, fieldName));
      buffer.append("\n");
    }
    return buffer.toString();
  }

  private String checkNullable(CtField field, String fieldName)
      throws NotFoundException {
    String checkNullable = "";
    if (!field.getType().isPrimitive() && !Nullable.isNullable(field)) {
      checkNullable = "if ("
          + fieldName
          + " == null) {\n  throw new RuntimeException(\"Field "
          + fieldName
          + " is null and is not @Nullable.\"); \n}\n";
    }
    return checkNullable;
  }

  private String checkOptional(String fieldAssignment, String value, boolean optional,
      String findExtraString, String fieldName) {
    findExtraString = "if (getIntent().hasExtra(\""
        + value
        + "\")) {\n  "
        + fieldAssignment
        + findExtraString
        + ";\n}";
    if (!optional) {
        findExtraString = findExtraString
        + " else {\n  throw new RuntimeException(\"Field "
        + fieldName
        + " is not optional and is not present in extras.\");\n}";
    }
    return findExtraString;
  }

  private boolean isBoolArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.booleanType);
  }

  private boolean isByteArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.byteType);
  }

  private boolean isCharArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.charType);
  }

  private boolean isFloatArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.floatType);
  }

  private boolean isDoubleArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.doubleType);
  }

  private boolean isShortArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.shortType);
  }

  private boolean isCharSequenceArray(CtField field, ClassPool classPool) throws NotFoundException {
    return field.getType().isArray() && isSubClass(classPool, field.getType().getComponentType(),
        CharSequence.class);
  }

  private boolean isArrayList(CtField field, ClassPool classPool) throws NotFoundException {
    return isSubClass(classPool, field.getType(), ArrayList.class);
  }

  private boolean isStringArray(CtField field, ClassPool classPool) throws NotFoundException {
    return field.getType().isArray() && isSubClass(classPool, field.getType().getComponentType(),
        String.class);
  }

  private boolean isParcelableArray(CtField field, ClassPool classPool) throws NotFoundException {
    return field.getType().isArray() && isSubClass(classPool, field.getType().getComponentType(),
        Parcelable.class);
  }

  private boolean isIntArray(CtField field) throws NotFoundException {
    return field.getType().isArray() && field.getType()
        .getComponentType()
        .subtypeOf(CtClass.intType);
  }

  //extension point for new classes
  private boolean isValidClass(CtClass clazz) throws NotFoundException {
    return isActivity(clazz);
  }

  private boolean isActivity(CtClass clazz) throws NotFoundException {
    return isSubClass(clazz.getClassPool(), clazz, Activity.class);
  }

  private boolean isSubClass(ClassPool classPool, CtClass clazz, Class<?> superClass)
      throws NotFoundException {
    CtClass superclass = classPool.get(superClass.getName());
    return clazz.subclassOf(superclass);
  }
}
