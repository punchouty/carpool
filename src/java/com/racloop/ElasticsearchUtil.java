package com.racloop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.plugins.PluginManager;
import org.elasticsearch.plugins.PluginManager.OutputMode;

public class ElasticsearchUtil {

	public static final String HQ_PLUGIN = "royrusso/elasticsearch-HQ";
	public static final String AWS_PLUGIN = "elasticsearch/elasticsearch-cloud-aws/2.5.1";
	public static final String KOPF_PLUGIN = "lmenezes/elasticsearch-kopf";
	
	public static void downloadPlugins(Settings settings) throws IOException {
		org.elasticsearch.env.Environment env = new org.elasticsearch.env.Environment(settings);
		Path path = env.pluginsFile().toPath();
		if (!Files.exists(path)) {
			Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-x---");
			FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
			Files.createDirectories(path, fileAttributes);
		}
		PluginManager pluginManager = new PluginManager(env, null, OutputMode.DEFAULT, TimeValue.timeValueMillis(0));
		File [] pluginsPath = pluginManager.getListInstalledPlugins();
		if (grails.util.Environment.getCurrent() == grails.util.Environment.DEVELOPMENT) {
			installPlugin(HQ_PLUGIN, pluginManager, pluginsPath);
			installPlugin(KOPF_PLUGIN, pluginManager, pluginsPath);
		}
		else {
			installPlugin(AWS_PLUGIN, pluginManager, pluginsPath);
		}
	}

	private static void installPlugin(String pluginName, PluginManager pluginManager,
			File[] pluginsPath) throws IOException {
		boolean isPluginInstalled = false;
		for (File file : pluginsPath) {
			if(file.getAbsolutePath().contains(pluginName)) {
				isPluginInstalled = true;
			}
		}
		if(!isPluginInstalled) pluginManager.downloadAndExtract(pluginName);
	}

}
