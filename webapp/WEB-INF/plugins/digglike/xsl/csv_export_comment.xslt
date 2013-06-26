<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template match="/digg">
		<xsl:text>"commentaire";"date du commentaire";"type de commentaire";"titre de la proposition";"date proposition";"score";"nombre de vote";nombre de commentaire";"type de proposition";""cat&#233;gorie"</xsl:text>
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="digg-submits/digg-submit"/>
	</xsl:template>
	<xsl:template match="/digg/digg-submits/digg-submit">
	
	<xsl:for-each select="digg-submit-comments/digg-submit-comment">
	
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-comment-value"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-comment-date"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"commentaire de proposition"</xsl:text><xsl:text>;</xsl:text>
				
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-title"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="../../digg-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
				<xsl:for-each select="digg-submit-comments/digg-submit-comment">
					<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-comment-value"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-comment-date"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"commentaire de commentaire"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-title"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
					<xsl:text>"</xsl:text><xsl:value-of select="../../../../digg-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
				</xsl:for-each >
	</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>