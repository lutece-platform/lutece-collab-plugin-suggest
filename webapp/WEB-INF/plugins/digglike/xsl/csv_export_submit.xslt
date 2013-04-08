<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template match="/digg">
		<xsl:text>"proposition";"date proposition";"score";"nombre de vote";"nombre de commentaire";"cat&#233;gorie"</xsl:text>
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="diggs-submit/digg-submit"/>
	</xsl:template>
	<xsl:template match="/digg/diggs-submit/digg-submit">
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-value"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
	</xsl:template>
</xsl:stylesheet>