package com.brobert.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brobert.exceptions.CouldNotConnectException;

/**
 * Test comment 1
 *
 * @author brobert
 */
public class ConnectionManager {

	private Map<String, ConnectionInfo> connectionInfos;
	private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);



	private ConnectionManager() {
		connectionInfos = new LinkedHashMap<>();
	}



	/**
	 * @param alias
	 */
	public Connection open(String alias) {
		if (validate(alias) == false) {
			throw new RuntimeException(alias + " is not an alias that was registered with this ConnectionManager instance.");
		}
		String username = connectionInfos.get(alias).getUsername();
		String password = connectionInfos.get(alias).getPassword();
		String jdbcUrl = connectionInfos.get(alias).getJdbcUrl();
		DBVendorType vendorType = connectionInfos.get(alias).getVendorType();
		Connection connection = null;
		try {
			Class.forName(classForName(vendorType));
			connection = DriverManager.getConnection(jdbcUrl, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}



	private Connection openThrowException(String alias) throws ClassNotFoundException, SQLException {
		if (validate(alias) == false) {
			throw new RuntimeException(alias + " is not an alias that was registered with this ConnectionManager instance.");
		}
		String username = connectionInfos.get(alias).getUsername();
		String password = connectionInfos.get(alias).getPassword();
		String jdbcUrl = connectionInfos.get(alias).getJdbcUrl();
		DBVendorType vendorType = connectionInfos.get(alias).getVendorType();
		Connection connection = null;
		Class.forName(classForName(vendorType));
		connection = DriverManager.getConnection(jdbcUrl, username, password);
		return connection;
	}



	public List<String> aliases() {
		List<String> aliases = new ArrayList<>();
		for (String alias : connectionInfos.keySet()) {
			aliases.add(alias);
		}
		return aliases;
	}



	/**
	 * @return
	 */
	private boolean validate(String alias) {
		boolean isValid = true;
		try {
			if (connectionInfos.get(alias) == null) {
				isValid = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isValid;
	}



	public static Builder newBuilder() {
		return new Builder();
	}





	public static class Builder {
		private ConnectionManager instance;



		private Builder() {
			instance = new ConnectionManager();
		}





		public static class Registrar {
			private String alias, username, password, jdbcUrl;
			private DBVendorType vendorType;
			private int refreshAfter = 200;

			private Builder builder;



			public Registrar(Builder builder) {
				this.builder = builder;
			}



			public Registrar alias(String alias) {
				this.alias = alias;
				return this;
			}



			public Registrar username(String username) {
				this.username = username;
				return this;
			}



			public Registrar password(String password) {
				this.password = password;
				return this;
			}



			public Registrar jdbcUrl(String jdbcUrl) {
				this.jdbcUrl = jdbcUrl;
				return this;
			}



			public Registrar dbVendor(DBVendorType vendorType) {
				this.vendorType = vendorType;
				return this;
			}



			public Registrar refreshAfter(int refreshAfter) {
				this.refreshAfter = refreshAfter;
				return this;
			}



			public Builder add() {
				ConnectionInfo info = new ConnectionInfo(username, password, jdbcUrl, vendorType, refreshAfter);
				builder.instance.connectionInfos.put(alias, info);
				return builder;
			}

		}



		public Registrar register() {
			return new Registrar(this);
		}



		public ConnectionManager build() {
			return instance;
		}
	}



	/**
	 * @param vendorType
	 * @return
	 */
	private String classForName(DBVendorType vendorType) {
		switch (vendorType) {
			case DB2:
				return "com.ibm.db2.jcc.DB2Driver";

			case MYSQL:
				return "com.mysql.jdbc.Driver";

			case SQLSERVER:
				return "com.microsoft.sqlserver.jdbc.SQLServerDriver";

			default:
				throw new IllegalArgumentException("This vendor enum has not been registered in the class for name method yet.");

		}
	}



	public List<CouldNotConnectException> failedConnectionCount() {
		List<CouldNotConnectException> exceptions = new ArrayList<>();
		for (String alias : connectionInfos.keySet()) {
			logger.info("Testing connection [" + alias + "]...");
			String sql = pingQuery(connectionInfos.get(alias).getVendorType());
			try (Connection conn = openThrowException(alias);
					PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.execute();
				logger.info("[" + alias + "] was connected to succesfully.");
			} catch (Exception e) {
				logger.error("[" + alias + "] did not succesfully connect");
				exceptions.add(new CouldNotConnectException("Could not connect to connection with the alias [" + alias + "]"));
			}
		}
		return exceptions;
	}



	private String pingQuery(DBVendorType vendor) {
		switch (vendor) {
			case DB2:
				return "VALUES 1";

			case MYSQL:
				return "SELECT 1";

			case SQLSERVER:
				return "SELECT 1";

			default:
				throw new IllegalArgumentException("This vendor enum has not been registered in the class for name method yet.");

		}
	}
}
