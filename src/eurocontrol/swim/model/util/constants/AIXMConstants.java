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
public interface AIXMConstants {

	/////////////////////////////
	// List of package GUID
	/////////////////////////////
    public static final String AIXM_51_GUID_PACKAGE_XSD_SCHEMA_DATATYPES = AIXMProperties.getInstance().getProperty("aixm_51_guid_package_xsd_schema_datatypes");//""{B4B4F82F-2CF3-4b84-84A0-6C527442A244}";
    public static final String AIXM_51_GUID_PACKAGE_AIXM_DATATYPES = AIXMProperties.getInstance().getProperty("aixm_51_guid_package_aixm_datatypes");//"{974F9493-39A7-432c-AD06-291F3870A033}";
    public static final String AIXM_51_GUID_PACKAGE_AIXM_FEATURES = AIXMProperties.getInstance().getProperty("aixm_51_guid_package_aixm_features");//"{67EACA15-9791-4c6b-9789-4BC1CCCCD07C}";
    public static final String AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY = AIXMProperties.getInstance().getProperty("aixm_51_guid_package_iso_19107_geometry");//"{AB0D4C12-3099-4e60-B1C5-FFE1087CEE35}";
    public static final String AIXM_51_GUID_PACKAGE_AIXM_ABSTRACT_FEATURES = AIXMProperties.getInstance().getProperty("aixm_51_guid_package_aixm_abstract_features");//"{B82D289B-634A-40a8-A778-60491509D0A8}";
    
	/////////////////////////////
	// List of class GUID
	/////////////////////////////
    public static final String AIXM_51_GUID_CLASS_XSD_SCHEMA_STRING = AIXMProperties.getInstance().getProperty("aixm_51_guid_class_xsd_schema_string");//"{AC61C2C9-A5E7-4a5f-BDB6-0122E1BFD3C2}";
    public static final String AIXM_51_GUID_CLASS_AIXMTIMESLICE = AIXMProperties.getInstance().getProperty("aixm_51_guid_class_aixmtimeslice");//"{4C40780C-8471-4641-885D-FEA3AE3DADD5}";
    public static final String AIXM_51_GUID_CLASS_AIXMOBJECT = AIXMProperties.getInstance().getProperty("aixm_51_guid_class_aixmobject");//"{70884A63-14D1-4bb1-A1CF-2AFB8C252955}";


	/////////////////////////////
	// List of used stereotypes
	/////////////////////////////
	public static final String AIXM_51_STEREOTYPE_CODELIST = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_codelist");//"codelist";
	public static final String AIXM_51_STEREOTYPE_DATATYPE = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_datatype");//"datatype";
	public static final String AIXM_51_STEREOTYPE_XSD_FACET = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_xsd_facet");//"XSDfacet";
	public static final String AIXM_51_STEREOTYPE_FEATURE = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_feature");//"feature";
	public static final String AIXM_51_STEREOTYPE_OBJECT = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_object");//"object";
	public static final String AIXM_51_STEREOTYPE_MESSAGE = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_message");//"message";
	public static final String AIXM_51_STEREOTYPE_CHOICE = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_choice");//"choice";
	public static final String AIXM_51_STEREOTYPE_COLLECTION_MEMBER_CHOICE = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_collection_member_choice");//"collectionMemberChoice";
	public static final String AIXM_51_STEREOTYPE_EXTENSION = AIXMProperties.getInstance().getProperty("aixm_51_stereotype_extension");//"extension";
	
	/////////////////////////////
	// List of Sparx EA Tagged Values
	/////////////////////////////
	public static final String AIXM_TAGGED_VALUE_CORE_VERSION = AIXMProperties.getInstance().getProperty("aixm_tagged_value_core_version");//"AIXM::coreVersion";
	public static final String AIXM_TAGGED_VALUE_GENERATE_FILE_NAME = AIXMProperties.getInstance().getProperty("aixm_tagged_value_generate_file_name");//"AIXM::generateFileName";
	public static final String AIXM_TAGGED_VALUE_EXTENSION_VERSION = AIXMProperties.getInstance().getProperty("aixm_tagged_value_extension_version");//"AIXM::extensionVersion";
    public static final String AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION = AIXMProperties.getInstance().getProperty("aixm_tagged_value_extension_file_location");//"AIXM::fileLocation";

	/////////////////////////////
	// List of default values for the core AIXM 5.1
	/////////////////////////////
	public static final String AIXM_51_DEFAULT_GENERATE_FILE_NAME_DATATYPES = AIXMProperties.getInstance().getProperty("aixm_51_default_generate_file_name_datatypes");//"AIXM_DataTypes.xsd";	
	public static final String AIXM_51_DEFAULT_GENERATE_FILE_NAME_FEATURES = AIXMProperties.getInstance().getProperty("aixm_51_default_generate_file_name_features");//"AIXM_Features.xsd";
	public static final String AIXM_51_DEFAULT_CORE_VERSION = AIXMProperties.getInstance().getProperty("aixm_51_default_core_version");//"5.1";	
	public static final String AIXM_51_DEFAULT_TARGET_NAMESPACE = AIXMProperties.getInstance().getProperty("aixm_51_default_target_namespace");//"http://www.aixm.aero/schema/5.1";
	public static final String AIXM_51_DEFAULT_TARGET_NAMESPACE_PREFIX = AIXMProperties.getInstance().getProperty("aixm_51_default_target_namespace_prefix");//"aixm";
    // OVA2015 AIXMSCR-3
	public static final String AIXM_51_DEFAULT_EXTENSION_FILE_LOCATION = AIXMProperties.getInstance().getProperty("aixm_51_default_extension_file_location");//".";

}
