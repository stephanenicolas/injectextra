package com.github.stephanenicolas.injectresource

import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin
import javassist.build.IClassTransformer
import org.gradle.api.Project

/**
 * @author SNI
 */
public class InjectResourcePlugin extends AbstractMorpheusPlugin {

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new InjectResourceProcessor();
  }

  @Override
  protected void configure(Project project) {
    project.dependencies {
      provided 'com.github.stephanenicolas.injectresource:injectresource-annotations:1.0.0-SNAPSHOT'
    }
  }

  @Override
  protected Class getPluginExtension() {
    InjectResourcePluginExtension
  }

  @Override
  protected String getExtension() {
    "injectresource"
  }
}
