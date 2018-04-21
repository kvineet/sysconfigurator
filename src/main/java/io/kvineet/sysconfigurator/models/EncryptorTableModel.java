package io.kvineet.sysconfigurator.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class EncryptorTableModel implements TableModel {
	
	private final List<Map<String, String>> dataSet;
	private final List<Columns> columns;
	private final List<TableModelListener> listners = new ArrayList<>();
	private final List<Map<String, String>> removed;
	
	public EncryptorTableModel(List<Columns> columns, List<Map<String, String>> dataSet) {
		this.dataSet = dataSet;
		this.columns = columns;
		this.removed = new ArrayList<>();
		Collections.sort(columns, new Columns.SortByOrder());
		fireDataChangedEvents();
	}
	
	public void reloadData(List<Map<String, String>> dataSet) {
		this.dataSet.removeIf(e -> true); 
		this.dataSet.addAll(dataSet);
		this.removed.removeIf( e -> true);
		fireDataChangedEvents();
	}
	
	public void reloadData(List<Columns> columns, List<Map<String, String>> dataSet) {
		this.dataSet.removeIf(e -> true); 
		this.dataSet.addAll(dataSet);
		
		this.columns.removeIf(e -> true);
		this.columns.addAll(columns);
		
		this.removed.removeIf( e -> true);
		fireDataChangedEvents();
	}
	
	public List<Columns> getColumns(){
		return columns;
	}
	
	public List<Map<String, String>> getDataSet(){
		return dataSet;
	}

	public List<Map<String, String>> getRemovedData(){
		return removed;
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		listners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columns.get(columnIndex).getName();
	}

	@Override
	public int getRowCount() {
		return dataSet.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Columns col = columns.get(columnIndex);
		Map<String, String> data = dataSet.get(rowIndex);
		return data.getOrDefault(col.getName(), "");
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Columns col = columns.get(columnIndex);
		if(col.isPrimaryKey() && checkDuplicate((String) aValue, rowIndex, col.getName())) {
				return;
		}
		Map<String, String> data = dataSet.get(rowIndex);
		data.put(col.getName(), (String) aValue);
		fireDataChangedEvents(rowIndex);
	}
	
	private boolean checkDuplicate(String aValue, int rowIndex, String name) {
		Map<String, String> rowData = dataSet.get(rowIndex);
		
		String newVal = columns.stream()
				.filter(e -> e.isPrimaryKey())
				.map(e -> {
					if(name.equals(e.getName())) {
						return aValue;
					}
					else {
						return rowData.getOrDefault(e.getName(), "");
					}
				})
				.collect(Collectors.joining(""));
		
		int i =0;
		for(Map<String, String> data: dataSet) {
			String pkeyValue = columns.stream()
				.filter(e -> e.isPrimaryKey())
				.map(e -> data.get(e.getName()))
				.collect(Collectors.joining(""));
			if(i !=rowIndex && pkeyValue.equals(newVal)) {
				return true;
			}
			i++;
		}
		return false;
	}

	public void addRow() {
		dataSet.add(new HashMap<String, String>());
		fireDataChangedEvents();
	}
	
	public void removeRow(int rowIndex) {
		Map<String, String> data= dataSet.get(rowIndex);
		removed.add(data);
		dataSet.remove(data);
		fireDataChangedEvents();
	}
	
	private void fireDataChangedEvents() {
		TableModelEvent e = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
		fireDataChangedEvents(e);
	}
	
	private void fireDataChangedEvents(int rowIndex) {
		TableModelEvent e = new TableModelEvent(this, rowIndex);
		fireDataChangedEvents(e);
	}


	private void fireDataChangedEvents(TableModelEvent e) {
		for(TableModelListener eventListner : listners) {
			eventListner.tableChanged(e);
		}
	}

	public void refreshData() {
		fireDataChangedEvents();
	}

	

}
