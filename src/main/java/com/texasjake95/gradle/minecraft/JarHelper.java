package com.texasjake95.gradle.minecraft;

import java.util.ArrayList;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;

import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.tasks.user.SourceCopyTask;

import com.texasjake95.gradle.ProjectHelper;
import com.texasjake95.gradle.minecraft.extension.ExtensionJarData;

public class JarHelper {

	private JarHelper()
	{
	}

	public static void addJarTasks(Project project)
	{
		SourceCopyTask task = (SourceCopyTask) project.getTasks().getByName("sourceMainJava");
		task.replace("${version}", project.property("mod_version").toString() + "." + project.property("buildNumber").toString());
		JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
		ExtensionJarData jarData = ProjectHelper.getExtension(project, "jarData");
		//
		apiJar(project, jarData.getApiFrom());
		SourceCopyTask apiTask = (SourceCopyTask) project.getTasks().getByName("sourceApiJava");
		//
		devJar(project, jarData.getDevFrom());
		//
		Jar jar = (Jar) project.getTasks().getByName("sourceJar");
		jar.from(task.getOutput(), apiTask.getOutput());
		for (Object object : jarData.getSrcFrom())
			jar.from(object);
		//
		SourceSet api = javaConv.getSourceSets().getByName("api");
		JavaCompile compile = (JavaCompile) project.getTasks().getByName(api.getCompileJavaTaskName());
		jar = (Jar) project.getTasks().getByName("jar");
		jar.from(compile.getOutputs());
	}

	private static Object[] getDev(Project project)
	{
		JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
		//
		SourceCopyTask task = (SourceCopyTask) project.getTasks().getByName("sourceMainJava");
		//
		SourceSet main = javaConv.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		JavaCompile compile = (JavaCompile) project.getTasks().getByName(main.getCompileJavaTaskName());
		return new Object[] { compile.getOutputs(), task.getOutput() };
	}

	private static Object[] getApi(Project project)
	{
		JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
		SourceCopyTask apiTask = (SourceCopyTask) project.getTasks().getByName("sourceApiJava");
		//
		SourceSet api = javaConv.getSourceSets().getByName("api");
		JavaCompile apiCompile = (JavaCompile) project.getTasks().getByName(api.getCompileJavaTaskName());
		return new Object[] { apiCompile.getOutputs(), apiTask.getOutput() };
	}

	private static void devJar(Project project, ArrayList<Object> devFrom)
	{
		Jar jar = ProjectHelper.addTask(project, "devJar", Jar.class);
		jar.setClassifier("dev");
		jar.from(getApi(project), getDev(project));
		for (Object object : devFrom)
			jar.from(object);
		project.getArtifacts().add("archives", jar);
		project.getTasks().getByName("build").dependsOn(jar.getName());
	}

	private static void apiJar(Project project, ArrayList<Object> apiFrom)
	{
		configureTasks(project);
		Jar jar = ProjectHelper.addTask(project, "apiJar", Jar.class);
		jar.setClassifier("api");
		jar.from(getApi(project));
		for (Object object : apiFrom)
			jar.from(object);
		jar.dependsOn("apiClasses");
		project.getArtifacts().add("archives", jar);
		project.getTasks().getByName("build").dependsOn(jar.getName());
	}

	private static void configureTasks(Project project)
	{
		JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
		//
		Javadoc javadoc = (Javadoc) project.getTasks().getByName("javadoc");
		DelayedFile dir = new DelayedFile(project, "{BUILD_DIR}/api/sources/java");
		SourceSet api = javaConv.getSourceSets().getByName("api");
		//
		SourceCopyTask apiTask = ProjectHelper.addTask(project, "sourceApiJava", SourceCopyTask.class);
		apiTask.setSource(api.getJava());
		apiTask.setOutput(dir);
		apiTask.replace("${version}", project.property("mod_version").toString() + "." + project.property("buildNumber").toString());
		javadoc.source(apiTask.getOutput(), javadoc.getSource());
		//
	}
}
