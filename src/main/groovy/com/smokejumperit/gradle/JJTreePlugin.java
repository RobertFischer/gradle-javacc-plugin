package com.smokejumperit.gradle.compiler;

import org.gradle.api.*;
import org.gradle.api.plugins.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.compile.Compile;
import com.smokejumperit.gradle.javacc.JJTreeCompile;
import com.smokejumperit.gradle.javacc.JavaccCompile;
import com.smokejumperit.gradle.compilers.CompilerPlugin;
import com.smokejumperit.gradle.*;
import java.util.*;
import java.io.*;

public class JJTreePlugin extends CompilerPlugin<JJTreeCompile, SourceTask> implements Plugin<Project> {

  /**
  * The file suffixes specific to the language of this compiler. 
  */
  protected Collection<String> getLanguageFileSuffixes() {
		return Arrays.asList(new String[] { "jjt" });
	}

  /**
	* Returns the empty list.
  */
  protected Collection<String> getAdditionalConsumedFileSuffixes() { return Collections.emptyList(); }

	protected String getName() { return "jjtree"; }

	protected Class<JJTreeCompile> getCompileTaskClass() { return JJTreeCompile.class; }

	/**
	* Returns <code>null</code>: no documentation!
	*/
	protected Class<SourceTask> getDocTaskClass() { return null; }

	/**
	* Returns the empty list
	*/
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

  public void apply(Project project) {
    project.apply(Collections.singletonMap("plugin", JavaccPlugin.class));
    super.apply(project);
  }

  protected void postConfig(JJTreeCompile compileTask, SourceSet set, Project project) {
    File destDir = new File(compileTask.getDestinationDir().getParent(), "jjtree-gen");
    compileTask.setDestinationDir(destDir);

		final Spec<File> jjSpec = new Spec<File> () {
      public boolean isSatisfiedBy(File it) {
        return it.getName().toLowerCase().endsWith(".jj");
      }
    };

		final Spec<File> javaSpec = new Spec<File> () {
      public boolean isSatisfiedBy(File it) {
        return it.getName().toLowerCase().endsWith(".java");
      }
    };

    FileCollection genJjSource = project.files(destDir).filter(jjSpec);
    FileCollection genJavaSource = project.files(destDir).filter(javaSpec);
    FileCollection jjSource = project.files(compileTask.getSource()).filter(jjSpec);
    FileCollection javaSource = project.files(compileTask.getSource()).filter(javaSpec);

    for(Task t : project.getTasks().withType(JavaccCompile.class)) {
      JavaccCompile jjCompile = (JavaccCompile)t;
			if(set.equals(jjCompile.getSourceSet())) {
				jjCompile.dependsOn(compileTask);
				jjCompile.source(genJjSource);
				jjCompile.source(jjSource);
			}
    } 

    for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
      Compile javaCompile = (Compile)t;
			javaCompile.dependsOn(compileTask);
      javaCompile.source(javaSource);
      javaCompile.source(genJavaSource);
    }
  } 

}
