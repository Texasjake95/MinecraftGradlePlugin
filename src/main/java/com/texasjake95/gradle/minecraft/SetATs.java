package com.texasjake95.gradle.minecraft;

import java.io.File;
import java.util.ArrayList;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import net.minecraftforge.gradle.user.patch.UserPatchExtension;

import com.texasjake95.commons.file.FileHelper;

public class SetATs extends DefaultTask {
	
	private final File atFolder = new File(this.getProject().getBuildDir(), "/ats/");
	
	@TaskAction
	public void doTask()
	{
		ArrayList<String> names = FileHelper.gatherFileNamesInDir(atFolder.getAbsolutePath(), true);
		UserPatchExtension patch = (UserPatchExtension) this.getProject().getExtensions().getByName("minecraft");
		for (String name : names)
		{
			String fullName = this.atFolder.getAbsolutePath() + name;
			patch.accessT(fullName);
		}
	}
}
