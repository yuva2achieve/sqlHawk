/* This file is a part of the sqlHawk project.
 * http://timabell.github.com/sqlHawk/
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package uk.co.timwise.sqlhawk.db;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import uk.co.timwise.sqlhawk.config.Config;
import uk.co.timwise.sqlhawk.config.DbSpecificOption;
import uk.co.timwise.sqlhawk.config.DbType;


public class ConnectionURLBuilder {
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * 
	 * @param config
	 * @param properties
	 * @throws Exception 
	 */
	public String buildUrl(Config config) throws Exception {
		DbType dbType = config.getDbType();
		List<DbSpecificOption> driverOptions = dbType.getOptions();
		String connectionURL = buildUrlFromArgs(config, driverOptions);
		logger.config("connectionURL: " + connectionURL);
		return connectionURL;
	}

	private String buildUrlFromArgs(Config config, List<DbSpecificOption> driverOptions) throws Exception {
		Properties properties = config.getDbType().getProps();
		String connectionSpec = properties.getProperty("connectionSpec");
		Map<String, String> extraConnectionOptions = config.getExtraOptions();
		for (DbSpecificOption option : driverOptions) {
			//options available directly in hard coded command line arguments of sqlHawk
			if (option.getName().equalsIgnoreCase("host") && config.getHost() != null) {
				option.setValue(config.getHost());
			} else if (option.getName().equalsIgnoreCase("port")
				         && (config.getPort() != null || properties.getProperty("default-port") != null)) {
				if (config.getPort() != null){
					option.setValue(config.getPort());
				} else {
					option.setValue(properties.getProperty("default-port"));
				}
			} else if (option.getName().equalsIgnoreCase("database") && config.getDatabase() != null) {
				option.setValue(config.getDatabase());
			} else if (option.getName().equalsIgnoreCase("instance") && config.getDatabaseInstance() != null) {
				option.setValue(config.getDatabaseInstance());
			//options available through the "connection-options" multi-part command line argument of sqlHawk
			} else if (extraConnectionOptions.containsKey(option.getName())) {
				option.setValue(extraConnectionOptions.get(option.getName()));
			} else {
				throw new Exception("The specified database driver requires option '" + option.getName() + "' which has not been supplied. You can supply extra options with --connection-options (see --help for more information)");
			}
			//perform actual replacement in driver string e.g. <host> with <myDbHost>
			connectionSpec = connectionSpec.replaceAll("\\<" + option.getName() + "\\>", option.getValue().toString());
		}
		return connectionSpec;
	}
}
