<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="digglikeDefaultMessage" scope="session" class="fr.paris.lutece.plugins.digglike.web.DefaultMessageJspBean" />

<%
	digglikeDefaultMessage.init( request,fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE);
%>
<%= digglikeDefaultMessage.getManageDefaultMessage( request ) %>

<%@ include file="../../AdminFooter.jsp" %>