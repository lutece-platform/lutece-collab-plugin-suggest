<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="suggestSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestJspBean" />


<% 
	suggestSuggest.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE );
    response.sendRedirect( suggestSuggest.doRemoveSuggest( request ) );
%>
