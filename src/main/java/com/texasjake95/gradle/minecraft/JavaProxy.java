package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.bundling.Jar;

import com.google.common.base.Strings;

import net.minecraftforge.gradle.common.BaseExtension;
import net.minecraftforge.gradle.tasks.user.SourceCopyTask;
import net.minecraftforge.gradle.user.patch.UserPatchExtension;

import com.texasjake95.gradle.ProjectHelper;

public class JavaProxy {

	public static boolean setup = true;
	private static final Pattern VERSION_CHECK = Pattern.compile("([\\d._pre]+)-([\\w\\d.]+)(?:-[\\w\\d.]+)?");

	public static void apply(Project project)
	{
		project.getConfigurations().create("afterThought");
		setupMinecraftExtension(project);
		final Delete resetModFolder = ProjectHelper.addTask(project, "resetModFolder", Delete.class);
		resetModFolder.delete(project.getProjectDir().getAbsolutePath() + "/mods/");
		addRequiredDeps(project, resetModFolder);
		final SetATs setATs = ProjectHelper.addTask(project, "setATs", SetATs.class);
		project.getExtensions().create("atSetup", ExtensionATExtract.class);
		project.getExtensions().create("modSetup", ExtensionModSetup.class);
		project.getExtensions().create("skipConfiguration", ExtensionConfigurationSkip.class);
		project.getExtensions().create("depAdd", DependencyManager.class, project);
		project.afterEvaluate(new Action<Project>() {

			@Override
			public void execute(Project project)
			{
				if (setup)
				{
					DependencyManager depAdd = (DependencyManager) project.getExtensions().getByName("depAdd");
					depAdd.handleAfterThought();
					for (ATExtractData data : ((ExtensionATExtract) project.getExtensions().getByName("atSetup")).getData())
					{
						if (data.getModFile() != null)
						{
							ATExtract task = ProjectHelper.addTask(project, data.getTaskName(), ATExtract.class);
							task.setModFile(data.getModFile());
							task.setAt(data.getAt());
							task.setFileUnpacked(data.getFileUnpacked());
						}
					}
					TaskCollection<ATExtract> tasks = project.getTasks().withType(ATExtract.class);
					setATs.dependsOn(tasks.getNames());
					if (checkModFolder((ExtensionModSetup) project.getExtensions().getByName("modSetup")))
					{
						addModFolder(project, resetModFolder);
					}
					configureRunTasks(project);
					addJarTasks(project);
				}
				setup = false;
			}
		});
		project.getTasks().getByName("extractUserDev").dependsOn(setATs.getName());
	}

	private static void setupMinecraftExtension(Project project)
	{
		BaseExtension patch = (BaseExtension) project.getExtensions().getByName("minecraft");
		if (Strings.isNullOrEmpty(patch.getVersion()) || patch.getVersion().equals("null") || !VERSION_CHECK.matcher(patch.getVersion()).matches())
			patch.setVersion(project.property("minecraft_version") + "-" + MinecraftForgeVersion.getForgeVersion(project));
		if (new File("../run").exists())
			patch.setAssetDir("../run/assets");
	}

	protected static boolean skipDependency(Project project, String name)
	{
		return ((ExtensionConfigurationSkip) project.getExtensions().getByName("skipConfiguration")).containsDepend(name);
	}

	protected static boolean skipConfiguration(Project project, String name)
	{
		return ((ExtensionConfigurationSkip) project.getExtensions().getByName("skipConfiguration")).containsConfig(name);
	}

	protected static boolean isValid(File file)
	{
		if (file.getAbsolutePath().endsWith("jar"))
		{
			if (file.getName().contains("source") || file.getName().contains("javadoc") || file.getName().contains("src"))
				return false;
			return true;
		}
		return false;
	}

	protected static File getFile(Project project, Dependency dep)
	{
		String fileName = dep.getName() + "-" + dep.getVersion();
		for (File file : project.getConfigurations().getByName("default").getFiles())
		{
			if (file.getAbsolutePath().contains(fileName))
			{
				return file;
			}
		}
		return null;
	}

