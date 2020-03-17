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

package com.liferay.portal.tools.service.builder.test.service.base;

import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalServiceRegistry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntity;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntityBlob1BlobModel;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntityBlob2BlobModel;
import com.liferay.portal.tools.service.builder.test.service.LazyBlobEntityLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.LazyBlobEntityPersistence;

import java.io.InputStream;
import java.io.Serializable;

import java.sql.Blob;

import java.util.List;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the lazy blob entity local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.portal.tools.service.builder.test.service.impl.LazyBlobEntityLocalServiceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.portal.tools.service.builder.test.service.impl.LazyBlobEntityLocalServiceImpl
 * @generated
 */
public abstract class LazyBlobEntityLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements IdentifiableOSGiService, LazyBlobEntityLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>LazyBlobEntityLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>com.liferay.portal.tools.service.builder.test.service.LazyBlobEntityLocalServiceUtil</code>.
	 */

	/**
	 * Adds the lazy blob entity to the database. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntity the lazy blob entity
	 * @return the lazy blob entity that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public LazyBlobEntity addLazyBlobEntity(LazyBlobEntity lazyBlobEntity) {
		lazyBlobEntity.setNew(true);

		return lazyBlobEntityPersistence.update(lazyBlobEntity);
	}

	/**
	 * Creates a new lazy blob entity with the primary key. Does not add the lazy blob entity to the database.
	 *
	 * @param lazyBlobEntityId the primary key for the new lazy blob entity
	 * @return the new lazy blob entity
	 */
	@Override
	@Transactional(enabled = false)
	public LazyBlobEntity createLazyBlobEntity(long lazyBlobEntityId) {
		return lazyBlobEntityPersistence.create(lazyBlobEntityId);
	}

	/**
	 * Deletes the lazy blob entity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntityId the primary key of the lazy blob entity
	 * @return the lazy blob entity that was removed
	 * @throws PortalException if a lazy blob entity with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public LazyBlobEntity deleteLazyBlobEntity(long lazyBlobEntityId)
		throws PortalException {

		return lazyBlobEntityPersistence.remove(lazyBlobEntityId);
	}

	/**
	 * Deletes the lazy blob entity from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntity the lazy blob entity
	 * @return the lazy blob entity that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public LazyBlobEntity deleteLazyBlobEntity(LazyBlobEntity lazyBlobEntity) {
		return lazyBlobEntityPersistence.remove(lazyBlobEntity);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(
			LazyBlobEntity.class, clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return lazyBlobEntityPersistence.findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntityModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return lazyBlobEntityPersistence.findWithDynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntityModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return lazyBlobEntityPersistence.findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return lazyBlobEntityPersistence.countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection) {

		return lazyBlobEntityPersistence.countWithDynamicQuery(
			dynamicQuery, projection);
	}

	@Override
	public LazyBlobEntity fetchLazyBlobEntity(long lazyBlobEntityId) {
		return lazyBlobEntityPersistence.fetchByPrimaryKey(lazyBlobEntityId);
	}

	/**
	 * Returns the lazy blob entity matching the UUID and group.
	 *
	 * @param uuid the lazy blob entity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching lazy blob entity, or <code>null</code> if a matching lazy blob entity could not be found
	 */
	@Override
	public LazyBlobEntity fetchLazyBlobEntityByUuidAndGroupId(
		String uuid, long groupId) {

		return lazyBlobEntityPersistence.fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the lazy blob entity with the primary key.
	 *
	 * @param lazyBlobEntityId the primary key of the lazy blob entity
	 * @return the lazy blob entity
	 * @throws PortalException if a lazy blob entity with the primary key could not be found
	 */
	@Override
	public LazyBlobEntity getLazyBlobEntity(long lazyBlobEntityId)
		throws PortalException {

		return lazyBlobEntityPersistence.findByPrimaryKey(lazyBlobEntityId);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(lazyBlobEntityLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(LazyBlobEntity.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("lazyBlobEntityId");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(
			lazyBlobEntityLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(LazyBlobEntity.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName(
			"lazyBlobEntityId");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {

		actionableDynamicQuery.setBaseLocalService(lazyBlobEntityLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(LazyBlobEntity.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("lazyBlobEntityId");
	}

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return lazyBlobEntityPersistence.create(
			((Long)primaryKeyObj).longValue());
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return lazyBlobEntityLocalService.deleteLazyBlobEntity(
			(LazyBlobEntity)persistedModel);
	}

	public BasePersistence<LazyBlobEntity> getBasePersistence() {
		return lazyBlobEntityPersistence;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return lazyBlobEntityPersistence.findByPrimaryKey(primaryKeyObj);
	}

	/**
	 * Returns the lazy blob entity matching the UUID and group.
	 *
	 * @param uuid the lazy blob entity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching lazy blob entity
	 * @throws PortalException if a matching lazy blob entity could not be found
	 */
	@Override
	public LazyBlobEntity getLazyBlobEntityByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return lazyBlobEntityPersistence.findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns a range of all the lazy blob entities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.LazyBlobEntityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of lazy blob entities
	 * @param end the upper bound of the range of lazy blob entities (not inclusive)
	 * @return the range of lazy blob entities
	 */
	@Override
	public List<LazyBlobEntity> getLazyBlobEntities(int start, int end) {
		return lazyBlobEntityPersistence.findAll(start, end);
	}

	/**
	 * Returns the number of lazy blob entities.
	 *
	 * @return the number of lazy blob entities
	 */
	@Override
	public int getLazyBlobEntitiesCount() {
		return lazyBlobEntityPersistence.countAll();
	}

	/**
	 * Updates the lazy blob entity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param lazyBlobEntity the lazy blob entity
	 * @return the lazy blob entity that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public LazyBlobEntity updateLazyBlobEntity(LazyBlobEntity lazyBlobEntity) {
		return lazyBlobEntityPersistence.update(lazyBlobEntity);
	}

	/**
	 * Returns the lazy blob entity local service.
	 *
	 * @return the lazy blob entity local service
	 */
	public LazyBlobEntityLocalService getLazyBlobEntityLocalService() {
		return lazyBlobEntityLocalService;
	}

	/**
	 * Sets the lazy blob entity local service.
	 *
	 * @param lazyBlobEntityLocalService the lazy blob entity local service
	 */
	public void setLazyBlobEntityLocalService(
		LazyBlobEntityLocalService lazyBlobEntityLocalService) {

		this.lazyBlobEntityLocalService = lazyBlobEntityLocalService;
	}

	/**
	 * Returns the lazy blob entity persistence.
	 *
	 * @return the lazy blob entity persistence
	 */
	public LazyBlobEntityPersistence getLazyBlobEntityPersistence() {
		return lazyBlobEntityPersistence;
	}

	/**
	 * Sets the lazy blob entity persistence.
	 *
	 * @param lazyBlobEntityPersistence the lazy blob entity persistence
	 */
	public void setLazyBlobEntityPersistence(
		LazyBlobEntityPersistence lazyBlobEntityPersistence) {

		this.lazyBlobEntityPersistence = lazyBlobEntityPersistence;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public com.liferay.counter.kernel.service.CounterLocalService
		getCounterLocalService() {

		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(
		com.liferay.counter.kernel.service.CounterLocalService
			counterLocalService) {

		this.counterLocalService = counterLocalService;
	}

	@Override
	public LazyBlobEntityBlob1BlobModel getBlob1BlobModel(
		Serializable primaryKey) {

		Session session = null;

		try {
			session = lazyBlobEntityPersistence.openSession();

			return (LazyBlobEntityBlob1BlobModel)session.get(
				LazyBlobEntityBlob1BlobModel.class, primaryKey);
		}
		catch (Exception exception) {
			throw lazyBlobEntityPersistence.processException(exception);
		}
		finally {
			lazyBlobEntityPersistence.closeSession(session);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InputStream openBlob1InputStream(long lazyBlobEntityId) {
		try {
			LazyBlobEntityBlob1BlobModel LazyBlobEntityBlob1BlobModel =
				getBlob1BlobModel(lazyBlobEntityId);

			Blob blob = LazyBlobEntityBlob1BlobModel.getBlob1Blob();

			if (blob == null) {
				return _EMPTY_INPUT_STREAM;
			}

			InputStream inputStream = blob.getBinaryStream();

			if (_useTempFile) {
				inputStream = new AutoDeleteFileInputStream(
					_file.createTempFile(inputStream));
			}

			return inputStream;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@Override
	public LazyBlobEntityBlob2BlobModel getBlob2BlobModel(
		Serializable primaryKey) {

		Session session = null;

		try {
			session = lazyBlobEntityPersistence.openSession();

			return (LazyBlobEntityBlob2BlobModel)session.get(
				LazyBlobEntityBlob2BlobModel.class, primaryKey);
		}
		catch (Exception exception) {
			throw lazyBlobEntityPersistence.processException(exception);
		}
		finally {
			lazyBlobEntityPersistence.closeSession(session);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public InputStream openBlob2InputStream(long lazyBlobEntityId) {
		try {
			LazyBlobEntityBlob2BlobModel LazyBlobEntityBlob2BlobModel =
				getBlob2BlobModel(lazyBlobEntityId);

			Blob blob = LazyBlobEntityBlob2BlobModel.getBlob2Blob();

			if (blob == null) {
				return _EMPTY_INPUT_STREAM;
			}

			InputStream inputStream = blob.getBinaryStream();

			if (_useTempFile) {
				inputStream = new AutoDeleteFileInputStream(
					_file.createTempFile(inputStream));
			}

			return inputStream;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	public void afterPropertiesSet() {
		persistedModelLocalServiceRegistry.register(
			"com.liferay.portal.tools.service.builder.test.model.LazyBlobEntity",
			lazyBlobEntityLocalService);

		DB db = DBManagerUtil.getDB();

		if ((db.getDBType() != DBType.DB2) &&
			(db.getDBType() != DBType.MYSQL) &&
			(db.getDBType() != DBType.MARIADB) &&
			(db.getDBType() != DBType.SYBASE)) {

			_useTempFile = true;
		}
	}

	public void destroy() {
		persistedModelLocalServiceRegistry.unregister(
			"com.liferay.portal.tools.service.builder.test.model.LazyBlobEntity");
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return LazyBlobEntityLocalService.class.getName();
	}

	protected Class<?> getModelClass() {
		return LazyBlobEntity.class;
	}

	protected String getModelClassName() {
		return LazyBlobEntity.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = lazyBlobEntityPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	@BeanReference(type = LazyBlobEntityLocalService.class)
	protected LazyBlobEntityLocalService lazyBlobEntityLocalService;

	@BeanReference(type = LazyBlobEntityPersistence.class)
	protected LazyBlobEntityPersistence lazyBlobEntityPersistence;

	@ServiceReference(
		type = com.liferay.counter.kernel.service.CounterLocalService.class
	)
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@BeanReference(type = File.class)
	protected File _file;

	private static final InputStream _EMPTY_INPUT_STREAM =
		new UnsyncByteArrayInputStream(new byte[0]);

	private boolean _useTempFile;

	@ServiceReference(type = PersistedModelLocalServiceRegistry.class)
	protected PersistedModelLocalServiceRegistry
		persistedModelLocalServiceRegistry;

}