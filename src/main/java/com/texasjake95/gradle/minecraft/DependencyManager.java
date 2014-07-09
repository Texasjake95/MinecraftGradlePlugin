package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.util.ArrayList;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import com.google.common.collect.Lists;

import com.texasjake95.gradle.ExtensionEclipseSetup;
import com.texasjake95.gradle.ProjectHelper;

public class DependencyManager {

	private Project project;
	private ArrayList<AfterThought> afterThoughts = Lists.newArrayList();

	public DependencyManager(Project project)
	{
		this.project = project;
	}

	public void addIronChests()
	{
		String version = MinecraftForgeVersion.getVersion(project, "cpw.mods", "ironchest", "ironchest_version");
		version = project.property("minecraft_version") + "-" + version;
		ProjectHelper.addDependency(project, getDepString("cpw.mods", "ironchest", version, "deobf"));
		ProjectHelper.addDependency(project, getDepString("cpw.mods", "ironchest", version, "src"));
		addAfterThoughtDep(project, getDepString("cpw.mods", "ironchest", version, "deobf"));
		addAfterThoughtDep(project, getDepString("cpw.mods", "ironchest", version, "src"));
		afterThoughts.add(new AfterThought("ironchest", version, "deobf", "src"));
	}

	public void addNEI()
	{
		String version = project.property("minecraft_version") + "-" + CCVersion.getVersion(project, "CodeChickenCore", "ccc_version");
		if (!checkForDep(project, "codechicken", "CodeChickenCore", version))
			this.addCCC();
		addChickenBonesModWithAT(project, this, "NotEnoughItems", "nei_version", "nei_at.cfg", "NEI");
	}

	public void addCCC()
	{
		String version = MinecraftForgeVersion.getVersion(project, "CodeChickenLib", "codechicken", "ccl_version");
		version = project.property("minecraft_version") + "-" + version;
		if (!checkForDep(project, "codechicken", "CodeChickenLib", version))
			this.addCCL();
		addChickenBonesMod(project, this, "CodeChickenCore", "ccc_version");
	}

	public void addCCL()
	{
		String version = MinecraftForgeVersion.getVersion(project, "CodeChickenLib", "codechicken", "ccl_version");
		version = project.property("minecraft_version") + "-" + version;
		ProjectHelper.addDependency(project, getDepString("codechicken", "CodeChickenLib", version, "dev"));
		ProjectHelper.addDependency(project, getDepString("codechicken", "CodeChickenLib", version, "src"));
		addAfterThoughtDep(project, getDepString("codechicken", "CodeChickenLib", version, "dev"));
		addAfterThoughtDep(project, getDepString("codechicken", "CodeChickenLib", version, "src"));
		afterThoughts.add(new AfterThought("CodeChickenLib", version, "dev", "src"));
	}

	public void handleAfterThought()
	{
		ExtensionModSetup modSetup = (ExtensionModSetup) project.getExtensions().getByName("modSetup");
		ExtensionEclipseSetup eclipseSetup = (ExtensionEclipseSetup) project.getExtensions().getByName("eclipseSetup");
		ExtensionATExtract atSetup = (ExtensionATExtract) project.getExtensions().getByName("atSetup");
		for (AfterThought afterThought : this.afterThoughts)
		{
			File file = afterThought.getFile(project);
			if (file != null)
			{
				modSetup.addMod(file);
				eclipseSetup.addEclipseSetup(afterThought.getName() + "-" + afterThought.getVersion(), afterThought.getClassifer(), afterThought.getSoruceClassifer());
				if (afterThought.hasAT())
					atSetup.addAT(file, afterThought.getAtName(), new File(project.getBuildDir().getAbsolutePath() + "/unpacked/" + afterThought.getUnPackDir() + "/"));
			}
		}
	}

	private static void addChickenBonesMod(Project project, DependencyManager manager, String mod, String versionProp)
	{
		String version = project.property("minecraft_version") + "-" + CCVersion.getVersion(project, mod, versionProp);
		ProjectHelper.addDependency(project, getDepString("codechicken", mod, version, "dev"));
		ProjectHelper.addDependency(project, getDepString("codechicken", mod, version, "src"));
		addAfterThoughtDep(project, getDepString("codechicken", mod, version, "dev"));
		addAfterThoughtDep(project, getDepString("codechicken", mod, version, "src"));
		manager.afterThoughts.add(new AfterThought(mod, version, "dev", "src"));
	}

	private static void addAfterThoughtDep(Project project, String dep)
	{
		project.getDependencies().add("afterThought", dep);
	}

	private static void addChickenBonesModWithAT(Project project, DependencyManager manager, String mod, String versionProp, String atName, String unpackedDir)
	{
		String version = project.property("minecraft_version") + "-" + CCVersion.getVersion(project, mod, versionProp);
		ProjectHelper.addDependency(project, getDepString("codechicken", mod, version, "dev"));
		ProjectHelper.addDependency(project, getDepString("codechicken", mod, version, "src"));
		addAfterThoughtDep(project, getDepString("codechicken", mod, version, "dev"));
		addAfterThoughtDep(project, getDepString("codechicken", mod, version, "src"));
		manager.afterThoughts.add(new AfterThought(mod, version, "dev", "src", "nei_at.cfg", "NEI"));
	}

	public static File getFile(Project project, String depName, String version, String classifer)
	{
		String depFile = getFileName(depName, version, classifer);
		if (project.getConfigurations().getNames().contains("afterThought"))
			for (File file : project.getConfigurations().getByName("afterThought").resolve())
			{
				String fileName = file.getName();
				if (fileName.contains("sources") || fileName.contains("javadoc"))
					continue;
				if (fileName.contains(depFile))
				{
					return file;
				}
			}
		return null;
	}

	private static boolean checkForDep(Project project, String group, String artifact, String version)
	{
		if (project.getConfigurations().getNames().contains("afterThought"))
			for (Dependency dep : project.getConfigurations().getByName("afterThought").getAllDependencies())
			{
				if (dep != null)
					if (dep.getGroup() != null && dep.getName() != null && dep.getVersion() != null)
						if (dep.getGroup().equals(group) && dep.getName().equals(artifact) && dep.getVersion().equals(version))
							return true;
			}
		return false;
	}

	private static String getDepString(String group, String artifact, String version, String classifer)
	{
		String format = classifer == null ? "%s:%s:%s" : "%s:%s:%s:%s";
		Object[] array = classifer == null ? new Object[] { group, artifact, version } : new Object[] { group, artifact, version, classifer };
		return String.format(format, array);
	}

	private static String getFileName(String artifact, String version, String classifer)
	{
		String format = classifer == null ? "%s-%s" : "%s-%s-%s";
		Object[] array = classifer == null ? new Object[] { artifact, version } : new Object[] { artifact, version, classifer };
		return String.format(format, array);
	}
}
