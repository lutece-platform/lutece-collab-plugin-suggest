<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text"/>
<xsl:template match="/">
 <xsl:apply-templates select="digg/diggs-submit/digg-submit"/> 
</xsl:template>
<xsl:template match="digg-submit">
	<xsl:text>"</xsl:text>
			<xsl:value-of select="digg-submit-value"/> 
	<xsl:text>";</xsl:text>
     <xsl:text>&#10;</xsl:text>
</xsl:template>
 </xsl:stylesheet>