package com.smokejumperit.gradle.compiler;

import org.gradle.api.*;
import org.gradle.api.plugins.*;
import org.gradle.api.file.*;
import org.gradle.api.tasks.*;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.compile.Compile;
import com.smokejumperit.gradle.javacc.JJTreeCompile;
import com.smokejumperit.gradle.javacc.JavaccCompile;
import com.smokejumperit.gradle.compilers.*;
import com.smokejumperit.gradle.*;
import java.util.*;
import java.io.*;
import com.google.common.collect.Collections2;
import com.google.common.base.Predicate;
import com.google.common.base.Function;

public class JJTreePlugin extends GeneratorPlugin<JJTreeCompile> implements Plugin<Project> {

  /**
  * The file suffixes specific to the language of this compiler. 
  */
	@Override
  protected Collection<String> getLanguageFileSuffixes() {
		return Arrays.asList(new String[] { "jjt" });
	}

  /**
	* Returns the empty list.
  */
	@Override
  protected Collection<String> getAdditionalConsumedFileSuffixes() { return Collections.emptyList(); }

	@Override
	protected String getName() { return "jjtree"; }

	@Override
	protected Class<JJTreeCompile> getCompileTaskClass() { return JJTreeCompile.class; }

	/**
	* Returns the empty list
	*/
	@Override
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

	@Override
  public void apply(Project project) {
    project.getPlugins().apply(JavaccPlugin.class);
    super.apply(project);
  }

	@Override
	public String getCompilesToLanguage() { return "javacc"; }

}
