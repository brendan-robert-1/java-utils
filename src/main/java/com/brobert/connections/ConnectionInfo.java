/**
 *
 */
package com.brobert.connections;

import org.apache.commons.lang.Validate;

/**
 * @author brobert
 *
 */
public class ConnectionInfo {

	private String username, password, JdbcUrl;
	private DBVendorType vendorType;
	private int refreshAfter;

	private static final int DEFAULT_REFRESH = 200;



	public ConnectionInfo(String username, String password, String jdbcUrl, DBVendorType vendorType) {
		setUsername(username);
		setPassword(password);
		setJdbcUrl(jdbcUrl);
		setVendorType(vendorType);
		setRefreshAfter(DEFAULT_REFRESH);
	}


	public ConnectionInfo(String username, String password, String jdbcUrl, DBVendorType vendorType, int refreshAfter) {
		setUsername(username);
		setPassword(password);
		setJdbcUrl(jdbcUrl);
		setVendorType(vendorType);
		setRefreshAfter(refreshAfter);
	}



	public String getUsername() {
		return username;
	}



	private void setUsername(String username) {
		Validate.notEmpty(username);
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	private void setPassword(String password) {
		Validate.notEmpty(password);
		this.password = password;
	}



	public String getJdbcUrl() {
		return JdbcUrl;
	}



	private void setJdbcUrl(String jdbcUrl) {
		Validate.notEmpty(jdbcUrl);
		JdbcUrl = jdbcUrl;
	}



	public DBVendorType getVendorType() {
		return vendorType;
	}



	private void setVendorType(DBVendorType vendorType) {
		Validate.notNull(vendorType);
		this.vendorType = vendorType;
	}



	public int getRefreshAfter() {
		return refreshAfter;
	}



	public void setRefreshAfter(int refreshAfter) {
		if (refreshAfter < 1) {
			throw new IllegalArgumentException("Refresh After can not be less than 1");
		}
		this.refreshAfter = refreshAfter;
	}
}
