<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template match="/suggest">
		<xsl:text>"titre de la proposition";"commentaire";"date du commentaire";"type de commentaire";"date proposition";"score";"nombre de vote";nombre de commentaire";"type de proposition";""cat&#233;gorie"</xsl:text>
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="suggest-submits/suggest-submit"/>
	</xsl:template>
	<xsl:template match="/suggest/suggest-submits/suggest-submit">
	
	<xsl:for-each select="suggest-submit-comments/suggest-submit-comment">
				
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-title"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-comment-value"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-comment-date"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"commentaire de proposition"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../suggest-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
				<xsl:for-each select="suggest-submit-comments/suggest-submit-comment">
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-title"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-comment-value"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-comment-date"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"commentaire de commentaire"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../suggest-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
				</xsl:for-each >
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>