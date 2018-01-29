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
public interface AIRMConstants {

    
	/////////////////////////////
	// List of package GUID
	/////////////////////////////
	public final static String AIRM_ROOT_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_root_package_guid");
	public final static String AIRM_FOUNDATION_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_foundation_package_guid");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_package_guid");
	public final static String AIRM_INFORMATION_MODEL_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_information_model_package_guid");
	
	
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_DATATYPES_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_datatypes_package_guid");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_ENUMERATION_PACKAGE = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_enumeration_package");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_IDENTIFIER_PACKAGE = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_identifier_package");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_TYPES_PACKAGE = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_types_package");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_CONSTRUCTED_TYPES_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_constructed_types_package_guid");
	public final static String AIRM_CONSOLIDATED_LOGICAL_MODEL_SUBJECT_FIELDS_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_consolidated_logical_model_subject_fields_package_guid");
	
	
	public final static String AIRM_INFORMATION_MODEL_SUBJECT_FIELDS_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_information_model_subject_fields_package_guid");
	
	
	public final static String AIRM_PARKING_AREA_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_parking_area_package_guid");
	public final static String AIRM_PARKING_AREA_DEPRECATED_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_parking_area_deprecated_package_guid");
	public final static String AIRM_PARKING_AREA_UNPROCESSED_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_parking_area_unprocessed_package_guid");
	public final static String AIRM_PARKING_AREA_OUT_OF_SCOPE_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_parking_area_out_of_scope_package_guid");
	public final static String AIRM_PROPOSALS_PACKAGE_GUID = AIRMProperties.getInstance().getProperty("airm_proposals_package_guid");
	
	    
	/////////////////////////////
	// Default addresses of SQL servers storing EA models (e.g. AIRM)  
	/////////////////////////////		
	public final static String AIRM_SQL_CONNECTION_STRING = AIRMProperties.getInstance().getProperty("airm_sql_connection_string"); 
	
	/////////////////////////////
	// List of Tagged Values
	/////////////////////////////
	public final static String AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_mega_unique_identifier");		
	public final static String AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_definition_source");
	public final static String AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_definition_status");
	public final static String AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_definition_abbreviation");
	public final static String AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_definition_synonyms");
	public final static String AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON = AIRMProperties.getInstance().getProperty("airm_tagged_value_name_definition_in_lexicon");
	
	public final static String[] AIRM_TAGGED_VALUES_FOR_GLOSSARY  = new String[] {
	        AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,
	        AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS,
	        AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,
	        AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS,
	        AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON
	};
	
	/////////////////////////////
	// List of default values for Tagged Values
	/////////////////////////////
	public final static String AIRM_TAGGED_VALUE_DEFINITION_STATUS_DEFAULT_VALUE = AIRMProperties.getInstance().getProperty("airm_tagged_value_definition_status_default_value");
	public final static String AIRM_TAGGED_VALUE_DEFINITION_IN_LEXICON_DEFAULT_VALUE = AIRMProperties.getInstance().getProperty("airm_tagged_value_definition_in_lexicon_default_value");
	
	////////////////////////////////////////
	// Excel Constants for the AIRM glossary
	////////////////////////////////////////
	public static int AIRM_GLOSSARY_COLUMN_NAME = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_name"));
	public static int AIRM_GLOSSARY_COLUMN_ABBREVIATION = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_abbreviation"));
	public static int AIRM_GLOSSARY_COLUMN_DEFINITION = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_definition"));
	public static int AIRM_GLOSSARY_COLUMN_DEFINITION_SOURCE = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_definition_source"));
	public static int AIRM_GLOSSARY_COLUMN_AUTHOR = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_author"));
    
	public static int AIRM_GLOSSARY_COLUMN_SYNONYM = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_synonym"));
	public static int AIRM_GLOSSARY_COLUMN_STATUS =Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_status"));
	public static int AIRM_GLOSSARY_COLUMN_UML_TYPE = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_uml_type"));
	public static int AIRM_GLOSSARY_COLUMN_GUID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_guid"));
	public static int AIRM_GLOSSARY_COLUMN_PACKAGE_HIERARCHY = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_column_package_hierarchy"));

	public static int AIRM_GLOSSARY_FIRST_EDITABLE_ROW = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_glossary_first_editable_row"));
	
	////////////////////////////////////////
	// Excel Constants for the export to MEGA
	////////////////////////////////////////
	public static int AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_first_editable_row"));
		
	// data model
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_MEGA_ID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_mega_id"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_SHORT_NAME = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_short_name"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_COMMENT = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_comment"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_OWNER_DATAMODEL = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_owner_datamodel"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_OWNER_PACKAGER = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_owner_packager"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_NAF_ARCHITECTURE_VIEW = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_naf_architecture_view"));
	public static int AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_GUID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_datamodel_column_guid"));
	
	
	// entity
	public static int AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_MEGA_ID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_entity_column_mega_id"));
	public static int AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_SHORT_NAME = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_entity_column_short_name"));
	public static int AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_OWNER_DATA_MODEL = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_entity_column_owner_data_model"));
	public static int AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_COMMENT = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_entity_column_comment"));
	public static int AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_GUID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_entity_column_guid"));
	
	// attribute
	public static int AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_MEGA_ID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_attribute_column_mega_id"));
	public static int AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_SHORT_NAME = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_attribute_column_short_name"));
	public static int AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_ENTITY_DM = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_attribute_column_entity_dm"));
	public static int AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_COMMENT = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_attribute_column_comment"));
	public static int AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_GUID = Integer.parseInt(AIRMProperties.getInstance().getProperty("airm_export_to_mega_attribute_column_guid"));
	
	
	
	/////////////////////////////
	// DEPRECATED TAGGED VALUES
	/////////////////////////////
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_SOURCE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_source");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_CONTRIBUTION_SOURCE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_contribution_source");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_PERSISTENCE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_persistence");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY_TYPE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_by_value_property_type");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_IS_COLLECTION = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_is_collection");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_MODEL_GROUP = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_model_group");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_NO_PROPERTY_TYPE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_no_property_type");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_XSD_DERIVATION = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_xsd_derivation");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_HAS_XML_LANG = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_has_xml_lang");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_XML_SCHEMA_TYPE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_xml_schema_type");	
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_status");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_abbreviation");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_synonyms");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_definition_source");
	public final static String AIRM_DEPRECATED_TAGGED_VALUE_NAME_CONTRIBUTOR = AIRMProperties.getInstance().getProperty("airm_deprecated_tagged_value_name_contributor");
	
	
	public final static String[] AIRM_DEPRECATED_TAGGED_VALUES = new String[] {
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_SOURCE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_CONTRIBUTION_SOURCE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_PERSISTENCE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_BY_VALUE_PROPERTY_TYPE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_IS_COLLECTION,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_MODEL_GROUP,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_NO_PROPERTY_TYPE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_XSD_DERIVATION,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_HAS_XML_LANG,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_XML_SCHEMA_TYPE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE,
        AIRM_DEPRECATED_TAGGED_VALUE_NAME_CONTRIBUTOR
	};
}
