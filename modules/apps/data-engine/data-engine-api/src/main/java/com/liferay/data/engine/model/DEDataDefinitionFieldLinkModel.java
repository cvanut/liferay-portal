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

package com.liferay.data.engine.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.model.AttachedModel;
import com.liferay.portal.kernel.model.BaseModel;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the DEDataDefinitionFieldLink service. Represents a row in the &quot;DEDataDefinitionFieldLink&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkImpl</code>.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DEDataDefinitionFieldLink
 * @generated
 */
@ProviderType
public interface DEDataDefinitionFieldLinkModel
	extends AttachedModel, BaseModel<DEDataDefinitionFieldLink> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a de data definition field link model instance should use the {@link DEDataDefinitionFieldLink} interface instead.
	 */

	/**
	 * Returns the primary key of this de data definition field link.
	 *
	 * @return the primary key of this de data definition field link
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this de data definition field link.
	 *
	 * @param primaryKey the primary key of this de data definition field link
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the uuid of this de data definition field link.
	 *
	 * @return the uuid of this de data definition field link
	 */
	@AutoEscape
	public String getUuid();

	/**
	 * Sets the uuid of this de data definition field link.
	 *
	 * @param uuid the uuid of this de data definition field link
	 */
	public void setUuid(String uuid);

	/**
	 * Returns the de data definition field link ID of this de data definition field link.
	 *
	 * @return the de data definition field link ID of this de data definition field link
	 */
	public long getDeDataDefinitionFieldLinkId();

	/**
	 * Sets the de data definition field link ID of this de data definition field link.
	 *
	 * @param deDataDefinitionFieldLinkId the de data definition field link ID of this de data definition field link
	 */
	public void setDeDataDefinitionFieldLinkId(
		long deDataDefinitionFieldLinkId);

	/**
	 * Returns the group ID of this de data definition field link.
	 *
	 * @return the group ID of this de data definition field link
	 */
	public long getGroupId();

	/**
	 * Sets the group ID of this de data definition field link.
	 *
	 * @param groupId the group ID of this de data definition field link
	 */
	public void setGroupId(long groupId);

	/**
	 * Returns the fully qualified class name of this de data definition field link.
	 *
	 * @return the fully qualified class name of this de data definition field link
	 */
	@Override
	public String getClassName();

	public void setClassName(String className);

	/**
	 * Returns the class name ID of this de data definition field link.
	 *
	 * @return the class name ID of this de data definition field link
	 */
	@Override
	public long getClassNameId();

	/**
	 * Sets the class name ID of this de data definition field link.
	 *
	 * @param classNameId the class name ID of this de data definition field link
	 */
	@Override
	public void setClassNameId(long classNameId);

	/**
	 * Returns the class pk of this de data definition field link.
	 *
	 * @return the class pk of this de data definition field link
	 */
	@Override
	public long getClassPK();

	/**
	 * Sets the class pk of this de data definition field link.
	 *
	 * @param classPK the class pk of this de data definition field link
	 */
	@Override
	public void setClassPK(long classPK);

	/**
	 * Returns the ddm structure ID of this de data definition field link.
	 *
	 * @return the ddm structure ID of this de data definition field link
	 */
	public long getDdmStructureId();

	/**
	 * Sets the ddm structure ID of this de data definition field link.
	 *
	 * @param ddmStructureId the ddm structure ID of this de data definition field link
	 */
	public void setDdmStructureId(long ddmStructureId);

	/**
	 * Returns the field name of this de data definition field link.
	 *
	 * @return the field name of this de data definition field link
	 */
	public long getFieldName();

	/**
	 * Sets the field name of this de data definition field link.
	 *
	 * @param fieldName the field name of this de data definition field link
	 */
	public void setFieldName(long fieldName);

}