	protected static boolean checkForATs(JarFile jarFile)
	{
		try
		{
			Manifest maniFest = jarFile.getManifest();
			if (maniFest != null)
				if (maniFest.getAttributes("FMLAT") != null)
					return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private static void addRequiredDeps(Project project, Task resetModFolder)
	{
		Task task = project.getTasks().getByName("setupDecompWorkspace");
		task.dependsOn(resetModFolder.getName());
		task = project.getTasks().getByName("setupDevWorkspace");
		task.dependsOn(resetModFolder.getName());
		task = project.getTasks().getByName("runClient");
		task.dependsOn(resetModFolder.getName());
		task = project.getTasks().getByName("debugClient");
		task.dependsOn(resetModFolder.getName());
		task = project.getTasks().getByName("runServer");
		task.dependsOn(resetModFolder.getName());
		task = project.getTasks().getByName("debugServer");
		task.dependsOn(resetModFolder.getName());
	}

	private static void addJarTasks(Project project)
	{
		SourceCopyTask task = (SourceCopyTask) project.getTasks().getByName("sourceMainJava");
		task.replace("${version}", project.property("mod_version").toString() + "." + project.property("buildNumber").toString());
		JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
		//
		Jar jar = ProjectHelper.addTask(project, "devJar", Jar.class);
		jar.setClassifier("dev");
		jar.from(javaConv.getSourceSets().getByName("main").getOutput());
		jar.from(task.getOutput());
		project.getArtifacts().add("archives", jar);
		project.getTasks().getByName("build").dependsOn(jar.getName());
		//
		jar = ProjectHelper.addTask(project, "apiJar", Jar.class);
		jar.setClassifier("api");
		jar.from(javaConv.getSourceSets().getByName("main").getOutput()).include("com/texasjake95/*/api/**");
		jar.from(task.getOutput()).include("com/texasjake95/*/api/**");
		project.getArtifacts().add("archives", jar);
		project.getTasks().getByName("build").dependsOn(jar.getName());
		//
		jar = (Jar) project.getTasks().getByName("sourceJar");
		jar.from(task.getOutput());
	}

	private static void configureRunTasks(Project project)
	{
		JavaExec task = (JavaExec) project.getTasks().getByName("runClient");
		setClient(task, project);
		task = (JavaExec) project.getTasks().getByName("debugClient");
		setClient(task, project);
		task = (JavaExec) project.getTasks().getByName("runServer");
		setServer(task, project);
		task = (JavaExec) project.getTasks().getByName("debugServer");
		setServer(task, project);
	}

	private static void setServer(JavaExec task, Project project)
	{
		setJavaExec(task, project, false);
	}

	private static void setJavaExec(JavaExec task, Project project, boolean isClient)
	{
		if (isClient)
		{
			if (project.hasProperty("mcUsername"))
				task.args("--username=" + project.property("mcUsername"));
			if (project.hasProperty("mcPassword"))
				task.args("--password=" + project.property("mcPassword"));
		}
		task.args("--gameDir=" + getGameDir(project));
		task.args("--assetsDir=" + getAssetsDir(project));
	}

	private static void setClient(JavaExec task, Project project)
	{
		setJavaExec(task, project, true);
	}

	private static String getGameDir(Project project)
	{
		if (project.hasProperty("mcGameDir"))
			return (String) project.property("mcGameDir");
		return project.getProjectDir().getAbsolutePath();
	}

	private static String getAssetsDir(Project project)
	{
		if (project.hasProperty("mcAssetsDir"))
			return (String) project.property("mcAssetsDir");
		return ((UserPatchExtension) project.getExtensions().getByName("minecraft")).getAssetDir();
	}

	private static void addModFolder(Project project, Delete resetModFolder)
	{
		Copy modFolder;
		if ((modFolder = (Copy) project.getTasks().findByPath("setupModFolder")) == null)
			modFolder = ProjectHelper.addTask(project, "setupModFolder", Copy.class);
		for (ModFolderData mod : ((ExtensionModSetup) project.getExtensions().getByName("modSetup")).getData())
			modFolder.from(mod.getMod());
		modFolder.into(project.getProjectDir().getAbsolutePath() + "/mods/");
		modFolder.dependsOn(resetModFolder.getName());
		addRequiredDeps(project, modFolder);
	}

	private static boolean checkModFolder(ExtensionModSetup modSetup)
	{
		return !modSetup.getData().isEmpty();
	}
}
