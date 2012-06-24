package com.smokejumperit.gradle.javacc

import com.smokejumperit.gradle.compilers.AbstractGenerator
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.*
import org.gradle.api.plugins.*
import org.gradle.api.file.*

abstract class AbstractAntJavaccCompile extends AbstractGenerator {

	/**
	* The name of the Ant task to execute
	*/
	abstract getAntTaskName();

	@TaskAction
	protected void compile() {
		String JAVACC_HOME = System.getenv('JAVACC_HOME')
		if(!JAVACC_HOME) {
			throw new StopExecutionException("Need the JAVACC_HOME environment variable to be specified")
		}

		new ArrayList(source.files).each { File file ->
			project.ant."$antTaskName"(
				javaccHome:JAVACC_HOME,
				target:file,
				outputDirectory:destinationDir
			)
		}

	}

}
