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

package eurocontrol.swim.model.sparx;

/**
 * @author hlepori
 */
public interface EAConstants {

		/////////////////////////////
    	// List of EA element types
    	/////////////////////////////
		public static final String EA_TYPE_CLASS = "Class";
		public static final String EA_TYPE_PACKAGE = "Package";
		public static final String EA_TYPE_CLASS_DIAGRAM = "Diagram";
		public static final String EA_TYPE_ENUMERATION = "Enumeration";
		
		public static final String EA_TYPE_ATTRIBUTE = "Attribute";
		
		public static final String EA_TYPE_CONNECTOR_END = "Role";
		
		/////////////////////////////
    	// List of navigability properties for associations
    	/////////////////////////////
		public static final String EA_CONNECTOR_NAVIGABILITY_UNSPECIFIED = "Unspecified";
		public static final String EA_CONNECTOR_NAVIGABILITY_NAVIGABLE = "Navigable";
		public static final String EA_CONNECTOR_NAVIGABILITY_NON_NAVIGABLE = "Non-Navigable";
		
		/////////////////////////////
		// List of types of associations
		/////////////////////////////
		public static final String EA_CONNECTOR_TYPE_ASSOCIATION = "Association";
		public static final String EA_CONNECTOR_TYPE_INHERITANCE = "Generalization";
		public static final String EA_CONNECTOR_TYPE_DEPENDANCY = "Dependency";
		public static final String EA_CONNECTOR_TYPE_NOTELINK = "NoteLink";
		public static final String EA_CONNECTOR_TYPE_AGGREGATION = "Aggregation";
		

		/////////////////////////////
		// List of properties of aggregations
		/////////////////////////////
		public static final String EA_CONNECTOR_CONTAINMENT_VALUE = "Value";
		public static final String EA_CONNECTOR_CONTAINMENT_UNSPECIFIED = "Unspecified";	
		public static final String EA_CONNECTOR_AGGREGATION_COMPOSITE = "composite";
		public static final String EA_CONNECTOR_AGGREGATION_NONE = "none";
		
		/////////////////////////////
		// List of visibility properties (public, private...)
		/////////////////////////////
		public static final String EA_VISIBILITY_PUBLIC = "Public";
				
		
}
