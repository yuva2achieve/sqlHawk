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
package uk.co.timwise.sqlhawk.model;

public class Procedure implements Comparable<Procedure>, ISqlObject {
	private String schema;
	private String name;
	private String definition;

	public String getSchema() {
		return schema;
	}

	public String getName() {
		return name;
	}

	public String getDefinition(){
		return definition;
	}

	public Procedure() {}

	public Procedure(String schema, String name, String definition) {
		this.schema = schema;
		this.name = name;
		this.definition = definition;

		//Remove all leading and trailing and whitespace for the procedure sql when saving.
		//It means trailing and leading whitespace will be ignored when checking if an sp has been updated
		// which seems sensible.
		if (this.definition != null) {
			this.definition = this.definition.trim();
		}
	}

	/**
	 * compare stored proc based on name and definition
	 */
	@Override
	public int compareTo(Procedure proc) {
		int nameCompare = name.compareTo(proc.name);
		if (nameCompare!=0)
			return nameCompare;
		return definition.compareTo(proc.definition);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDefinition(String definition) {
		this.definition = definition;
	}
}
