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

package com.liferay.polls.internal.exportimport.data.handler;

import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.polls.constants.PollsConstants;
import com.liferay.polls.constants.PollsPortletKeys;
import com.liferay.polls.model.PollsChoice;
import com.liferay.polls.model.PollsQuestion;
import com.liferay.polls.model.PollsVote;
import com.liferay.polls.service.PollsChoiceLocalService;
import com.liferay.polls.service.PollsQuestionLocalService;
import com.liferay.polls.service.PollsVoteLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Farache
 * @author Marcellus Tavares
 * @author Máté Thurzó
 */
@Component(
	immediate = true, property = "javax.portlet.name=" + PollsPortletKeys.POLLS,
	service = PortletDataHandler.class
)
public class PollsPortletDataHandler extends BasePortletDataHandler {

	public static final String[] CLASS_NAMES = {
		PollsChoice.class.getName(), PollsQuestion.class.getName(),
		PollsVote.class.getName()
	};

	public static final String NAMESPACE = "polls";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataLocalized(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(PollsChoice.class),
			new StagedModelType(PollsQuestion.class),
			new StagedModelType(PollsVote.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "choices", true, false, null,
				PollsChoice.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "questions", true, false, null,
				PollsQuestion.class.getName()),
			new PortletDataHandlerBoolean(
				NAMESPACE, "votes", true, false, null,
				PollsVote.class.getName()));
		setStagingControls(getExportControls());
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				PollsPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_pollsQuestionLocalService.deleteQuestions(
			portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			final PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		portletDataContext.addPortletPermissions(PollsConstants.RESOURCE_NAME);

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (portletDataContext.getBooleanParameter(
				PollsPortletDataHandler.NAMESPACE, "questions")) {

			ActionableDynamicQuery questionActionableDynamicQuery =
				_pollsQuestionLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			questionActionableDynamicQuery.performActions();

			ActionableDynamicQuery choiceActionableDynamicQuery =
				_pollsChoiceLocalService.getExportActionableDynamicQuery(
					portletDataContext);

			choiceActionableDynamicQuery.performActions();

			if (portletDataContext.getBooleanParameter(
					PollsPortletDataHandler.NAMESPACE, "votes")) {

				ActionableDynamicQuery voteActionableDynamicQuery =
					_pollsVoteLocalService.getExportActionableDynamicQuery(
						portletDataContext);

				voteActionableDynamicQuery.performActions();
			}
		}

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		portletDataContext.importPortletPermissions(
			PollsConstants.RESOURCE_NAME);

		if (portletDataContext.getBooleanParameter(
				PollsPortletDataHandler.NAMESPACE, "questions")) {

			Element questionsElement =
				portletDataContext.getImportDataGroupElement(
					PollsQuestion.class);

			List<Element> questionElements = questionsElement.elements();

			for (Element questionElement : questionElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, questionElement);
			}

			Element choicesElement =
				portletDataContext.getImportDataGroupElement(PollsChoice.class);

			List<Element> choiceElements = choicesElement.elements();

			for (Element choiceElement : choiceElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, choiceElement);
			}
		}

		if (portletDataContext.getBooleanParameter(NAMESPACE, "votes")) {
			Element votesElement = portletDataContext.getImportDataGroupElement(
				PollsVote.class);

			List<Element> voteElements = votesElement.elements();

			for (Element voteElement : voteElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					portletDataContext, voteElement);
			}
		}

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		if (ExportImportDateUtil.isRangeFromLastPublishDate(
				portletDataContext)) {

			_staging.populateLastPublishDateCounts(
				portletDataContext,
				new StagedModelType[] {
					new StagedModelType(PollsChoice.class.getName()),
					new StagedModelType(PollsQuestion.class.getName()),
					new StagedModelType(PollsVote.class.getName())
				});

			return;
		}

		ActionableDynamicQuery choiceActionableDynamicQuery =
			_pollsChoiceLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		choiceActionableDynamicQuery.performCount();

		ActionableDynamicQuery questionActionableDynamicQuery =
			_pollsQuestionLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		questionActionableDynamicQuery.performCount();

		ActionableDynamicQuery voteActionableDynamicQuery =
			_pollsVoteLocalService.getExportActionableDynamicQuery(
				portletDataContext);

		voteActionableDynamicQuery.performCount();
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	@Reference(unbind = "-")
	protected void setPollsChoiceLocalService(
		PollsChoiceLocalService pollsChoiceLocalService) {

		_pollsChoiceLocalService = pollsChoiceLocalService;
	}

	@Reference(unbind = "-")
	protected void setPollsQuestionLocalService(
		PollsQuestionLocalService pollsQuestionLocalService) {

		_pollsQuestionLocalService = pollsQuestionLocalService;
	}

	@Reference(unbind = "-")
	protected void setPollsVoteLocalService(
		PollsVoteLocalService pollsVoteLocalService) {

		_pollsVoteLocalService = pollsVoteLocalService;
	}

	private PollsChoiceLocalService _pollsChoiceLocalService;
	private PollsQuestionLocalService _pollsQuestionLocalService;
	private PollsVoteLocalService _pollsVoteLocalService;

	@Reference
	private Staging _staging;

}