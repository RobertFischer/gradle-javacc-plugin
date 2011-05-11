package com.smokejumperit.gradle.compiler;

import static java.util.Arrays.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.plugins.*;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.compile.*;
import org.gradle.api.specs.*;
import com.smokejumperit.gradle.EnvPlugin;
import com.smokejumperit.gradle.javacc.JavaccCompile;
import com.smokejumperit.gradle.compilers.CompilerPlugin;
import java.util.*;
import java.io.*;

public class JavaccPlugin extends CompilerPlugin<JavaccCompile, SourceTask> implements Plugin<Project> {

  /**
  * The file suffixes specific to the language of this compiler. 
  */
  protected Collection<String> getLanguageFileSuffixes() {
		return asList(new String[] { "jj" });
	}

	/*
	* Returns the empty list.
	*/
  protected Collection<String> getAdditionalConsumedFileSuffixes() { 
		return Collections.emptyList();
	}

	protected String getName() { return "javacc"; }

	protected Class<JavaccCompile> getCompileTaskClass() { return JavaccCompile.class; }

	/**
	* Returns <code>null</code>: no documentation!
	*/
	protected Class<SourceTask> getDocTaskClass() { return null; }

	/**
	* Provides the necessary compile configuration names for Mirah.
	*/
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

	public void apply(Project project) {
		project.apply(Collections.singletonMap("plugin", EnvPlugin.class));
		super.apply(project);
	}

	protected void postConfig(JavaccCompile compileTask, SourceSet set, Project project) {
		compileTask.setSourceSet(set);

		final Spec<File> javaSpec = new Spec<File> () {
			public boolean isSatisfiedBy(File it) {
				return it.getName().toLowerCase().endsWith(".java");
			}
		};

		File destDir = new File(compileTask.getDestinationDir().getParent(), "javacc-gen");
		compileTask.setDestinationDir(destDir);

		FileCollection genSource = project.files(destDir).filter(javaSpec);
		FileCollection javaSource = project.files(compileTask.getSource()).filter(javaSpec);

		for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
			Compile javaCompile = (Compile)t;
			project.getLogger().info("Setting " + javaCompile + " to depend on " + compileTask);
			javaCompile.dependsOn(compileTask);
			javaCompile.source(javaSource);
			javaCompile.source(genSource);
		}
	}


}
