package com.github.stephanenicolas.injectextra

import com.github.stephanenicolas.morpheus.AbstractMorpheusPlugin
import javassist.build.IClassTransformer
import org.gradle.api.Project

/**
 * @author SNI
 */
public class InjectExtraPlugin extends AbstractMorpheusPlugin {

  @Override
  public IClassTransformer[] getTransformers(Project project) {
    return new InjectExtraProcessor();
  }

  @Override
  protected void configure(Project project) {
    project.dependencies {
      provided 'com.github.stephanenicolas.injectextra:injectextra-annotations:1.+'
    }
  }

  @Override
  protected Class getPluginExtension() {
    InjectExtraPluginExtension
  }

  @Override
  protected String getExtension() {
    "injectextra"
  }
}
