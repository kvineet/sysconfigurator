package io.kvineet.sysconfigurator.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.kvineet.sysconfigurator.models.Columns;


public class AppWindowUtil {

	public static List<Columns> constructCols() {
		List<Columns> cols = new ArrayList<>();
		cols.add(new Columns("tenant", 0, true, false));
		cols.add(new Columns("key", 1, true, false));
		cols.add(new Columns("value", 2, false, true));
		cols.add(new Columns("environment", 3, false, false));
		return cols;
	}

	public static List<Map<String, String>> constructDataSet() {
		List<Map<String, String>> dataSet = new ArrayList<>();
		Map<String, String> data1 = new HashMap<>();
		dataSet.add(data1);
		return dataSet;
	}

}
