/**
 *
##########################################################################
##########################################################################

Copyright (c) 2013, EUROCONTROL
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

package eurocontrol.swim.model.rules.fixm;

import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;
import eurocontrol.swim.model.util.constants.FIXMConstants;

/**
 * @author hlepori
 */
public class FIXM_ConceptualModel_CleanUp  extends AbstractRule implements AIRMConstants, FIXMConstants {

    protected final String DEFINITION = "Definition";
    protected final String ALTERNATE_NAMES = "Alternate Names";
    protected final String HAS_PARTS = "Has Parts";
    protected final String IS_PART_OF = "Is Part Of";
    protected final String DATA_TYPES = "Data Type(s)";
    protected final String RANGE_OF_VALUES = "Range of Values";
    protected final String BUSINESS_RULES = "Business Rules";
    protected final String NOTES = "Notes";
    protected final String REFERENCE = "Reference";
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getName()
     */
    public String getName() {
        return "[FIXM Conceptual Model] V&V - Model clean-up";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        processPackage(myPackage);      
    }
    
    /**
     * @param informationModelPackage
     */
    private void processPackageContentRecursively(Collection packageCollection) 
    {
        if (packageCollection!= null && packageCollection.GetCount() > 0) 
        {            
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) 
            {
                Package subPackage = (Package) packageIter.next();                
                EAEventManager.getInstance().fireEAEvent(this,"Updating package " + subPackage.GetName());                
                processPackage(subPackage);
            }
        }
    }
    
    /**
     * 
     * @param myPackage
     */
    protected void processPackage(Package myPackage)
    {
        cleanUpPackage(myPackage);
        for(Iterator iter = myPackage.GetElements().iterator();iter.hasNext();)
        {
            Element element = (Element)iter.next();
            processElement(element);            
        }       
        processPackageContentRecursively(myPackage.GetPackages());
    }
    
    /**
     * 
     * @param element
     */
    protected void processElement (Element element)
    {
        if(element.GetType().equals("Note") || element.GetType().equals("Boundary"))
            return;
        
        cleanUpElement(element);
        for(Iterator iter = element.GetAttributes().iterator();iter.hasNext();)
        {
            Attribute attribute = (Attribute)iter.next();
            processAttribute(attribute);
        }
        for(Iterator iter = element.GetConnectors().iterator();iter.hasNext();)
        {
            Connector connector = (Connector) iter.next();
            processConnector(connector);
        }
    }
    
    /**
     * 
     * @param attribute
     */
    protected void processAttribute (Attribute attribute)
    {
        cleanUpAttribute(attribute);
    }
    
    /**
     * 
     * @param connector
     */
    protected void processConnector (Connector connector)
    {
        cleanUpConnector(connector);
        processConnectorEnd(connector.GetClientEnd());
        processConnectorEnd(connector.GetSupplierEnd());
    }
    
    /**
     * 
     * @param role
     */
    protected void processConnectorEnd (ConnectorEnd role)
    {
        cleanUpRole(role);
    }
       
    
    /**
     * 
     * @param myPackage
     */
    protected void cleanUpPackage(Package myPackage)
    {
        // Do nothing
    }
  
    /**
     * 
     * @param element
     */
    protected void cleanUpElement(Element element)
    {
        //////////////////////
        // Update stereotypes
        //////////////////////
        if(element.GetStereotype().equals("FIXMEntity"))
        {
            element.SetStereotype("");
        }
        else if(element.GetStereotype().equals("FIXMMessage"))
        {
            element.SetStereotype("Message");
        }
        else
        {
            if(element.GetStereotype().length() == 0)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": " + SparxUtilities.toString(element) + " has no stereotype.");
            }
            else
            {
               EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Check stereotype of " + SparxUtilities.toString(element) + ".");
            }            
        }
        
        /////////////////////////////
        // Update Status
        /////////////////////////////
        element.SetStatus("Proposed");
        
        /////////////////////////////
        // Update Author
        /////////////////////////////
        element.SetAuthor("SESAR");
        
        element.Update();
        
