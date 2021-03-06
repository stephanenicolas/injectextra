package com.github.stephanenicolas.injectextra;

import java.io.FileOutputStream;
import java.net.URL;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.SdkConfig;
import org.robolectric.annotation.Config;
import org.robolectric.bytecode.AsmInstrumentingClassLoader;
import org.robolectric.bytecode.Setup;
import org.robolectric.res.Fs;
import com.github.stephanenicolas.injectextra.InjectExtraProcessor;


/**
 * Created by administrateur on 2014-08-21.
 */
@Slf4j
public class InjectExtraTestRunner extends RobolectricTestRunner {
  private InjectExtraProcessor processor = new InjectExtraProcessor();

  public InjectExtraTestRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected ClassLoader createRobolectricClassLoader(Setup setup, SdkConfig sdkConfig) {
    URL[] urls = getJarResolver().getLocalArtifactUrls(sdkConfig.getSdkClasspathDependencies());
    return new AsmInstrumentingClassLoader(setup, urls) {
      @Override
      protected byte[] getByteCode(String className) throws ClassNotFoundException {
        if (className.startsWith("com.github.stephanenicolas.injectextra")) {
          try {
            CtClass dummyClass = ClassPool.getDefault().get(className);
            if (processor.shouldTransform(dummyClass)) {
              log.debug("Intercepting via InjectExtra " + className);
              processor.applyTransformations(dummyClass);
              byte[] bytes = dummyClass.toBytecode();
              System.out.println("Size of weaved byte code :" + bytes.length);
              FileOutputStream fileOutputStream =
                  new FileOutputStream("/tmp/" + className + ".class");
              fileOutputStream.write(bytes);
              fileOutputStream.close();
              return bytes;
            }
          } catch (Exception e) {
            throw new RuntimeException("Impossible to transform class " + className, e);
          }
        }
        return super.getByteCode(className);
      }
    };
  }
}
