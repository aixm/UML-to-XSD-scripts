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
public interface WXXMConstants {

	/////////////////////////////
	// List of WXXM stereotypes
	/////////////////////////////
	public static final String WXXM_STEREOTYPE_APPLICATION_SCHEMA = WXXMProperties.getInstance().getProperty("wxxm_stereotype_application_schema");//"Application Schema";
	public static final String WXXM_STEREOTYPE_LEAF = WXXMProperties.getInstance().getProperty("wxxm_stereotype_leaf");//"Leaf";
	public static final String WXXM_STEREOTYPE_FEATURE_TYPE = WXXMProperties.getInstance().getProperty("wxxm_stereotype_feature_type");//"FeatureType";
	public static final String WXXM_STEREOTYPE_ENUMERATION = WXXMProperties.getInstance().getProperty("wxxm_stereotype_enumeration");//"enumeration";
	public static final String WXXM_STEREOTYPE_TYPE = WXXMProperties.getInstance().getProperty("wxxm_stereotype_type");//"Type";
	public static final String WXXM_STEREOTYPE_UNION = WXXMProperties.getInstance().getProperty("wxxm_stereotype_union");//"Union";
	public static final String WXXM_STEREOTYPE_PROPERTY = WXXMProperties.getInstance().getProperty("wxxm_stereotype_property");//"property";
    
//	public final static String[] WXXM_STEREOTYPES_NOT_RELEVANT_FOR_AIRM = new String[] {
//	        
//	};
	
	/////////////////////////////
	// List of Tagged Values
	/////////////////////////////
	public final static String WXXM_TAGGED_VALUE_NAME_ELEMENT_FORM_DEFAULT = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_element_form_default");//"elementFormDefault";
	public final static String WXXM_TAGGED_VALUE_NAME_GML_PROFILE_SCHEMA = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_gml_profile_schema");//"gmlProfileSchema";
	public final static String WXXM_TAGGED_VALUE_NAME_OWNER = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_owner");//"owner";
	public final static String WXXM_TAGGED_VALUE_NAME_OWNED_BY = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_owned_by");//"ownedBy";
	public final static String WXXM_TAGGED_VALUE_NAME_SCHEMA_LOCATION = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_schema_location");//"schemaLocation";
	public final static String WXXM_TAGGED_VALUE_NAME_TARGET_NAMESPACE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_target_namespace");//"targetNamespace";
	public final static String WXXM_TAGGED_VALUE_NAME_VERSION = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_version");//"version";
	public final static String WXXM_TAGGED_VALUE_NAME_XMLNS = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_xmlns");//"xmlns";
	public final static String WXXM_TAGGED_VALUE_NAME_XSD_DOCUMENT = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_xsd_document");//"xsdDocument";
	public final static String WXXM_TAGGED_VALUE_NAME_CANONICAL_XSD = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_canonical_xsd");//"canonicalXSD";
	
	public final static String WXXM_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_by_value_property");//"byValueProperty";
	public final static String WXXM_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY_TYPE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_by_value_property_type");//"byValuePropertyType";
	public final static String WXXM_TAGGED_VALUE_NAME_CONTENT_TYPE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_content_type");//"contentType";
	public final static String WXXM_TAGGED_VALUE_NAME_DERIVATION = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_derivation");//"derivation";
	public final static String WXXM_TAGGED_VALUE_NAME_IS_COLLECTION = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_is_collection");//"isCollection";
	public final static String WXXM_TAGGED_VALUE_NAME_MAX_INCLUSIVE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_max_inclusive");//"maxInclusive";
	public final static String WXXM_TAGGED_VALUE_NAME_MIN_INCLUSIVE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_min_inclusive");//"minInclusive";
	public final static String WXXM_TAGGED_VALUE_NAME_NO_PROPERTY_TYPE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_no_property_type");//"noPropertyType";
	public final static String WXXM_TAGGED_VALUE_NAME_HAS_XML_LANG = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_has_xml_lang");//"hasXmlLang";
	public final static String WXXM_TAGGED_VALUE_NAME_MODEL_GROUP = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_model_group");//"modelGroup";
	public final static String WXXM_TAGGED_VALUE_NAME_XSD_DERIVATION = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_xsd_derivation");//"xsdDerivation";
	
