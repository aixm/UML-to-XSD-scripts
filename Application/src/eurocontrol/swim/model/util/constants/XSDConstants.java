/**
 *
##########################################################################
##########################################################################

Copyright (c) 2010, EUROCONTROL
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright 
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of EUROCONTROL nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

##########################################################################
##########################################################################
*/

package eurocontrol.swim.model.util.constants;

/**
 * @author hlepori
 */
public interface XSDConstants {

	public static final String XSD_TAG_ROOT = "schema";
	public static final String XSD_TAG_IMPORT = "import";
	public static final String XSD_TAG_ELEMENT = "element";
	public static final String XSD_TAG_COMPLEX_TYPE = "complexType";
	public static final String XSD_TAG_SIMPLE_TYPE = "simpleType";
	public static final String XSD_TAG_COMPLEX_CONTENT = "complexContent";
	public static final String XSD_TAG_SIMPLE_CONTENT = "simpleContent";
	public static final String XSD_TAG_GROUP = "group";
	public static final String XSD_TAG_EXTENSION = "extension";
	public static final String XSD_TAG_SEQUENCE = "sequence";
	public static final String XSD_TAG_RESTRICTION = "restriction";
	public static final String XSD_TAG_ENUMERATION = "enumeration";
	public static final String XSD_TAG_LIST = "list";
	public static final String XSD_TAG_INCLUDE = "include";
	public static final String XSD_TAG_ATTRIBUTE_GROUP = "attributeGroup";
	public static final String XSD_TAG_ATTRIBUTE = "attribute";
	public static final String XSD_TAG_CHOICE = "choice";
	public static final String XSD_TAG_ANNOTATION = "annotation";
    public static final String XSD_TAG_APPINFO = "appinfo";   //OVA2015 AIXMSCR-5
    public static final String XSD_TAG_GMLDESCRIPTION = "gml:description";   //OVA2015 AIXMSCR-5
    public static final String XSD_TAG_DOCUMENTATION = "documentation";
    public static final String XSD_TAG_DEPRECATED = "deprecated";
	public static final String XSD_TAG_UNION = "union";
	public static final String XSD_TAG_PATTERN = "pattern";
	public static final String XSD_TAG_ANY = "any";
    public static final String XSD_TAG_RATIONALE = "rationale";
    public static final String XSD_TAG_REPLACEMENT = "replacement";
    public static final String XSD_TAG_DEPRECATION_VERSION = "deprecationVersion";
    public static final String XSD_TAG_DELETION_VERSION = "deletionVersion";
	
	public static final String XSD_XML_NAMESPACE = "xmlns";
	
	
	public static final String XSD_HTTP_XLINK_SCHEMA = "http://www.w3.org/1999/xlink";
    public static final String XSD_HTTP_XLINK_SCHEMA_PREFIX = "xlink";
    public static final String XSD_HTTP_XLINK_SCHEMA_LOCATION = "http://www.w3.org/1999/xlink.xsd";
	public static final String XSD_HTTP_XHTML_SCHEMA = "http://www.w3.org/1999/xhtml";
	public static final String XSD_HTTP_GML_SCHEMA = "http://www.opengis.net/gml/3.2";
    public static final String XSD_HTTP_GML_SCHEMA_LOCATION = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";
    public static final String XSD_HTTP_GML_SCHEMA_PREFIX = "gml";
	public static final String XSD_HTTP_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	public static final String XSD_TAGGED_VALUE_TARGET_NAMESPACE = "XSD::targetNamespace";
	public static final String XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX = "XSD::targetNamespacePrefix";
    public static final String XSD_TAGGED_VALUE_ATTRIBUTE_FORM_DEFAULT = "XSD::attributeFormDefault";
	public static final String XSD_TAGGED_VALUE_ELEMENT_FORM_DEFAULT = "XSD::elementFormDefault";
	
	public static final String XSD_DEFAULT_ATTRIBUTE_FORM = "unqualified";
	public static final String XSD_DEFAULT_ELEMENT_FORM = "qualified";
	public static final String XSD_DEFAULT_TARGET_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static final String XSD_DEFAULT_TARGET_NAMESPACE_PREFIX = "xsd";
}
