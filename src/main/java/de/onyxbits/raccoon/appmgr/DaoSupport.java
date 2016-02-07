/*
 * Copyright 2016 Patrick Ahlbrecht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.onyxbits.raccoon.appmgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * {@link AppGroupDao} and {@link AndroidAppDao} share a junction table. The
 * junction table can only be created if the other two already exist. Since
 * everything in Raccoon is on demand, we can't reliably tell which DAO gets
 * requested first and therefore we can't put the junction table's creation in
 * either module.
 * 
 * @author patrick
 * 
 */
class DaoSupport {

	/**
	 * Do the version 1 shared init of the daos.
	 * 
	 * @param c
	 * @throws SQLException
	 */
	protected static void v1Shared(Connection c) throws SQLException {

		PreparedStatement st = c
				.prepareStatement("CREATE TABLE IF NOT EXISTS androidapps (aid BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY, packagename VARCHAR(255) NOT NULL, versioncode INT NOT NULL, mainversion INT NOT NULL, patchversion INT NOT NULL, name VARCHAR(255) NOT NULL, version VARCHAR(255) NOT NULL, minsdk INT NOT NULL)");
		st.execute();
		st.close();

		// This one is actually not shared, but since we are already at it, create
		// it as well.
		st = c
				.prepareStatement("CREATE TABLE IF NOT EXISTS permissions (pid BIGINT FOREIGN KEY REFERENCES androidapps ON DELETE CASCADE, name VARCHAR(255))");
		st.execute();

		st.close();

		st = c
				.prepareStatement("CREATE TABLE IF NOT EXISTS appgroups (gid BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY, name VARCHAR(255) NOT NULL UNIQUE)");
		st.execute();
		st.close();

		st = c
				.prepareStatement("CREATE TABLE IF NOT EXISTS androidapps_appgroups (aid BIGINT FOREIGN KEY REFERENCES androidapps ON DELETE CASCADE, gid BIGINT FOREIGN KEY REFERENCES appgroups ON DELETE CASCADE )");
		st.execute();
		st.close();
	}
}
