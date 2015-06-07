package com.racloop;

import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.plugins.PluginManager;
import org.elasticsearch.plugins.PluginManager.OutputMode;

public class ElasticsearchUtil {

	private static final String HQ_PLUGIN = "royrusso/elasticsearch-HQ";
	private static final String AWS_PLUGIN = "elasticsearch/elasticsearch-cloud-aws/2.5.1";
	private static final String KOPF_PLUGIN = "lmenezes/elasticsearch-kopf";
	
	public static void init() throws IOException {
		Tuple<Settings, Environment> initialSettings = InternalSettingsPreparer.prepareSettings(EMPTY_SETTINGS, true);
		Path path = initialSettings.v2().pluginsFile().toPath();
		if (!Files.exists(path)) {
			Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr-x---");
		    FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
			Files.createDirectories(initialSettings.v2().pluginsFile().toPath(), fileAttributes);
			PluginManager pluginManager = new PluginManager(initialSettings.v2(), null, OutputMode.DEFAULT, TimeValue.timeValueMillis(0));
	        File [] pluginsPath = pluginManager.getListInstalledPlugins();
	        //installPlugin(HQ_PLUGIN, pluginManager, pluginsPath);
	        //installPlugin(KOPF_PLUGIN, pluginManager, pluginsPath);
	        if (grails.util.Environment.getCurrent() == grails.util.Environment.PRODUCTION) {
	        	installPlugin(AWS_PLUGIN, pluginManager, pluginsPath);
	        }
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
