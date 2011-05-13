package com.smokejumperit.gradle.javacc;

import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.plugins.*;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.compile.*;
import org.gradle.api.specs.*;
import org.gradle.api.execution.*;
import java.util.*;
import java.io.*;

public class Utils {

	public static void passSource(final AbstractCompile fromTask, final Collection<? extends SourceTask> toTasks, final String suffix) {
		final TaskExecutionGraph taskGraph = fromTask.getProject().getGradle().getTaskGraph();
		
		taskGraph.addTaskExecutionGraphListener(new TaskExecutionGraphListener() {
			public void graphPopulated(final TaskExecutionGraph ignored) {
				fromTask.doLast(new Action<Task>() {
					public void execute(Task ignored) {
						doPassSource(fromTask, toTasks, suffix);
					}
				});
				doPassSource(fromTask, toTasks, suffix);
			}
		});
	}

	private static void doPassSource(final AbstractCompile compileTask, final Collection<? extends SourceTask> toTasks, final String named) {
		final Project project = compileTask.getProject();
		final ConfigurableFileTree javaInDestDir = project.fileTree(compileTask.getDestinationDir());
		javaInDestDir.setIncludes(Collections.singletonList("**/*." + named ));

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
			javaFiles.setIncludes(Collections.singletonList("**/*." + named));
			genSource = genSource.plus(javaFiles);
		}

		for(SourceTask t : toTasks) {
			project.getLogger().debug("Adding sources to " + t + ": " + genSource.getAsPath());
			t.source(t.getSource().plus(genSource));
		}
	}

}
