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
public interface AIXMExtensionsConstants {

	/////////////////////////////
	// List of default values for an AIXM 5.1 extension
	/////////////////////////////
	public static final String AIXM_51_EXTENSION_DEFAULT_GENERATE_FILE_NAME_DATATYPES = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_generate_file_name_datatypes");//"AIXM_Extension_DataTypes.xsd";
	public static final String AIXM_51_EXTENSION_DEFAULT_GENERATE_FILE_NAME_FEATURES = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_generate_file_name_features");//"AIXM_Extension_Features.xsd";	
	public static final String AIXM_51_EXTENSION_DEFAULT_EXTENSION_VERSION = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_extension_version");//"1.0";
	public static final String AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_target_namespace");//"http://www.aixm.aero/schema/5.1/extension/ext";
	public static final String AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE_PREFIX = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_target_namespace_prefix");//"ext";
	public static final String AIXM_51_EXTENSION_DEFAULT_ATTRIBUTE_FORM = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_attribute_form");//"unqualified";
	public static final String AIXM_51_EXTENSION_DEFAULT_ELEMENT_FORM = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_default_element_form");//"qualified";
    
	public static final String AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_reverse_associations_namespace");
	public static final String AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_FILE_NAME = AIXMExtensionsProperties.getInstance().getProperty("aixm_51_extension_reverse_associations_file_name");
	
	
}
