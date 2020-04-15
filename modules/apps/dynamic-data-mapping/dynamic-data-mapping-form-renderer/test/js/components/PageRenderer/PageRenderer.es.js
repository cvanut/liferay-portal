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

import '../../../../src/main/resources/META-INF/resources/js/components/SuccessPage/SuccessPage.es';
import PageRenderer from '../../../../src/main/resources/META-INF/resources/js/components/PageRenderer/PageRenderer.es';
import mockPagesEmpty from '../../__mock__/mockPageEmpty.es';
import mockSuccessPage from '../../__mock__/mockSuccessPage.es';
import withContextMock from '../../__mock__/withContextMock.es';

const spritemap = 'icons.svg';
let component;
let pagesEmpty = null;
let successPageSettings = null;

describe('PageRenderer', () => {
	beforeEach(() => {
		jest.useFakeTimers();
		pagesEmpty = JSON.parse(JSON.stringify(mockPagesEmpty));
		successPageSettings = JSON.parse(JSON.stringify(mockSuccessPage));

		jest.useFakeTimers();
	});

	afterEach(() => {
		if (component) {
			component.dispose();
		}
	});

	it('creates the defaut markup when success page is enabled', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push({contentRenderer: 'success'});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[0],
			pageIndex: 0,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 2,
			view: 'formBuilder',
		});

		expect(component).toMatchSnapshot();
	});

	it('creates the defaut markup with button to add success page if success page is disabled', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push(pagesEmpty[0]);
		successPageSettings.enabled = false;

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[1],
			pageIndex: 1,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 2,
			view: 'formBuilder',
		});

		expect(component.refs.successPage).not.toBeNull();
	});

	it('shows the new page button on the bottom of each page', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push({contentRenderer: 'success'});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[0],
			pageIndex: 0,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 2,
			view: 'formBuilder',
		});

		const newPageButton = component.refs.newPage0;

		expect(newPageButton).not.toBeNull();
	});

	it('adds a new page by clicking the New Page button', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push({contentRenderer: 'success'});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[0],
			pageIndex: 0,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 2,
			view: 'formBuilder',
		});

		component._handleAddPageClicked();

		expect(component.context.dispatch).toHaveBeenCalledWith('pageAdded', {
			pageIndex: 0,
		});
	});

	it('removes a page by clicking on kebab (three dots) menu on the right side of each page', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push(pagesEmpty[0]);
		pagesEmpty.push({contentRenderer: 'success'});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[1],
			pageIndex: 1,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 3,
			view: 'formBuilder',
		});

		const data = {
			item: {
				settingsItem: 'remove-page',
			},
		};

		component._handleDropdownItemClicked({data});

		expect(component.context.dispatch).toHaveBeenCalledWith(
			'pageDeleted',
			1
		);
	});

	it('removes the success page by clicking on kebab (three dots) menu on the right side of each page', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push(pagesEmpty[0]);
		pagesEmpty.push({
			contentRenderer: 'success',
			rows: [],
			successPageSettings,
		});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[2],
			pageIndex: 2,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 3,
			view: 'formBuilder',
		});

		const data = {
			item: {
				settingsItem: 'remove-page',
			},
		};

		component._handleDropdownItemClicked({data});

		expect(component.context.dispatch).toHaveBeenCalledWith(
			'activePageUpdated',
			2
		);
	});

	it('resets a page by clicking on kebab (three dots) menu on the right side of each page', () => {
		const PageRendererWithContextMock = withContextMock(PageRenderer);

		pagesEmpty.push(pagesEmpty[0]);
		pagesEmpty.push({contentRenderer: 'success'});

		component = new PageRendererWithContextMock({
			editable: true,
			editingLanguageId: 'en_US',
			page: pagesEmpty[1],
			pageIndex: 1,
			pages: pagesEmpty,
			paginationMode: 'wizard',
			portletNamespace: 'portletNamespace',
			showSubmitButton: false,
			spritemap,
			submitLabel: 'Submit',
			successPageSettings,
			total: 3,
			view: 'formBuilder',
		});

		const data = {
			item: {
				settingsItem: 'reset-page',
			},
		};

		component._handleDropdownItemClicked({data});

		expect(component.context.dispatch).toHaveBeenCalledWith('pageReset', {
			pageIndex: 1,
		});
	});
});