	public final static String WXXM_TAGGED_VALUE_NAME_SEQUENCE_NUMBER = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_sequence_number");//"sequenceNumber";
	public final static String WXXM_TAGGED_VALUE_NAME_INLINE_OR_BY_REFERENCE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_inline_or_by_reference");//"inlineOrByReference";
	public final static String WXXM_TAGGED_VALUE_NAME_IS_METADATA = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_is_metadata");//"isMetadata";
	public final static String WXXM_TAGGED_VALUE_NAME_ANONYMOUS_ROLE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_anonymous_role");//"anonymousRole";
	public final static String WXXM_TAGGED_VALUE_NAME_ANONYMOUS_TYPE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_anonymous_type");//"anonymousType";
	public final static String WXXM_TAGGED_VALUE_NAME_MAX_OCCURS = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_max_occurs");//"maxOccurs";
	public final static String WXXM_TAGGED_VALUE_NAME_MIN_OCCURS = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_min_occurs");//"minOccurs";
	public final static String WXXM_TAGGED_VALUE_NAME_NILLABLE = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_nillable");//"nillable";
	
	public final static String WXXM_TAGGED_VALUE_NAME_EXTENDED_SEQUENCE_NUMBER = WXXMProperties.getInstance().getProperty("wxxm_tagged_value_name_extended_sequence_number");//"Extended UML Profile for XML Schemas::property::sequenceNumber";
    
	
	public final static String[] WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM = new String[] {
	    	WXXM_TAGGED_VALUE_NAME_ELEMENT_FORM_DEFAULT,
	    	WXXM_TAGGED_VALUE_NAME_GML_PROFILE_SCHEMA,
	    	WXXM_TAGGED_VALUE_NAME_OWNER,
	    	WXXM_TAGGED_VALUE_NAME_OWNED_BY,
	    	WXXM_TAGGED_VALUE_NAME_SCHEMA_LOCATION,
	    	WXXM_TAGGED_VALUE_NAME_TARGET_NAMESPACE,
	    	WXXM_TAGGED_VALUE_NAME_VERSION,
	    	WXXM_TAGGED_VALUE_NAME_XMLNS,
	    	WXXM_TAGGED_VALUE_NAME_XSD_DOCUMENT,
	    	WXXM_TAGGED_VALUE_NAME_CANONICAL_XSD,
	    	WXXM_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY,
	    	WXXM_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY_TYPE,
	    	WXXM_TAGGED_VALUE_NAME_CONTENT_TYPE,
	    	WXXM_TAGGED_VALUE_NAME_DERIVATION,
	    	WXXM_TAGGED_VALUE_NAME_XSD_DERIVATION,
	    	WXXM_TAGGED_VALUE_NAME_IS_COLLECTION,
	    	WXXM_TAGGED_VALUE_NAME_MAX_INCLUSIVE,
	    	WXXM_TAGGED_VALUE_NAME_MIN_INCLUSIVE,
	    	WXXM_TAGGED_VALUE_NAME_NO_PROPERTY_TYPE,
	    	WXXM_TAGGED_VALUE_NAME_HAS_XML_LANG,
	    	WXXM_TAGGED_VALUE_NAME_MODEL_GROUP,
	    	WXXM_TAGGED_VALUE_NAME_CONTENT_TYPE,
	    	WXXM_TAGGED_VALUE_NAME_DERIVATION,	    	
	    	WXXM_TAGGED_VALUE_NAME_SEQUENCE_NUMBER,
	    	WXXM_TAGGED_VALUE_NAME_INLINE_OR_BY_REFERENCE,
	    	WXXM_TAGGED_VALUE_NAME_IS_METADATA,
	    	WXXM_TAGGED_VALUE_NAME_ANONYMOUS_ROLE,
	    	WXXM_TAGGED_VALUE_NAME_ANONYMOUS_TYPE,
	    	WXXM_TAGGED_VALUE_NAME_MAX_OCCURS,
	    	WXXM_TAGGED_VALUE_NAME_MIN_OCCURS,
	    	WXXM_TAGGED_VALUE_NAME_NILLABLE,
	    	WXXM_TAGGED_VALUE_NAME_EXTENDED_SEQUENCE_NUMBER
	};
	
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_GUID = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_guid"));//0;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_type"));//1;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_NAME = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_name"));//2;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_stereotype"));//3;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_NAME = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_tagged_value_name"));//4;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_VALUE = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_tagged_value_value"));//5;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_GUID = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_tagged_value_parent_guid"));//6;
    public final int WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_TYPE = Integer.parseInt(WXXMProperties.getInstance().getProperty("wxxm_export_to_excel_column_tagged_value_parent_type"));//7;
	
}
