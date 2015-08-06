/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.configuration.persistence;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactoryUtil;
import com.liferay.portal.kernel.io.ReaderInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.ReflectionUtil;

import java.io.IOException;
import java.io.StringReader;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javax.sql.DataSource;

import org.apache.felix.cm.NotCachablePersistenceManager;
import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.file.ConfigurationHandler;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		Constants.SERVICE_RANKING + ":Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = {PersistenceManager.class, ReloadablePersitenceManager.class}
)
public class ConfigurationPersistenceManager
	implements NotCachablePersistenceManager, ReloadablePersitenceManager {

	@Override
	public void delete(final String pid) throws IOException {
		if (System.getSecurityManager() != null) {
			try {
				AccessController.doPrivileged(
					new PrivilegedExceptionAction<Void>() {

						@Override
						public Void run() throws Exception {
							_delete(pid);

							return null;
						}

					});
			}
			catch (PrivilegedActionException pae) {
				throw (IOException)pae.getException();
			}
		}
		else {
			_delete(pid);
		}
	}

	@Override
	public boolean exists(String pid) {
		ReadLock readLock = _readWriteLock.readLock();

		try {
			readLock.lock();

			return _dictionaryMap.containsKey(pid);
		}
		finally {
			readLock.unlock();
		}
	}

	@Override
	public Enumeration<?> getDictionaries() {
		ReadLock readLock = _readWriteLock.readLock();

		try {
			readLock.lock();

			return Collections.enumeration(_dictionaryMap.values());
		}
		finally {
			readLock.unlock();
		}
	}

	@Override
	public Dictionary<?, ?> load(String pid) {
		ReadLock readLock = _readWriteLock.readLock();

		try {
			readLock.lock();

			return _dictionaryMap.get(pid);
		}
		finally {
			readLock.unlock();
		}
	}

	@Override
	public void reload(String pid) throws IOException {
		WriteLock writeLock = _readWriteLock.writeLock();

		try {
			writeLock.lock();

			_dictionaryMap.remove(pid);

			if (hasPid(pid)) {
				Dictionary<?, ?> dictionary = loadPid(pid);

				_dictionaryMap.put(pid, dictionary);
			}
		}
		finally {
			writeLock.unlock();
		}
	}

	@Override
	public void store(
			final String pid,
			@SuppressWarnings("rawtypes") final Dictionary dictionary)
		throws IOException {

		if (System.getSecurityManager() != null) {
			try {
				AccessController.doPrivileged(
					new PrivilegedExceptionAction<Void>() {

						@Override
						public Void run() throws Exception {
							_store(pid, dictionary);

							return null;
						}

					});
			}
			catch (PrivilegedActionException pae) {
				throw (IOException)pae.getException();
			}
		}
		else {
			_store(pid, dictionary);
		}
	}

	@Activate
	protected void activate() {
		ReadLock readLock = _readWriteLock.readLock();
		WriteLock writeLock = _readWriteLock.writeLock();

		try {
			readLock.lock();

			if (!hasConfigurationTable()) {
				readLock.unlock();
				writeLock.lock();

				try {
					createConfigurationTable();
				}
				finally {
					readLock.lock();
					writeLock.unlock();
				}
			}

			loadAllRecords();
		}
		finally {
			readLock.unlock();
		}
	}

	protected String buildSQL(String sql) throws IOException {
		DB db = DBFactoryUtil.getDB();

		return db.buildSQL(sql);
	}

	protected void cleanUp(
		Connection connection, Statement statement, ResultSet resultSet) {

		try {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		catch (SQLException sqle) {
			ReflectionUtil.throwException(sqle);
		}
		finally {
			try {
				if (statement != null) {
					statement.close();
				}
			}
			catch (SQLException sqle) {
				ReflectionUtil.throwException(sqle);
			}
			finally {
				try {
					if (connection != null) {
						connection.close();
					}
				}
				catch (SQLException sqle) {
					ReflectionUtil.throwException(sqle);
				}
			}
		}
	}

	protected boolean hasConfigurationTable() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();

			preparedStatement = prepareStatement(
				connection, "select count(*) from Configuration_");

			resultSet = preparedStatement.executeQuery();

			int count = 0;

			if (resultSet.next()) {
				count = resultSet.getInt(1);
			}

			if (count >= 0) {
				return true;
			}

			return false;
		}
		catch (IOException | SQLException e) {
			return false;
		}
		finally {
			cleanUp(connection, preparedStatement, resultSet);
		}
	}

	protected void createConfigurationTable() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();

			statement = connection.createStatement();

			statement.executeUpdate(
				buildSQL(
					"create table Configuration_ (configurationId " +
						"VARCHAR(255) not null primary key, dictionary TEXT)"));
		}
		catch (IOException | SQLException e) {
			ReflectionUtil.throwException(e);
		}
		finally {
			cleanUp(connection, statement, resultSet);
		}
	}

	@Deactivate
	protected void deactivate() {
		_dictionaryMap.clear();
	}
	
	protected PreparedStatement prepareStatement(
			Connection connection, String sql)
		throws IOException, SQLException {

		return connection.prepareStatement(buildSQL(sql));
	}

	protected void deleteFromDatabase(String pid) throws IOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = _dataSource.getConnection();

			preparedStatement = prepareStatement(
				connection,
				"delete from Configuration_ where configurationId = ?");

			preparedStatement.setString(1, pid);

			preparedStatement.executeUpdate();
		}
		catch (SQLException sqle) {
			throw new IOException(sqle);
		}
		finally {
			cleanUp(connection, preparedStatement, null);
		}
	}

	protected boolean hasPid(String pid) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();

			preparedStatement = prepareStatement(
				connection,
				"select count(*) from Configuration_ where " +
					"configurationId = ?");

			preparedStatement.setString(1, pid);

			resultSet = preparedStatement.executeQuery();

			int count = 0;

			if (resultSet.next()) {
				count = resultSet.getInt(1);
			}

			if (count > 0) {
				return true;
			}

			return false;
		}
		catch (IOException | SQLException e) {
			return ReflectionUtil.throwException(e);
		}
		finally {
			cleanUp(connection, preparedStatement, resultSet);
		}
	}

	protected void loadAllRecords() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();

			preparedStatement = connection.prepareStatement(
				buildSQL(
					"select configurationId, dictionary from Configuration_ " +
						"ORDER BY configurationId ASC"),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String pid = resultSet.getString(1);
				String configuration = resultSet.getString(2);

				_dictionaryMap.putIfAbsent(pid, read(configuration));
			}
		}
		catch (IOException | SQLException e) {
			ReflectionUtil.throwException(e);
		}
		finally {
			cleanUp(connection, preparedStatement, resultSet);
		}
	}

	protected Dictionary<?, ?> loadPid(String pid) throws IOException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();

			preparedStatement = prepareStatement(
				connection,
				"select dictionary from Configuration_ where " +
					"configurationId = ?");

			preparedStatement.setString(1, pid);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return read(resultSet.getString(1));
			}

			return _emptyDictionary;
		}
		catch (SQLException se) {
			return ReflectionUtil.throwException(se);
		}
		finally {
			cleanUp(connection, preparedStatement, resultSet);
		}
	}

	protected Dictionary<?, ?> read(String configuration)
		throws IOException {

		ReaderInputStream readerInputStream = new ReaderInputStream(
			new StringReader(configuration));

		return ConfigurationHandler.read(readerInputStream);
	}

	@Reference(target = "(bean.id=liferayDataSource)")
	protected void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	protected void store(ResultSet resultSet, Dictionary<?, ?> dictionary)
		throws IOException, SQLException {

		UnsyncByteArrayOutputStream outputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(outputStream, dictionary);

		resultSet.updateString(2, outputStream.toString());
	}

	@SuppressWarnings("resource")
	protected void storeInDB(String pid, Dictionary<?, ?> dictionary)
		throws IOException {

		UnsyncByteArrayOutputStream outputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(outputStream, dictionary);

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = _dataSource.getConnection();
			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(
				buildSQL(
					"select configurationId, dictionary from Configuration_ " +
						"where configurationId = ?"),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

			preparedStatement.setString(1, pid);

			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				resultSet.updateString(2, outputStream.toString());
				resultSet.updateRow();
			}
			else {
				preparedStatement = prepareStatement(
					connection,
					"insert into Configuration_ (configurationId, " +
						"dictionary) values (?, ?)");

				preparedStatement.setString(1, pid);
				preparedStatement.setString(2, outputStream.toString());
				preparedStatement.executeUpdate();
			}

			connection.commit();
		}
		catch (SQLException sqle) {
			ReflectionUtil.throwException(sqle);
		}
		finally {
			cleanUp(connection, preparedStatement, resultSet);

			outputStream.close();
		}
	}

	private void _delete(String pid) throws IOException {
		WriteLock writeLock = _readWriteLock.writeLock();

		try {
			writeLock.lock();

			Dictionary<?, ?> dictionary = _dictionaryMap.remove(pid);

			if ((dictionary != null) && hasPid(pid)) {
				deleteFromDatabase(pid);
			}
		}
		finally {
			writeLock.unlock();
		}
	}

	private void _store(
			String pid, @SuppressWarnings("rawtypes") Dictionary dictionary)
		throws IOException {

		WriteLock writeLock = _readWriteLock.writeLock();

		try {
			writeLock.lock();

			storeInDB(pid, dictionary);

			_dictionaryMap.put(pid, dictionary);
		}
		finally {
			writeLock.unlock();
		}
	}

	private static final Dictionary<?, ?> _emptyDictionary = new Hashtable<>();

	private DataSource _dataSource;
	private final ConcurrentMap<String, Dictionary<?, ?>> _dictionaryMap =
		new ConcurrentHashMap<>();
	private final ReentrantReadWriteLock _readWriteLock =
		new ReentrantReadWriteLock(true);

}