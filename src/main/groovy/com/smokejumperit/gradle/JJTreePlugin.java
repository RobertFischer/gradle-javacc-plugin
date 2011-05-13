package com.smokejumperit.gradle.compiler;

import org.gradle.api.*;
import org.gradle.api.plugins.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.compile.Compile;
import com.smokejumperit.gradle.javacc.JJTreeCompile;
import com.smokejumperit.gradle.javacc.JavaccCompile;
import com.smokejumperit.gradle.javacc.Utils;
import com.smokejumperit.gradle.compilers.CompilerPlugin;
import com.smokejumperit.gradle.*;
import java.util.*;
import java.io.*;
import com.google.common.collect.Collections2;
import com.google.common.base.Predicate;
import com.google.common.base.Function;

public class JJTreePlugin extends CompilerPlugin<JJTreeCompile> implements Plugin<Project> {

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
	* Returns the empty list
	*/
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

  public void apply(Project project) {
    project.apply(Collections.singletonMap("plugin", JavaccPlugin.class));
    super.apply(project);
  }

  protected void postConfig(final JJTreeCompile compileTask, final SourceSet set, final Project project) {
    File destDir = new File(compileTask.getDestinationDir().getParentFile().getParentFile(), "jjtree-gen/" + set.getName());
    compileTask.setDestinationDir(destDir);

		final Collection<JavaccCompile> javaccTasksForSet = Collections2.filter(( (Collection<JavaccCompile>)
			project.getTasks().withType(JavaccCompile.class)
		), new Predicate<JavaccCompile>() {
			public boolean apply(JavaccCompile jjCompile) {
				return set.equals(jjCompile.getSourceSet());
			}
		});
		for(JavaccCompile jjCompile : javaccTasksForSet) {
			jjCompile.dependsOn(compileTask);
		}
		Utils.passSource(compileTask, javaccTasksForSet, "jj");

		final Collection<Compile> javaTasksForSet = Collections2.transform(
			project.getTasksByName(set.getCompileJavaTaskName(), false),
			new Function<Task,Compile>() {
				public Compile apply(Task input) { return (Compile)input; }
			}
		);
		for(Compile javaCompile : javaTasksForSet) {
			javaCompile.dependsOn(compileTask);
		}
		Utils.passSource(compileTask, javaTasksForSet, "java");
  } 

}
