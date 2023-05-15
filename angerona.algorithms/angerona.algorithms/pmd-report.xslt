<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" />
    <xsl:strip-space elements="violation" />
    <xsl:template match="pmd">
        <xsl:variable name="total" select="count(//violation)"/>
PMD Scan: Total faults found = <xsl:value-of select="count(//violation)"/>
        <xsl:if test="$total &gt; 0">
                   Priority 1: <xsl:value-of select="count(//violation[@priority = 1])"/>
                   Priority 2: <xsl:value-of select="count(//violation[@priority = 2])"/>
                   Priority 3: <xsl:value-of select="count(//violation[@priority = 3])"/>
                   Priority 4: <xsl:value-of select="count(//violation[@priority = 4])"/>
                   Priority 5: <xsl:value-of select="count(//violation[@priority = 5])"/>      
====================================
</xsl:if>
        
        <xsl:for-each select="file">
            <xsl:sort data-type="number" order="descending" select="count(violation)"/>
            <xsl:variable name="filename" select="@name"/>
            <xsl:for-each select="violation">
                <xsl:variable name="fault" select="." />
                <xsl:value-of select="$filename"/> line <xsl:value-of disable-output-escaping="yes" select="@line"/>: <xsl:value-of disable-output-escaping="yes" select="normalize-space($fault)"/>.
</xsl:for-each>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>

