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
public interface MEGAConstants {

	// type of NAF Architecture view
	public static String MEGA_NAF_OPERATIONAL_ARCHITECTURE_VIEW = MEGAProperties.getInstance().getProperty("mega_naf_operational_architecture_view");
	public static String MEGA_NAF_SYSTEM_ARCHITECTURE_VIEW = MEGAProperties.getInstance().getProperty("mega_naf_system_architecture_view");
	public static String MEGA_NAF_ALL_ARCHITECTURE_VIEW = MEGAProperties.getInstance().getProperty("mega_naf_all_architecture_view");
		
	// type of Mega elements
	public static String MEGA_ENTITY_TYPE = MEGAProperties.getInstance().getProperty("mega_entity_type");
	public static String MEGA_ENUMERATION_TYPE = MEGAProperties.getInstance().getProperty("mega_enumeration_type");
	public static String MEGA_DATATYPE_TYPE = MEGAProperties.getInstance().getProperty("mega_datatype_type");
    
	// The name of the target architecture in MEGA
    public static final String MEGA_TARGET_ARCHITECTURE = MEGAProperties.getInstance().getProperty("mega_target_architecture");
    
}
