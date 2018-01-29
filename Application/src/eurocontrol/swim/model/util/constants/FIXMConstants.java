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
public interface FIXMConstants {

	/////////////////////////////
	// List of package GUID
	/////////////////////////////
    public static final String FIXM_GUID_PACKAGE_XSD_SCHEMA_DATATYPES = FIXMProperties.getInstance().getProperty("fixm_guid_package_xsd_schema_datatypes");
    public static final String FIXM_GUID_PACKAGE_CORE = FIXMProperties.getInstance().getProperty("fixm_guid_package_core");
    public static final String FIXM_GUID_PACKAGE_BASE = FIXMProperties.getInstance().getProperty("fixm_guid_package_base");
    
    
    public static final String FIXM_DEFAULT_CORE_VERSION = FIXMProperties.getInstance().getProperty("fixm_default_core_version");
    public static final String FIXM_DEFAULT_TARGET_NAMESPACE = FIXMProperties.getInstance().getProperty("fixm_default_target_namespace");
    public static final String FIXM_DEFAULT_TARGET_NAMESPACE_PREFIX = FIXMProperties.getInstance().getProperty("fixm_default_target_namespace_prefix");
    
    public static final String FIXM_BASE_TARGET_NAMESPACE = FIXMProperties.getInstance().getProperty("fixm_base_target_namespace");
    public static final String FIXM_BASE_TARGET_NAMESPACE_PREFIX = FIXMProperties.getInstance().getProperty("fixm_base_target_namespace_prefix");
    
    public static final String FIXM_STEREOTYPE_CHOICE = FIXMProperties.getInstance().getProperty("fixm_stereotype_choice");
    public static final String FIXM_STEREOTYPE_ENUMERATION = FIXMProperties.getInstance().getProperty("fixm_stereotype_enumeration");
    
    public static final String FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE = FIXMProperties.getInstance().getProperty("fixm_tagged_value_name_definition_source");
    public static final String FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES = FIXMProperties.getInstance().getProperty("fixm_tagged_value_name_definition_alternate_names");
    public static final String FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS = FIXMProperties.getInstance().getProperty("fixm_tagged_value_name_definition_abbreviations");
    
}
