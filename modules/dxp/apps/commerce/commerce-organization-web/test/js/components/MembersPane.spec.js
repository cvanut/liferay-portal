/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import MembersPane from 'components/MembersPane';
import {shallow} from 'enzyme';
import React from 'react';

jest.mock('utils/utils.es', () => {
	return {
		bindAll: jest.fn(),
		callApi: jest
			.fn()
			.mockImplementationOnce((params) => params)
			.mockImplementationOnce((params) => params),
	};
});

const apiParams = {
	baseURL: 'http://someUrl.com',
	id: 42,
};

const apiParamsMembers = {
	path: 'users',
	queryParams: {
		page: 1,
		pageSize: 100,
	},
	...apiParams,
};

describe('MembersPane', () => {
	let wrapper, component, Utils;

	beforeEach(() => {
		wrapper = shallow(<MembersPane />);
		component = wrapper.instance();
		Utils = require('utils/utils.es');
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	describe('Instantiation', () => {
		it('has the correct initial state on render', () => {
			const initialState = {
				isLoading: false,
				listBy: 'user',
				searchQuery: '',
			};

			expect(wrapper.state()).toMatchObject(initialState);
		});

		it('renders all the child components correctly', () => {
			expect(wrapper.children('PaneHeader').length).toEqual(1);
			expect(wrapper.children('MembersList').length).toEqual(1);
		});

		it('calls the API on componentDidMount to fetch the list of members', () => {
			const someId = 42,
				listBy = 'user';

			jest.spyOn(component, 'handleUpdate');

			wrapper.setProps({apiURL: 'http://someUrl.com', id: 42});

			return component.$didUpdate.then(() => {
				expect(component.handleUpdate).toHaveBeenCalledWith(
					someId,
					listBy
				);
				expect(component.handleUpdate).toHaveBeenCalledTimes(1);
				expect(Utils.callApi.mock.calls[2][0]).toMatchObject(apiParams);
				expect(Utils.callApi.mock.calls[3][0]).toMatchObject(
					apiParamsMembers
				);
			});
		});
	});

	describe('handleListBy', () => {
		it('sets the state with the correct list by mode', () => {
			component.handleListBy('someListBy');

			expect(wrapper.state()).toMatchObject({listBy: 'someListBy'});
			expect(
				wrapper.debug().match(/listBy="someListBy"/g).length
			).toEqual(2);
		});
	});

	describe('componentDidUpdate calls the API IFF', () => {
		it('the organization Id prop has changed', () => {
			jest.spyOn(component, 'handleUpdate');

			wrapper.setProps({id: 100});

			expect(component.handleUpdate).toHaveBeenCalled();
		});

		it('the listBy state has changed', () => {
			jest.spyOn(component, 'handleUpdate');
			wrapper.setState({listBy: 'account'});

			expect(wrapper.state()).toMatchObject({listBy: 'account'});
			expect(component.handleUpdate).toHaveBeenCalled();
		});
	});
});
