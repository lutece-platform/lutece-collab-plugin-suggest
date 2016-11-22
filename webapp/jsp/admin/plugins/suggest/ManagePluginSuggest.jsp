<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="suggestManagePluginSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean" />

<% suggestManagePluginSuggest.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE ); %>
<%= suggestManagePluginSuggest.getManageSuggest( request ) %>

<%@ include file="../../AdminFooter.jsp" %>