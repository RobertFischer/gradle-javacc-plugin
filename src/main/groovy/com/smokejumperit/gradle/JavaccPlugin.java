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
		
		for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
			Compile javaCompile = (Compile)t;
			project.getLogger().debug("Setting " + javaCompile + " to depend on " + compileTask);
			javaCompile.dependsOn(compileTask);
		}

		compileTask.doLast(new Action<Task>() {
			public void execute(Task task) {
				final ConfigurableFileTree javaInDestDir = project.fileTree(compileTask.getDestinationDir());
				javaInDestDir.setIncludes(Collections.singletonList("**/*.java"));

				FileCollection genSource = javaInDestDir;
				for(File file : compileTask.getSource().getFiles()) {
					final File dir;
					if(file.isDirectory()) {
						dir = file;
					} else {
						dir = file.getParentFile();
					}
					project.getLogger().debug("Adding in the Javacc source directory: " + dir);
					final ConfigurableFileTree javaFiles = project.fileTree(dir.getAbsoluteFile());
					javaFiles.setIncludes(Collections.singletonList("**/*.java"));
					genSource = genSource.plus(javaFiles);
				}

				for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
					Compile javaCompile = (Compile)t;
					project.getLogger().debug("Adding sources to " + javaCompile + ": " + genSource.getAsPath());
					javaCompile.source(javaCompile.getSource().plus(genSource));
				}
			}
		});
	}


}
