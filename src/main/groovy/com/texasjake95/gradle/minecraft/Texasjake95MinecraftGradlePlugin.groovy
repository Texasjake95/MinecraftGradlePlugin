package com.texasjake95.gradle.minecraft

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.sun.jmx.snmp.tasks.Task

class Texasjake95MinecraftGradlePlugin implements Plugin<Project>
{
	
	@Override
	public void apply(Project project)
	{
		JavaProxy.apply(project)
	}
}
