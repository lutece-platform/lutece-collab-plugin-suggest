<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="digglikeManagePluginDigglike" scope="session" class="fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean" />

<% digglikeManagePluginDigglike.init( request, fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE ); %>
<%= digglikeManagePluginDigglike.getManageDigg( request ) %>

<%@ include file="../../AdminFooter.jsp" %>