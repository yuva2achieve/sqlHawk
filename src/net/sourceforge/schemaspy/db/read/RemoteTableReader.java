/*
 * This file is a part of the SchemaSpy project (http://schemaspy.sourceforge.net).
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 John Currier
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sourceforge.schemaspy.db.read;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.model.Database;
import net.sourceforge.schemaspy.model.RemoteTable;
import net.sourceforge.schemaspy.model.Table;

/**
 * A table that's outside of the default schema but is referenced
 * by or references a table in the default schema.
 *
 * @author John Currier
 */
public class RemoteTableReader extends TableReader {

	/**
     * Connect to the PK's referenced by this table that live in the original schema
     * @param tables
     * @param excludeIndirectColumns
     * @param excludeColumns
     * @throws SQLException
     */
	public static void connectForeignKeys(
    		Database db, String schema, String name, String baseSchema, Properties properties,
    		DatabaseMetaData meta, RemoteTable table, Map<String, Table> tables,
    		Pattern excludeIndirectColumns, Pattern excludeColumns, TableReader tableReader, DbReader dbReader) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getImportedKeys(null, table.getSchema(), table.getName());

            while (rs.next()) {
                String otherSchema = rs.getString("PKTABLE_SCHEM");
                if (otherSchema != null && otherSchema.equals(baseSchema)) {
                    tableReader.addForeignKey(rs.getString("FK_NAME"), rs.getString("FKCOLUMN_NAME"),
                            rs.getString("PKTABLE_SCHEM"), rs.getString("PKTABLE_NAME"),
                            rs.getString("PKCOLUMN_NAME"),
                            rs.getInt("UPDATE_RULE"), rs.getInt("DELETE_RULE"),
                            tables, excludeIndirectColumns, excludeColumns, dbReader);
                }
            }
        } catch (SQLException sqlExc) {
            // if explicitly asking for these details then propagate the exception
            if (Config.getInstance().isOneOfMultipleSchemas())
                throw sqlExc;

            // otherwise just report the fact that we tried & couldn't
            System.err.println("Couldn't resolve foreign keys for remote table " + table.getSchema() + "." + table.getName() + ": " + sqlExc);
        } finally {
            if (rs != null)
                rs.close();
        }
    }
}