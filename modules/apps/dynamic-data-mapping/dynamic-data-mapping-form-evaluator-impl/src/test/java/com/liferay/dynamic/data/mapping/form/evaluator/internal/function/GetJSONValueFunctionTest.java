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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class GetJSONValueFunctionTest {

	@Before
	public void setUp() {
		_setUpJSONFactoryUtil();
	}

	@Test
	public void testApply() {
		String jsonObjectString = JSONUtil.put(
			"test", "test"
		).toString();

		JSONObject jsonObject = _getJSONValueFunction.apply(jsonObjectString);

		Assert.assertEquals(jsonObjectString, jsonObject.toString());
	}

	@Test
	public void testApplyEmptyValue() {
		JSONObject jsonObject = _getJSONValueFunction.apply(StringPool.BLANK);

		Assert.assertEquals(0, jsonObject.length());
	}

	@Test
	public void testApplyNullValue() {
		JSONObject jsonObject = _getJSONValueFunction.apply(null);

		Assert.assertEquals(0, jsonObject.length());
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private final GetJSONValueFunction _getJSONValueFunction =
		new GetJSONValueFunction();

}