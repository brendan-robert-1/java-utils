/**
 *
 */
package com.brobert.report;

/**
 * @author brobert
 *
 */

public class DemoRecord {

	@RecordElement(name = "User Name")
	private String name;

	private int userAge;

	@RecordElement(include = false)
	private int social;

	private String longFieldNameCamel;



	public DemoRecord(String name, int userAge, int social, String longFieldNameCamel) {
		this.name = name;
		this.userAge = userAge;
		this.social = social;
		this.longFieldNameCamel = longFieldNameCamel;
	}



	public String getName() {
		return name;
	}



	public int getUserAge() {
		return userAge;
	}



	public int getSocial() {
		return social;
	}



	public String getLongFieldNameCamel() {
		return longFieldNameCamel;
	}

}
