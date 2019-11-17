package io.kvineet.sysconfigurator.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import io.kvineet.sysconfigurator.models.Configuration;
import io.kvineet.sysconfigurator.utils.FileUtils;

public class RecentConfigurationService {

	public static final String configFileName = "sysconfigurator.cfg";

	public static List<Configuration> getAll() {

		Optional<Configuration[]> optionalConfigurations = FileUtils.getFromFile(configFileName, true,
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
			newList.add(0, configuration);
			newList.addAll(oldList);
			if (newList.size() > 5) {
				newList = newList.subList(0, 5);
			}

			boolean saved = FileUtils.saveToFile(newList.toArray(), configFileName);
			return saved ? newList : oldList;
		}
		return oldList;
	}
}
