<%--
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
--%>

<%@ page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletDisplayContext" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-theme:defineObjects />

<%
SearchBarPortletDisplayContext searchBarPortletDisplayContext = (SearchBarPortletDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<c:choose>
	<c:when test="<%= searchBarPortletDisplayContext.isDestinationUnreachable() %>">
		<div class="alert alert-info text-center">
			<liferay-ui:message key="this-search-bar-is-not-visible-to-users-yet" />

			<aui:a href="javascript:;" onClick="<%= portletDisplay.getURLConfigurationJS() %>"><liferay-ui:message key="set-up-its-destination-to-make-it-visible" /></aui:a>
		</div>
	</c:when>
	<c:otherwise>
		<aui:form action="<%= searchBarPortletDisplayContext.getSearchURL() %>" method="get" name="fm">
			<aui:fieldset id="searchContainer">
				<div class="input-group search-bar">
					<aui:field-wrapper inlineField="<%= true %>">
						<aui:input autoFocus="<%= true %>" cssClass="search-bar-keywords-input search-input" data-qa-id="searchInput" label="" name="<%= searchBarPortletDisplayContext.getKeywordsParameterName() %>" placeholder="search-..." title="search" type="text" useNamespace="<%= false %>" value="<%= searchBarPortletDisplayContext.getKeywords() %>" />
					</aui:field-wrapper>

					<c:choose>
						<c:when test="<%= searchBarPortletDisplayContext.isLetTheUserChooseTheSearchScope() %>">
							<aui:field-wrapper inlineField="<%= true %>">
								<aui:select cssClass="search-bar-scope-select search-select" label="" name="<%= searchBarPortletDisplayContext.getScopeParameterName() %>" title="scope" useNamespace="<%= false %>">
									<c:if test="<%= searchBarPortletDisplayContext.isAvailableEverythingSearchScope() %>">
										<aui:option label="all-sites" selected="<%= searchBarPortletDisplayContext.isSelectedEverythingSearchScope() %>" value="<%= searchBarPortletDisplayContext.getEverythingSearchScopeParameterString() %>" />
									</c:if>

									<aui:option label="this-site" selected="<%= searchBarPortletDisplayContext.isSelectedCurrentSiteSearchScope() %>" value="<%= searchBarPortletDisplayContext.getCurrentSiteSearchScopeParameterString() %>" />
								</aui:select>
							</aui:field-wrapper>
						</c:when>
						<c:otherwise>
							<aui:input name="<%= searchBarPortletDisplayContext.getScopeParameterName() %>" type="hidden" value="<%= searchBarPortletDisplayContext.getScopeParameterValue() %>" />
						</c:otherwise>
					</c:choose>

					<aui:field-wrapper cssClass="input-group-btn search-field" inlineField="<%= true %>">
						<liferay-ui:icon
							cssClass="icon-monospaced search-bar-search-button"
							icon="search"
							markupView="lexicon"
						/>
					</aui:field-wrapper>
				</div>
			</aui:fieldset>
		</aui:form>

		<aui:script use="liferay-search-bar">
			new Liferay.Search.SearchBar(A.one('#<portlet:namespace/>fm'));
		</aui:script>
	</c:otherwise>
</c:choose>