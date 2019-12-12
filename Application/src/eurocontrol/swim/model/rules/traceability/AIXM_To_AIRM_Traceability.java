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

package eurocontrol.swim.model.rules.traceability;

import java.util.ArrayList;
import java.util.HashMap;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

import eurocontrol.swim.model.util.constants.AIXMConstants;
import eurocontrol.swim.model.util.traceability.AIRM_Concept;
import eurocontrol.swim.model.util.traceability.AIXM_Construct;
import eurocontrol.swim.model.util.traceability.NSV11b_Construct;

/**
 * @author hlepori
 */
public class AIXM_To_AIRM_Traceability extends NSV11b_To_AIRM_Traceability implements AIXMConstants {

    protected int _count=0;
    
    private HashMap _matchingConceptsMap = new HashMap();
    private HashMap _associatedPropertiesMap = new HashMap();
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.traceability.NSV11b_To_AIRM_Traceability#getName()
     */
    public String getName() {  
        return "[AIXM onwards] TRACEABILITY - Generate traceability of AIXM back to the AIRM CLDM (Deprecated - does not rely on star model)";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.airm.NSV11b_To_AIRM_Traceability#getNSV11bConstruct(org.sparx.Element)
     */
    protected NSV11b_Construct getNSV11bConstruct(Element element) {
        return new AIXM_Construct(element);
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.airm.NSV11b_To_AIRM_Traceability#getNSV11bConstruct(org.sparx.Attribute, org.sparx.Element)
     */
    protected NSV11b_Construct getNSV11bConstruct(Attribute attribute,
            Element element) {
        return new AIXM_Construct(attribute, element);
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.airm.NSV11b_To_AIRM_Traceability#getNSV11bConstruct(org.sparx.ConnectorEnd, org.sparx.Connector, org.sparx.Element)
     */
    protected NSV11b_Construct getNSV11bConstruct(ConnectorEnd role,
            Connector connector, Element element) {
        return new AIXM_Construct(role, connector, element);
    }
    

    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.airm.NSV11b_To_AIRM_Traceability#realiseTraceability(eurocontrol.swim.model.util.NSV11b_Construct, eurocontrol.swim.model.util.AIRM_Concept)
     */
    protected void realiseTraceability(NSV11b_Construct nsv11b_construct,
            AIRM_Concept airm_concept) throws Exception {        
        super.realiseTraceability(nsv11b_construct, airm_concept);
        populateMapsForGraphicalTraceability(nsv11b_construct, airm_concept);
    }
    
    /**
     * 
     * @param nsv11b_construct
     * @param airm_concept
     */
    protected void populateMapsForGraphicalTraceability(
            NSV11b_Construct nsv11b_construct, AIRM_Concept airm_concept) {
      
        _matchingConceptsMap.put(nsv11b_construct, airm_concept);
        
        if(nsv11b_construct.getType().equals(EA_TYPE_CLASS) || nsv11b_construct.getType().equals(EA_TYPE_ENUMERATION))
        {
            _associatedPropertiesMap.put(nsv11b_construct.getElement().GetElementGUID(), new ArrayList());
        }
        else
        {            
            ((ArrayList)_associatedPropertiesMap.get(nsv11b_construct.getElement().GetElementGUID())).add(nsv11b_construct);
        }               
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.airm.NSV11b_To_AIRM_Traceability#finaliseTraceability()
     */
    protected void finaliseTraceability() throws Exception {
        super.finaliseTraceability();
        buildDiagrams();
    }
    
    /**
     * 
     *
     */
    protected void buildDiagrams()
    {
        //TODO
    }
    
}
