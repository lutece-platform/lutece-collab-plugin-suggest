<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template match="/digg">
		<xsl:apply-templates select="digg-entries/entry"/>
		<xsl:text>;"date proposition";"score";"nombre de vote";"nombre de commentaire";"Type de proposition";"cat&#233;gorie"</xsl:text>
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="digg-submits/digg-submit"/>
	</xsl:template>
	
	<xsl:template match="digg-submit">
				<xsl:apply-templates select="digg-submit-responses/response"/>
				<xsl:text>;"</xsl:text><xsl:value-of select="digg-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="digg-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
	</xsl:template>
	
	
	<xsl:template match="entry">
		<xsl:text>"</xsl:text>
		<xsl:value-of select="title"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	
	
	<xsl:template match="response">
	 <xsl:text>"</xsl:text>
		<xsl:value-of select="response-value"/>
		<xsl:text>"</xsl:text>
		<xsl:if test="position()!=last()">
			<xsl:text>;</xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>