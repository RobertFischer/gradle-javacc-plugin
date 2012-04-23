package com.smokejumperit.gradle.compiler;

import static java.util.Arrays.*;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.plugins.*;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.compile.*;
import org.gradle.api.specs.*;
import com.smokejumperit.gradle.javacc.JavaccCompile;
import com.smokejumperit.gradle.compilers.*;
import java.util.*;
import java.io.*;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class JavaccPlugin extends GeneratorPlugin<JavaccCompile> implements Plugin<Project> {

  /**
  * The file suffixes specific to the language of this compiler. 
  */
	@Override
  protected Collection<String> getLanguageFileSuffixes() {
		return asList(new String[] { "jj" });
	}

	/*
	* Returns the empty list.
	*/
	@Override
  protected Collection<String> getAdditionalConsumedFileSuffixes() { 
		return Collections.emptyList();
	}

	@Override
	protected String getName() { return "javacc"; }

	@Override
	protected Class<JavaccCompile> getCompileTaskClass() { return JavaccCompile.class; }

	/**
	* Provides the necessary compile configuration names for JavaCC.
	*/
	@Override
	protected Collection<String> getCompileConfigurationNames() { return Collections.emptyList(); }

	@Override
	public String getCompilesToLanguage() { return "java"; }

}
