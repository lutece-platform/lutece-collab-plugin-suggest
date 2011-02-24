<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="digglikeDefaultMessage" scope="session" class="fr.paris.lutece.plugins.digglike.web.DefaultMessageJspBean" />


<% 
	digglikeDefaultMessage.init( request, fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE);
    response.sendRedirect( digglikeDefaultMessage.doModifyDefaultMessage( request ) );
%>
