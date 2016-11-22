<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template match="/suggest">
		<xsl:apply-templates select="suggest-entries/entry"/>
		<xsl:text>;"date proposition";"score";"nombre de vote";"nombre de commentaire";"Type de proposition";"cat&#233;gorie"</xsl:text>
		<xsl:text>&#10;</xsl:text>
		<xsl:apply-templates select="suggest-submits/suggest-submit"/>
	</xsl:template>
	
	<xsl:template match="suggest-submit">
				<xsl:apply-templates select="suggest-submit-responses/response"/>
				<xsl:text>;"</xsl:text><xsl:value-of select="suggest-submit-date-response"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-score"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-number-vote"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-number-comment"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-type"/><xsl:text>"</xsl:text><xsl:text>;</xsl:text>
				<xsl:text>"</xsl:text><xsl:value-of select="suggest-submit-category"/><xsl:text>"</xsl:text> <xsl:text>&#10;</xsl:text>
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