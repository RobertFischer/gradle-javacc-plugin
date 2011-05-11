package com.smokejumperit.gradle.javacc

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.*
import org.gradle.api.plugins.*
import org.gradle.api.file.*

abstract class AbstractAntJavaccCompile extends AbstractCompile {

	/**
	* The name of the Ant task to execute
	*/
	abstract getAntTaskName();

	protected void compile() {
		String JAVACC_HOME = project.env['JAVACC_HOME']
		if(!JAVACC_HOME) {
			throw new StopExecutionException("Need the JAVACC_HOME environment variable to be specified")
		}

		doLast {
			new ArrayList(source.files).each { File file ->
				project.ant."$antTaskName"(
					javaccHome:JAVACC_HOME,
					target:file,
					outputDirectory:destinationDir
				)
			}
		}

	}

}
