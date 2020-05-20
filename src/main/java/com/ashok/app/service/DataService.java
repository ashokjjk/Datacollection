package com.ashok.app.service;

import org.springframework.stereotype.Component;

@Component
public class DataService {
	private String name, variable, units, date;
	private boolean flag;
	private Double value;

	public DataService(String name, String variable, String units, String date, boolean flag, Double value) {
		this.name = name;
		this.variable = variable;
		this.units = units;
		this.date = date;
		this.flag = flag;
		this.value = value;
	}

	public DataService() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return name + ","+ variable + "," + units + "," + date
				+ "," + flag + "," + value;
	}
	
	

}
