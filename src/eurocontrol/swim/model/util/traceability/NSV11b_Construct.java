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

import java.util.ArrayList;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

/**
 * @author hlepori
 */
public class NSV11b_Construct extends UML_Construct {

    protected Element _element;
    protected Attribute _attribute;
    protected ConnectorEnd _role;
    protected Connector _connector;
    
    /**
     * @param element
     */
    public NSV11b_Construct(Element element) {
        super(element);
        _element = element;
    }

    /**
     * @param attribute
     */
    public NSV11b_Construct(Attribute attribute, Element element) {
        super(attribute);
        _attribute = attribute;
        _element = element;
    }

    /**
     * @param role
     * @param connector
     * @param element
     */
    public NSV11b_Construct(ConnectorEnd role, Connector connector,
            Element element) {
        super(role, connector, element);
        _role=role;
        _connector=connector;
        _element=element;
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.UML_Construct#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        if(arg0 instanceof NSV11b_Construct)
            return super.equals(arg0);
        else return false;
    }
    
    public boolean isSemanticallyCompliantWith(AIRM_Concept airmConcept)
    {
        if(nameMatches(airmConcept))
        {
            if(getDefinition().equals(airmConcept.getDefinition()))
                    return true;
        }
        
        return false;
    }

    public String getSemanticComplianceReport(AIRM_Concept airmConcept)
    {
        String comment = new String();
        
        if(!isATMConcept())
        {
            comment = comment + "This construct is not an ATM concept." + " ";
        }
        else if(isSemanticallyCompliantWith(airmConcept))
        {
            // => (same names) and (different definitions)  
            if(getName().equals(airmConcept.getName()) && !getDefinition().equals(airmConcept.getDefinition()))
                comment = comment + "A synonym is used." + " ";
            
            // => (nsv11b type) different from (cldm concept type)
            if(!getType().equals(airmConcept.getType()))
                comment = comment + "The NSV11b " + getType() + " matches an " + "AIRM CLDM " + airmConcept.getType() + ". ";
        }
        else if (nameMatches(airmConcept))
        {
            if(!getDefinition().equals(airmConcept.getDefinition()))
                comment = comment + "The NSV 11b definition does not match." + " ";
        }
        else
        {
            comment = comment + "No equivalent concept found in the CLDM." + " ";
        }
        

        
        return comment;
    }
    
    /**
     * 
     * @param airmConcept
     * @return
     */
    public boolean nameMatches(AIRM_Concept airmConcept)
    {
        return nameMatches(airmConcept, getName());
    }
    
    /**
     * 
     * @param airmConcept
     * @return
     */
    protected boolean nameMatches(AIRM_Concept airmConcept, String name)
    {
        ArrayList validNames = airmConcept.getValidNames();
        for(Iterator iter = validNames.iterator(); iter.hasNext();)
        {
            String conceptName = (String)iter.next();
            //  => (same names) and (same definitions)   
            if(name.equals(conceptName))
                return true;
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    public boolean isATMConcept()
    {
        return true;
    }
    
    public String toString() {
        return "NSV11b Construct: " + super.toString();
    }
    
    /**
     * @return Returns the _element.
     */
    public Element getElement() {
        return _element;
    }
    
}
