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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;

/**
 * @author hlepori
 */
public class AIRM_Concept extends UML_Construct implements AIRMConstants, Serializable {

    protected ArrayList _validNames;
    
    /**
     * @param element
     */
    public AIRM_Concept(Element element) {
        super(element);
        _validNames = getListOfValidNameFor(element);                
    }

    /**
     * @param attribute
     */
    public AIRM_Concept(Attribute attribute, Element element) {
        super(attribute);
         
        if(SparxUtilities.belongsToPackage(element, AIRM_CONSOLIDATED_LOGICAL_MODEL_DATATYPES_PACKAGE_GUID))
        {
            _name = element.GetName() + "." + attribute.GetName();
            _validNames = new ArrayList();
            
            ArrayList validNamesForElement = getListOfValidNameFor(element);
            ArrayList validNamesForAttribute = getListOfValidNameFor(attribute);
            
            for (Iterator iter1 = validNamesForElement.iterator();iter1.hasNext();)
            {
                String elementName = (String)iter1.next();
                for (Iterator iter2 = validNamesForAttribute.iterator();iter2.hasNext();)
                {
                    String attributeName = (String)iter2.next();
                    _validNames.add(elementName + "." + attributeName);
                }
            }            
        }
        else
        {
            _validNames = getListOfValidNameFor(attribute);
        }                    
    }

    /**
     * @param role
     * @param connector
     * @param element
     */
    public AIRM_Concept(ConnectorEnd role, Connector connector, Element element) {
        super(role, connector, element);
        _validNames = getListOfValidNameFor(role);
    }
    

    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.UML_Construct#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        if(arg0 instanceof AIRM_Concept)
            return super.equals(arg0);
        else return false;
    }
    
    /**
     * @return Returns the _validNames.
     */
    public ArrayList getValidNames() {
        return _validNames;
    }

    
    /**
     * 
     * @param element
     * @return
     */
    protected ArrayList getListOfValidNameFor(Element element)
    {
        ArrayList list = new ArrayList();
        list.add(element.GetName());
        
        String[] synonyms = null;
        String listOfSynonyms = null;
        try
        {
            listOfSynonyms = SparxUtilities.getContentOfTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS,element);
        }
        catch(Exception e)
        {
            // do nothing
        }
        
        if(listOfSynonyms != null && listOfSynonyms.length()>0)
        {	
            synonyms = listOfSynonyms.split(",");
            list.addAll(Arrays.asList(synonyms));
        }
        
        return list;
    }
    
    
    /**
     * 
     * @param attribute
     * @return
     */
    protected ArrayList getListOfValidNameFor(Attribute attribute)
    {
        ArrayList list = new ArrayList();
        list.add(attribute.GetName());
        
        String[] synonyms = null;
        String listOfSynonyms = null;
        try
        {
            listOfSynonyms = SparxUtilities.getContentOfTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS,attribute);
        }
        catch(Exception e)
        {
            // do nothing
        }
        
        if(listOfSynonyms != null && listOfSynonyms.length()>0)
        {	
            synonyms = listOfSynonyms.split(",");
            list.addAll(Arrays.asList(synonyms));
        }
        
        return list;
    }
    
    /**
     * 
     * @param element
     * @return
     */
    protected ArrayList getListOfValidNameFor(ConnectorEnd role)
    {
        ArrayList list = new ArrayList();
        list.add(role.GetRole());
        
        String[] synonyms = null;
        String listOfSynonyms = null;
        try
        {
            listOfSynonyms = SparxUtilities.getContentOfTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS,role);
        }
        catch(Exception e)
        {
            // do nothing
        }
        
        if(listOfSynonyms != null && listOfSynonyms.length()>0)
        {	
            synonyms = listOfSynonyms.split(",");
            list.addAll(Arrays.asList(synonyms));
        }
        
        return list;
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.util.UML_Construct#toString()
     */
    public String toString() {
        return "AIRM Concept: " + super.toString();
    }
    
}
