package io.kvineet.sysconfigurator.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Singleton;

import io.kvineet.sysconfigurator.models.Configuration;
import io.kvineet.sysconfigurator.utils.FileUtils;

@Singleton
public class RecentConfigurationService {

	public static final String CONFIG_FILE_NAME = "sysconfigurator.cfg";

	public RecentConfigurationService() {
		// Needed for Injection
	}
	
	public static List<Configuration> getAll() {

		Optional<Configuration[]> optionalConfigurations = FileUtils.getFromFile(CONFIG_FILE_NAME, true,
				Configuration[].class);
		if (optionalConfigurations.isPresent()) {
			return Arrays.asList(optionalConfigurations.get());
		}

		return new LinkedList<>();
	}

	public static List<Configuration> save(Configuration configuration, List<Configuration> oldList) {

		List<Configuration> newList = new ArrayList<>();

		Optional<Configuration> optionalCongiguration = oldList.parallelStream()
				.filter(config -> config.getDbUrl().equals(configuration.getDbUrl())
						&& config.getTableName().equals(configuration.getTableName()))
				.findFirst();
		if (optionalCongiguration.isPresent()) {
			Configuration config = optionalCongiguration.get();
			newList.add(config);
			Iterator<Configuration> iterator = oldList.iterator();
			while (iterator.hasNext()) {
				Configuration oldConfig = iterator.next();
				if (!oldConfig.equals(config))
					newList.add(oldConfig);
			}
		} else {			
			newList.add(0, configuration);
			newList.addAll(oldList);
		}
		if (newList.size() > 5) {
			newList = newList.subList(0, 5);
		}
		boolean saved = false;
		try {
			saved = FileUtils.save(newList.toArray(), CONFIG_FILE_NAME);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return saved ? newList : oldList;
	}
}
