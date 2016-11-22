<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="suggestSubmitType" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestSubmitTypeJspBean" />


<% 
	suggestSubmitType.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE);
    response.sendRedirect( suggestSubmitType.doCreateSuggestSubmitType( request ) );
%>
