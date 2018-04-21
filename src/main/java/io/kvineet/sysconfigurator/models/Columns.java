package io.kvineet.sysconfigurator.models;

import java.util.Comparator;

public class Columns {

	public Columns() {

	}

	public Columns(String name, int order, boolean primaryKey, boolean encrypted) {
		super();
		this.name = name;
		this.order = order;
		this.primaryKey = primaryKey;
		this.encrypted = encrypted;
	}

	private String name;
	private int order;
	private boolean primaryKey;
	private boolean encrypted;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public static class SortByOrder implements Comparator<Columns> {

		@Override
		public int compare(Columns o1, Columns o2) {
			return o1.order - o2.order;
		}

	}

}
