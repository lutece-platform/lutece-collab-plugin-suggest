<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="ISO-8859-1" indent="yes" cdata-section-elements="digg-submit-value digg-submit-category"/>
<xsl:template match="/">
 <xsl:apply-templates select="digg"/> 
</xsl:template>

<xsl:template match="digg">
	
	<digg>
		<digg-title>
			<xsl:value-of select="digg-title"/>
		</digg-title>
		<digg-submits>
			<xsl:apply-templates select="digg-submits/digg-submit"/> 
		</digg-submits>
		<digg-entries>
			<xsl:apply-templates select="digg-entries/entry"/> 
		</digg-entries>
		
	</digg>	
</xsl:template>

<xsl:template match="digg-submit">
	<digg-submit>
		
		<digg-submit-title>
			<xsl:value-of select="digg-submit-title"/>
		</digg-submit-title>
		
		
		<digg-submit-date-response>
			<xsl:value-of select="digg-submit-date-response"/>
		</digg-submit-date-response>
		
		<digg-submit-score>
			<xsl:value-of select="digg-submit-score"/>
	</digg-submit-score>
		
		<digg-submit-number-vote>
			<xsl:value-of select="digg-submit-number-vote"/>
	</digg-submit-number-vote>
		
		<digg-submit-number-comment>
			<xsl:value-of select="digg-submit-number-comment"/>
		</digg-submit-number-comment>
		
		<digg-submit-category>
			<xsl:value-of select="digg-submit-category"/>
		</digg-submit-category>
		<digg-submit-type>
			<xsl:value-of select="digg-submit-type"/>
		</digg-submit-type>
		<digg-submit-responses>
			<xsl:apply-templates select="digg-submit-responses/response"/>
		</digg-submit-responses>
	</digg-submit>
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