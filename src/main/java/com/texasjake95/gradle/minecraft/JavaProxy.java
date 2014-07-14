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
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.TaskCollection;

import com.google.common.base.Strings;

import net.minecraftforge.gradle.common.BaseExtension;

import com.texasjake95.gradle.ProjectHelper;
import com.texasjake95.gradle.minecraft.extension.DependencyManager;
import com.texasjake95.gradle.minecraft.extension.ExtensionATExtract;
import com.texasjake95.gradle.minecraft.extension.ExtensionConfigurationSkip;
import com.texasjake95.gradle.minecraft.extension.ExtensionJarData;
import com.texasjake95.gradle.minecraft.extension.ExtensionModSetup;
import com.texasjake95.gradle.minecraft.extension.data.ATExtract;
import com.texasjake95.gradle.minecraft.extension.data.ATExtractData;
import com.texasjake95.gradle.minecraft.extension.data.ModFolderData;
import com.texasjake95.gradle.minecraft.version.Version;

public class JavaProxy {

	public static boolean setup = true;
	private static final Pattern VERSION_CHECK = Pattern.compile("([\\d._pre]+)-([\\w\\d.]+)(?:-[\\w\\d.]+)?");

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

	public static void apply(Project project)
	{
		project.getConfigurations().create("afterThought");
		project.getConfigurations().create("sources");
		setupMinecraftExtension(project);
		final Delete resetModFolder = ProjectHelper.addTask(project, "resetModFolder", Delete.class);
		resetModFolder.delete(project.getProjectDir().getAbsolutePath() + "/mods/");
		addRequiredDeps(project, resetModFolder);
		final SetATs setATs = ProjectHelper.addTask(project, "setATs", SetATs.class);
		project.getExtensions().create("atSetup", ExtensionATExtract.class);
		project.getExtensions().create("modSetup", ExtensionModSetup.class);
		project.getExtensions().create("skipConfiguration", ExtensionConfigurationSkip.class);
		project.getExtensions().create("depAdd", DependencyManager.class, project);
		project.getExtensions().create("jarData", ExtensionJarData.class);
		project.afterEvaluate(new Action<Project>() {

			@Override
			public void execute(Project project)
			{
				if (setup)
				{
					DependencyManager depAdd = (DependencyManager) project.getExtensions().getByName("depAdd");
					depAdd.handleAfterThought();
					for (ATExtractData data : ((ExtensionATExtract) project.getExtensions().getByName("atSetup")).getData())
						if (data.getModFile() != null)
						{
							ATExtract task = ProjectHelper.addTask(project, data.getTaskName(), ATExtract.class);
							task.setModFile(data.getModFile());
							task.setAt(data.getAt());
							task.setFileUnpacked(data.getFileUnpacked());
						}
					TaskCollection<ATExtract> tasks = project.getTasks().withType(ATExtract.class);
					setATs.dependsOn(tasks.getNames());
					if (checkModFolder((ExtensionModSetup) project.getExtensions().getByName("modSetup")))
						addModFolder(project, resetModFolder);
					JarHelper.addJarTasks(project);
				}
				setup = false;
			}
		});
		project.getTasks().getByName("extractUserDev").dependsOn(setATs.getName());
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

	private static boolean checkModFolder(ExtensionModSetup modSetup)
	{
		return !modSetup.getData().isEmpty();
	}

	protected static File getFile(Project project, Dependency dep)
	{
		String fileName = dep.getName() + "-" + dep.getVersion();
		for (File file : project.getConfigurations().getByName("default").getFiles())
			if (file.getAbsolutePath().contains(fileName))
				return file;
		return null;
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

	private static void setupMinecraftExtension(Project project)
	{
		BaseExtension patch = (BaseExtension) project.getExtensions().getByName("minecraft");
		if (Strings.isNullOrEmpty(patch.getVersion()) || patch.getVersion().equals("null") || !VERSION_CHECK.matcher(patch.getVersion()).matches())
			patch.setVersion(project.property("minecraft_version") + "-" + Version.getForgeVersion(project));
		if (new File("../run").exists())
			patch.setRunDir("../run/assets");
	}

	protected static boolean skipConfiguration(Project project, String name)
	{
		return ((ExtensionConfigurationSkip) project.getExtensions().getByName("skipConfiguration")).containsConfig(name);
	}

	protected static boolean skipDependency(Project project, String name)
	{
		return ((ExtensionConfigurationSkip) project.getExtensions().getByName("skipConfiguration")).containsDepend(name);
	}
}