//        /////////////////////////////
//        // Update Tagged Values
//        /////////////////////////////
//        try
//        {
//            //////////////////
//            // rename tagged values inherited from AIRM IM useful to FIXM, or create if absent
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,element,"");
//                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": A definition source must be provided for element " + SparxUtilities.toString(element));
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS,element))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES, element);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES,element,"");                
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,element))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS, element);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS,element,"");                
//            }
//            
//            /////////////////
//                     
//            
//            
//            //////////////////
//            // remove tagged values inherited from AIRM IM but useless to FIXM
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON, element))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON,element);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, element))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, element);
//            
//            
//            //////////////////
//            // remove tagged values inherited from the import of the FIXM data dictionary but useless to FIXM
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated("FIXM::" + DATA_TYPES, element))
//                SparxUtilities.deleteTaggedValue("FIXM::" + DATA_TYPES, element);
//            
//            if(SparxUtilities.isTaggedValuePopulated("FIXM::" + HAS_PARTS, element))
//                SparxUtilities.deleteTaggedValue("FIXM::" + HAS_PARTS, element);
//            
//            if(SparxUtilities.isTaggedValuePopulated("FIXM::" + IS_PART_OF, element))
//                SparxUtilities.deleteTaggedValue("FIXM::" + IS_PART_OF, element);
//            
//            if(SparxUtilities.isTaggedValuePopulated("FIXM::" + RANGE_OF_VALUES, element))
//                SparxUtilities.deleteTaggedValue("FIXM::" + RANGE_OF_VALUES, element);
//            
//        }
//        catch(Exception e)
//        {
//            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not process tagged values for element " + SparxUtilities.toString(element));
//        }
        
       
    }
    
    /**
     * 
     * @param attribute
     */
    protected void cleanUpAttribute(Attribute attribute)
    {
        
        /////////////////////////////
        // Update Tagged Values
        /////////////////////////////
//        try
//        {
//            //////////////////
//            // rename tagged values inherited from AIRM IM useful to FIXM, or create if absent
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,attribute,"");
//                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": A definition source must be provided for attribute " + SparxUtilities.toString(attribute));
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS,attribute))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES, attribute);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES,attribute,"");                
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,attribute))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS, attribute);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS,attribute,"");                
//            }
//            
//            /////////////////
//                     
//            //////////////////
//            // remove tagged values inherited from AIRM IM useless to FIXM
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON, attribute))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON,attribute);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, attribute))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, attribute);
//            
//            
//            
//        }
//        catch(Exception e)
//        {
//            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not process tagged values for attribute " + SparxUtilities.toString(attribute));
//        }
//        
//        attribute.Update();
    }
    
    /**
     * 
     * @param connector
     */
    protected void cleanUpConnector(Connector connector)
    {
        
    }
    
    /**
     * 
     * @param role
     */
    protected void cleanUpRole(ConnectorEnd role)
    {
//        if(role.GetRole().length()==0)
//            return;
//        
//        /////////////////////////////
//        // Update Tagged Values
//        /////////////////////////////
//        try
//        {
//            //////////////////
//            // rename tagged values inherited from AIRM IM useful to FIXM, or create if absent
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, role))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, role);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,role,"");
//                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": A definition source must be provided for role " + SparxUtilities.toString(role));
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS,role))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES, role);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ALTERNATE_NAMES,role,"");                
//            }
//            
//            /////////////////
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,role))
//            {
//                SparxUtilities.renameTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS, role);
//            }
//            else
//            {
//                SparxUtilities.addTaggedValue(FIXM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATIONS,role,"");                
//            }
//            
//            /////////////////
//                     
//            //////////////////
//            // remove tagged values inherited from AIRM IM useless to FIXM
//            //////////////////
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON, role))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON,role);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, role))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, role);
//            
//            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, role))
//                SparxUtilities.deleteTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, role);
//            
//            
//            
//        }
//        catch(Exception e)
//        {
//            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not process tagged values for role " + SparxUtilities.toString(role));
//        }
    }
}
