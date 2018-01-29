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

package eurocontrol.swim.model.util.traceability;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIXMConstants;

/**
 * @author hlepori
 */
public class AIXM_Construct extends NSV11b_Construct implements AIXMConstants  {

    
    
    /**
     * @param element
     */
    public AIXM_Construct(Element element) {
        super(element);
    }

    /**
     * @param attribute
     */
    public AIXM_Construct(Attribute attribute, Element element) {
        super(attribute, element);
        if(isAIXMDatatype(element))
        {
            _name = element.GetName() + "." + attribute.GetName();
        }       
    }

    /**
     * @param role
     * @param connector
     * @param element
     */
    public AIXM_Construct(ConnectorEnd role, Connector connector,
            Element element) {
        super(role, connector, element);
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.NSV11b_Construct#nameMatches(eurocontrol.swim.model.util.AIRM_Concept)
     */
    public boolean nameMatches(AIRM_Concept airmConcept) 
    {
        if(getType().equals(EA_TYPE_CLASS) || getType().equals(EA_TYPE_ENUMERATION))
        {
            if(getName().startsWith("Code") && getName().endsWith("BaseType"))
            {
                String newName = getName().substring(0,getName().length() - 8) + "Type";
                return super.nameMatches(airmConcept,newName);
            }
            if(getName().contains("Extension"))
            {
                String newName = getName().substring(0,getName().length() - 9);
            }
        }
        if(getType().equals(EA_TYPE_ATTRIBUTE))
        {
            if(_element.GetName().startsWith("Code") && _element.GetName().endsWith("BaseType"))
            {
                String newName = _element.GetName().substring(0,_element.GetName().length() - 8) + "Type" + "." + _attribute.GetName();
                return super.nameMatches(airmConcept,newName);
            }
        }
        
        return super.nameMatches(airmConcept);
    }

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.NSV11b_Construct#isATMConcept()
     */
    public boolean isATMConcept() 
    {        
        if(getType().equals(EA_TYPE_CLASS) || getType().equals(EA_TYPE_ENUMERATION))
        {
            if(getName().startsWith("Code") && !getName().endsWith("BaseType"))
                return false;            
            if(getName().equals("XHTMLType"))
                return false;
            if(SparxUtilities.belongsToPackage(_element,AIXM_51_GUID_PACKAGE_AIXM_ABSTRACT_FEATURES))
                return false;
        }
        else if(getType().equals(EA_TYPE_ATTRIBUTE))
        {
            if(getName().contains("nilReason"))
            	return false;
            if(_attribute.GetStereotype().equals(AIXM_51_STEREOTYPE_XSD_FACET))
                return false;
            if(_attribute.GetName().equals("OTHER"))
                return false;                        
        }
        return super.isATMConcept();
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.NSV11b_Construct#toString()
     */
    public String toString() {       
        return "AIXM Construct: " + "["+_type + "] " + _name;
    }    
    
    /**
     * 
     * @param element
     * @return
     */
    protected boolean isAIXMDatatype (Element element)
    {
        if(SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_AIXM_DATATYPES))
            return true;
        String stereotype = element.GetStereotype();
        if(stereotype.equals(AIXM_51_STEREOTYPE_CODELIST) || stereotype.equals(AIXM_51_STEREOTYPE_DATATYPE))
            return true;        
        return false;
    }
    
}
