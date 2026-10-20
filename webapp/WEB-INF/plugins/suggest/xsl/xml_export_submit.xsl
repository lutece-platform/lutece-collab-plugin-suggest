<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="suggest-submit-value suggest-submit-category"/>
<xsl:template match="/">
 <xsl:apply-templates select="suggest"/> 
</xsl:template>

<xsl:template match="suggest">
	
	<suggest>
		<suggest-title>
			<xsl:value-of select="suggest-title"/>
		</suggest-title>
		<suggest-submits>
			<xsl:apply-templates select="suggest-submits/suggest-submit"/> 
		</suggest-submits>
		<suggest-entries>
			<xsl:apply-templates select="suggest-entries/entry"/> 
		</suggest-entries>
		
	</suggest>	
</xsl:template>

<xsl:template match="suggest-submit">
	<suggest-submit>
		
		<suggest-submit-title>
			<xsl:value-of select="suggest-submit-title"/>
		</suggest-submit-title>
		
		
		<suggest-submit-date-response>
			<xsl:value-of select="suggest-submit-date-response"/>
		</suggest-submit-date-response>
		
		<suggest-submit-score>
			<xsl:value-of select="suggest-submit-score"/>
	</suggest-submit-score>
		
		<suggest-submit-number-vote>
			<xsl:value-of select="suggest-submit-number-vote"/>
	</suggest-submit-number-vote>
		
		<suggest-submit-number-comment>
			<xsl:value-of select="suggest-submit-number-comment"/>
		</suggest-submit-number-comment>
		
		<suggest-submit-category>
			<xsl:value-of select="suggest-submit-category"/>
		</suggest-submit-category>
		<suggest-submit-type>
			<xsl:value-of select="suggest-submit-type"/>
		</suggest-submit-type>
		<suggest-submit-responses>
			<xsl:apply-templates select="suggest-submit-responses/response"/>
		</suggest-submit-responses>
	</suggest-submit>
</xsl:template>


	<xsl:template match="entry">
		<entry>
			<title>
				<xsl:value-of select="title"/>
			</title>
			<id>
				<xsl:value-of select="id"/>
			</id>
			<type-id>
				<xsl:value-of select="type-id"/>
			</type-id>
		</entry>
	</xsl:template>

	<xsl:template match="response">
	 	<response>
	 		<response-value>
				<xsl:value-of select="response-value"/>
			</response-value>
			<xsl:apply-templates select="entry"/>
		</response>
	</xsl:template>

</xsl:stylesheet>