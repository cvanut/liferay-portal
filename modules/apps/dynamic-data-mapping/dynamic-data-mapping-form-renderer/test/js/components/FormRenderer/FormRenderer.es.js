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

import FormRenderer from '../../../../src/main/resources/META-INF/resources/js/components/FormRenderer/FormRenderer.es';
import * as FormSupport from '../../../../src/main/resources/META-INF/resources/js/components/FormRenderer/FormSupport.es';
import mockPages from '../../__mock__/mockPages.es';
import mockSuccessPage from '../../__mock__/mockSuccessPage.es';

const spritemap = 'icons.svg';
let component;
let pages = null;
let successPageSettings = null;

describe('FormRenderer', () => {
	beforeEach(() => {
		pages = JSON.parse(JSON.stringify(mockPages));
		successPageSettings = JSON.parse(JSON.stringify(mockSuccessPage));

		jest.useFakeTimers();
	});

	afterEach(() => {
		if (component) {
			component.dispose();
		}

		pages = null;
	});

	it('renders default markup with Success Page ative', () => {
		pages.push({contentRenderer: 'success'});
		component = new FormRenderer({
			pages,
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('renders a layout in wizard mode', () => {
		component = new FormRenderer({
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('renders a layout in pagination mode', () => {
		component = new FormRenderer({
			pages,
			paginationMode: 'paginated',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('renders a layout with pages in mode of list', () => {
		component = new FormRenderer({
			modeRenderer: 'list',
			pages,
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('renders a layout in editable mode', () => {
		component = new FormRenderer({
			editable: true,
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('renders a layout with disabled drag and drop', () => {
		component = new FormRenderer({
			dragAndDropDisabled: true,
			editable: true,
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component._dragAndDrop).toBeUndefined();
	});

	it('continues to propagate the fieldEdited event', () => {
		component = new FormRenderer({
			dragAndDropDisabled: true,
			editable: true,
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		const spy = jest.spyOn(component, 'emit');
		const {pageRenderer0} = component.refs;
		const mockEvent = jest.fn();

		pageRenderer0.emit('fieldEdited', mockEvent);

		expect(spy).toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith('fieldEdited', expect.anything());
	});

	it('continues to propagate the fieldClicked event', () => {
		component = new FormRenderer({
			dragAndDropDisabled: true,
			editable: true,
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		const spy = jest.spyOn(component, 'emit');
		const {pageRenderer0} = component.refs;
		const mockEvent = jest.fn();

		pageRenderer0.emit('fieldClicked', mockEvent);

		expect(spy).toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith('fieldClicked', expect.anything());
	});

	it('propagates field edit event', () => {
		component = new FormRenderer({
			editable: true,
			pages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		const spy = jest.spyOn(component, 'emit');

		component._handleFieldEdited();

		expect(spy).toHaveBeenCalled();
	});

	it('renders a layout with an empty field only in editable mode', () => {
		const columnIndex = 2;
		const fields = [
			{
				spritemap: 'icons.svg',
				type: 'option_multiple',
			},
		];
		const pageIndex = 0;
		const rowIndex = 1;

		const newmockPages = FormSupport.setColumnFields(
			pages,
			pageIndex,
			rowIndex,
			columnIndex,
			fields
		);

		component = new FormRenderer({
			editable: true,
			pages: newmockPages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});

	it('does not render the layout with the field empty in non-editable mode', () => {
		const columnIndex = 2;
		const fields = [
			{
				spritemap: 'icons.svg',
				type: 'option_multiple',
			},
		];
		const pageIndex = 0;
		const rowIndex = 1;

		const newmockPages = FormSupport.setColumnFields(
			pages,
			pageIndex,
			rowIndex,
			columnIndex,
			fields
		);

		component = new FormRenderer({
			editable: false,
			pages: newmockPages,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			spritemap,
			successPageSettings,
		});

		expect(component).toMatchSnapshot();
	});
});
