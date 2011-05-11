package com.smokejumperit.gradle.javacc

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.*
import org.gradle.api.plugins.*
import org.gradle.api.file.*

class JavaccCompile extends AbstractAntJavaccCompile {

	/**
	* The source set which is primarily handled by this compiler.
	*/
	SourceSet sourceSet

	final antTaskName = "javacc"

}
