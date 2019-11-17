package io.kvineet.sysconfigurator.services;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.kvineet.sysconfigurator.daos.SystemConfigDao;
import io.kvineet.sysconfigurator.models.Columns;
import io.kvineet.sysconfigurator.utils.FileUtils;

@Singleton
public class ExportQueryService {

	@Inject
	private SystemConfigDao basicDao;
	
	public ExportQueryService() {
		// Needed for Injection
	}

	public boolean save(String tableName, List<Map<String, String>> dataSet, List<Map<String, String>> removedSet,
			List<Columns> columns, String dirPath, String fileName) {

		String query = basicDao.constructInsertOrUpdateQuery(tableName, dataSet, columns);

		
		FileUtils.save(query, "");
		return true;
	}
}
