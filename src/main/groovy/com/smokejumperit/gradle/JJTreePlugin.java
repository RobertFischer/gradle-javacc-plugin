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

		for(Task t : project.getTasks().withType(JavaccCompile.class)) {
			JavaccCompile jjCompile = (JavaccCompile)t;
			if(set.equals(jjCompile.getSourceSet())) {
				jjCompile.dependsOn(compileTask);
			}
		}
		for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
			Compile javaCompile = (Compile)t;
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
          project.getLogger().debug("Adding in a JJTree source directory: " + dir);
          final ConfigurableFileTree javaFiles = project.fileTree(dir.getAbsoluteFile());
          javaFiles.setIncludes(Collections.singletonList("**/*.java"));
          genSource = genSource.plus(javaFiles);
        }

				for(Task t : project.getTasksByName(set.getCompileJavaTaskName(), false)) {
					Compile javaCompile = (Compile)t;
					javaCompile.source(javaCompile.getSource().plus(genSource));
				}
			}
		});

		compileTask.doLast(new Action<Task>() {
			public void execute(Task task) {
				final ConfigurableFileTree jjInDestDir = project.fileTree(compileTask.getDestinationDir());
        jjInDestDir.setIncludes(Collections.singletonList("**/*.jj"));

        FileCollection genSource = jjInDestDir;
        for(File file : compileTask.getSource().getFiles()) {
          final File dir;
          if(file.isDirectory()) {
            dir = file;
          } else {
            dir = file.getParentFile();
          }
          project.getLogger().debug("Adding in a JJTree source directory: " + dir);
          final ConfigurableFileTree jjFiles = project.fileTree(dir.getAbsoluteFile());
          jjFiles.setIncludes(Collections.singletonList("**/*.jj"));
          genSource = genSource.plus(jjFiles);
        }

				for(Task t : project.getTasks().withType(JavaccCompile.class)) {
					JavaccCompile jjCompile = (JavaccCompile)t;
					if(set.equals(jjCompile.getSourceSet())) {
						jjCompile.source(jjCompile.getSource().plus(genSource));
					}
				} 
			}
		});

  } 

}
