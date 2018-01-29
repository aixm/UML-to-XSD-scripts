<!--====================================================================-->
<!-- XSLT Script Developped by 2GETHER for EUROCONTROL -->
<!-- Released:  04-dec-2013 -->
<!-- Revision:  1.0 -->
<!-- AIXM 5.1.1/5.2 Transformation script -->
<!--     
- The purpose of this script is to transform an AIXM 5.1.1 or 5.2 xml Schema to reorder properties on an AIXM 5.1 basis.    
- This script is complementary to Entreprise Architect export scripts.    
- REQUIREMENTS :         
		- A file named AIXM_Features-5.1.xsd containing the AIXM-5.1 xml schema must be placed in the same folder        
		- This script is designed for Xalan engine. Any other XSLT engine may not produce the same xml output (neither formatting nor content)-->
<!--    Contacts :    - EUROCONTROL : eduard.porosnicu@eurocontrol.int    - 2GETHER : tiv@pulsar.be--><!--====================================================================-->
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:aixm="http://www.aixm.aero/schema/5.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xalan="http://xml.apache.org/xslt">	

<!-- OUTPUT Definition, Xalan specific -->	
<xsl:output method="xml" encoding="utf-8" indent="yes" omit-xml-declaration="no" doctype-public="" xalan:indent-amount="4"/>	<!-- Use of strip-space / disabled for Xalan -->	
<!-- <xsl:strip-space  elements="*"/> -->	

<!-- Template for xsd:schema root -->	
<xsl:template match="xsd:schema">		
	<xsl:copy>			
		<xsl:apply-templates select="text() | @* | node()"/>		
	</xsl:copy>	
</xsl:template>	

<!-- Template for 'others' nodes -->	
<xsl:template match="*">		
	<xsl:copy>			
		<xsl:apply-templates select="* | text() | @*"/>		
	</xsl:copy>	
</xsl:template>	

<!-- Template for attributes -->	
	<xsl:template match="@*">		
	<xsl:copy/>	
</xsl:template>	

<!-- Template for comments -->	
	<xsl:template match="comment()">		
	<xsl:copy/>		
	<!-- Workaround for missing line-feed -->		
	<xsl:text>&#10;</xsl:text>	
</xsl:template>	

<!-- Template for properties -->	
<xsl:template match="xsd:group[substring(@name, string-length(@name) - string-length('PropertyGroup') + 1) = 'PropertyGroup']/xsd:sequence | //xsd:sequence/choice">
	<!-- Define variables -->		
	<xsl:variable name="parentName" select="string(../@name)"/>		
	<!-- Reference group of nodes from AIXM 5.1 / Needs AIXM_Features-5.1.xsd in the same folder -->		
	<xsl:variable name="refAttributes" select="document('AIXM_Features-5.1.xsd')//xsd:group[@name = $parentName]/xsd:sequence/*[not(substring(@name, string-length(@name) - string-length('PropertyType') + 1) = 'PropertyType')]"/>		
	<xsl:variable name="refRelationShips" select="document('AIXM_Features-5.1.xsd')//xsd:group[@name = $parentName]/xsd:sequence/*[substring(@name, string-length(@name) - string-length('PropertyType') + 1) = 'PropertyType']"/>		
	<!-- Local group of nodes from AIXM 5.1.1 / 5.2 -->		
	<xsl:variable name="localAttributes" select="./*[not(substring(@name, string-length(@name) - string-length('PropertyType') + 1) = 'PropertyType')]"/>		
	<xsl:variable name="localRelationShips" select="./*[substring(@name, string-length(@name) - string-length('PropertyType') + 1) = 'PropertyType']"/>		
	<xsl:choose>			
		<!-- if node is empty, command an auto-closed-node copy -->			
		<xsl:when test="count(./*) = 0">				
			<xsl:copy/>			
		</xsl:when>			
		<xsl:otherwise>				
			<xsl:copy>					
				<xsl:if test="name() = 'sequence'">						
					<!-- Workaround for missing line-feed & tabs -->						
					<xsl:text>&#10;&#09;&#09;&#09;</xsl:text>					
				</xsl:if>					
				<!-- Call sorted copy of Attributes -->					
				<xsl:call-template name="groupNodes">						
					<xsl:with-param name="refNodes" select="$refAttributes"/>						
					<xsl:with-param name="localNodes" select="$localAttributes"/>					
				</xsl:call-template>					
				<!-- Call sorted copy of Relationships -->					
				<xsl:call-template name="groupNodes">						
					<xsl:with-param name="refNodes" select="$refRelationShips"/>						
					<xsl:with-param name="localNodes" select="$localRelationShips"/>					
				</xsl:call-template>				
			</xsl:copy>			
		</xsl:otherwise>		
	</xsl:choose>	
</xsl:template>	

<!-- Named template. Definition of generic sorted copy algorithm -->	
<xsl:template name="groupNodes">		
	<xsl:param name="refNodes"/>		
	<xsl:param name="localNodes"/>		
	<!-- Reorder from existing list of nodes from AIXM 5.1 -->		
	<xsl:for-each select="$refNodes">			
		<xsl:choose>				
			<xsl:when test="name() = 'element'">					
				<xsl:variable name="nodeName" select="string(./@name)"/>					
					<xsl:apply-templates select="$localNodes[@name = $nodeName]"/>				
				</xsl:when>				
				<xsl:when test="name() = 'choice'">					
					<!-- ASSUMPTIONS :                                 
						- <element> children of the same <choice> MUST have their names beginning by the same prefix
						- 2 <choice> in the same PropertyGroup SHOULD NEVER have the same children name's prefix -->
					<xsl:variable name="firstChildName" select="string(./xsd:element[1]/@name)"/>					
					<xsl:variable name="nodeChildrenPrefix" select="substring-before($firstChildName, '_')"/>					
					<xsl:apply-templates select="$localNodes[child::xsd:element[starts-with(@name, $nodeChildrenPrefix)]]"/>
				</xsl:when>				
				<xsl:otherwise>					
					<xsl:variable name="nodeType" select="name()"/>					
					<xsl:apply-templates select="$localNodes[name() = $nodeType]"/>				
				</xsl:otherwise>			
			</xsl:choose>		
		</xsl:for-each>		
		<!-- Add new nodes -->		
		<xsl:for-each select="$localNodes">			
			<xsl:choose>				
				<xsl:when test="name() = 'element'">					
					<xsl:variable name="nodeName" select="string(./@name)"/>					
					<xsl:if test="not($refNodes[@name = $nodeName])">						
						<xsl:apply-templates select="."/>					
					</xsl:if>				
				</xsl:when>				
				<xsl:when test="name() = 'choice'">					
					<!-- ASSUMPTIONS :                                 
						- <element> children of the same <choice> MUST have their names beginning by the same prefix 
						- 2 <choice> in the same PropertyGroup SHOULD NEVER have the same children name's prefix -->
					<xsl:variable name="firstChildName" select="string(./xsd:element[1]/@name)"/>					
					<xsl:variable name="nodeChildrenPrefix" select="substring-before($firstChildName, '_')"/>					
					<xsl:if test="not($refNodes[child::xsd:element[starts-with(@name, $nodeChildrenPrefix)]])">
						<xsl:apply-templates select="."/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="nodeType" select="string(name())"/>
					<xsl:if test="not($refNodes[name() = $nodeType])">
						<xsl:apply-templates select="$localNodes[name() = $nodeType]"/>
					</xsl:if>				
				</xsl:otherwise>			
			</xsl:choose>		
		</xsl:for-each>	
	</xsl:template>
</xsl:stylesheet>
						
