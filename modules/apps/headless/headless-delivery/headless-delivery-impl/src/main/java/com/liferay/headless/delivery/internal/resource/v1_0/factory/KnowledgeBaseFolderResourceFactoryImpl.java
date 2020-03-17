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

package com.liferay.headless.delivery.internal.resource.v1_0.factory;

import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseFolderResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	immediate = true, service = KnowledgeBaseFolderResource.Factory.class
)
public class KnowledgeBaseFolderResourceFactoryImpl
	implements KnowledgeBaseFolderResource.Factory {

	@Override
	public KnowledgeBaseFolderResource.Builder create() {
		return new KnowledgeBaseFolderResource.Builder() {

			@Override
			public KnowledgeBaseFolderResource build() {
				if (_user == null) {
					throw new IllegalArgumentException("User is not set");
				}

				return (KnowledgeBaseFolderResource)ProxyUtil.newProxyInstance(
					KnowledgeBaseFolderResource.class.getClassLoader(),
					new Class<?>[] {KnowledgeBaseFolderResource.class},
					(proxy, method, arguments) -> _invoke(
						method, arguments, _checkPermissions, _user));
			}

			@Override
			public KnowledgeBaseFolderResource.Builder checkPermissions(
				boolean checkPermissions) {

				_checkPermissions = checkPermissions;

				return this;
			}

			@Override
			public KnowledgeBaseFolderResource.Builder user(User user) {
				_user = user;

				return this;
			}

			private boolean _checkPermissions = true;
			private User _user;

		};
	}

	@Activate
	protected void activate() {
		KnowledgeBaseFolderResource.FactoryHolder.factory = this;
	}

	@Deactivate
	protected void deactivate() {
		KnowledgeBaseFolderResource.FactoryHolder.factory = null;
	}

	private Object _invoke(
			Method method, Object[] arguments, boolean checkPermissions,
			User user)
		throws Throwable {

		String name = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (checkPermissions) {
			PermissionThreadLocal.setPermissionChecker(
				_defaultPermissionCheckerFactory.create(user));
		}
		else {
			PermissionThreadLocal.setPermissionChecker(
				_liberalPermissionCheckerFactory.create(user));
		}

		KnowledgeBaseFolderResource knowledgeBaseFolderResource =
			_componentServiceObjects.getService();

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		knowledgeBaseFolderResource.setContextCompany(company);

		knowledgeBaseFolderResource.setContextUser(user);

		try {
			return method.invoke(knowledgeBaseFolderResource, arguments);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getTargetException();
		}
		finally {
			_componentServiceObjects.ungetService(knowledgeBaseFolderResource);

			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KnowledgeBaseFolderResource>
		_componentServiceObjects;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference(target = "(permission.checker.type=liberal)")
	private PermissionCheckerFactory _liberalPermissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}