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
import com.smokejumperit.gradle.javacc.Utils;
import com.smokejumperit.gradle.compilers.CompilerPlugin;
import java.util.*;
import java.io.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class JavaccPlugin extends CompilerPlugin<JavaccCompile> implements Plugin<Project> {

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
	* Provides the necessary compile configuration names for Mirah.
	*/
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

	public void apply(Project project) {
		project.apply(Collections.singletonMap("plugin", EnvPlugin.class));
		super.apply(project);
	}

	protected void postConfig(final JavaccCompile compileTask, final SourceSet set, final Project project) {
		compileTask.setSourceSet(set);

		File destDir = new File(project.getBuildDir(), "javacc-gen/" + set.getName()).getAbsoluteFile();
		compileTask.setDestinationDir(destDir);

		
		final Collection<Compile> javaTasks = Collections2.transform(project.getTasksByName(set.getCompileJavaTaskName(), false),
			new Function<Task,Compile>() {
				public Compile apply(Task input) {
					return (Compile)input;
				}
			}
		);
		
		for(Compile javaCompile : javaTasks) {
			project.getLogger().debug("Setting " + javaCompile + " to depend on " + compileTask);
			javaCompile.dependsOn(compileTask);
		}

		Utils.passSource(compileTask, javaTasks, "java");
	}


}
