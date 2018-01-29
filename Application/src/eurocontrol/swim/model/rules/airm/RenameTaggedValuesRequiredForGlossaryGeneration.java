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

package eurocontrol.swim.model.rules.airm;

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

/**
 * @author hlepori
 */
public class RenameTaggedValuesRequiredForGlossaryGeneration extends AbstractRule implements AIRMConstants {

    
    /*
     * (non-Javadoc)
     * 
     * @see eurocontrol.swim.model.rules.other.Rule#getName()
     */
    public String getName() {
        return "[AIRM] TAGGED VALUES - Rename the tagged values required for glossary generation: definition source, status, synonyms & abbreviation";
    }

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        processPackageContentRecursively(myPackage
                .GetPackages());
    }
    

    /**
     * @param informationModelPackage
     */
    private void processPackageContentRecursively(Collection packageCollection) {

        if (packageCollection!= null && packageCollection.GetCount() > 0) {
            // local variable to store a subpackage
            Package subPackage = null;
            // local variable to store the element (= a class)
            Element element = null;
            // local variable to store an attribute
            Attribute attribute = null;
            // local variable to store a connector
            Connector connector = null;
            // local variable to store the roles
            ConnectorEnd sourceRole = null;
            ConnectorEnd targetRole = null;
            
            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) {
                subPackage = (Package) packageIter.next();
                 
                EAEventManager.getInstance().fireEAEvent(this,"Updating package " + subPackage.GetName());

                /////////////////////////////////////////////////
                // Process the classes defined in this subpackage
                /////////////////////////////////////////////////
                for (Iterator elementsIter = subPackage.GetElements()
                        .iterator(); elementsIter.hasNext();) {
                    
                    element = (Element) elementsIter.next();
                    
                    // The class element is used generically
                    // WARNING: in some cases, an element of type enumeration
                    // ...
                    if (element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION)) {
                        
                        //////////////////////////////////////////////
                        // Check the tagged values of the class                        
                        //////////////////////////////////////////////

                        try
                        {
                            SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE, AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element);
                            SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS, AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element);
                            SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS, AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element);
                            SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION, AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, element);
                            
                        }
                        catch(Exception e)
                        {
                            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update element " + SparxUtilities.toString(element));
                        }
                  
                         ///////////////////////////////////////////////////
                         // Check the tagged values of the class attributes                        
                         ///////////////////////////////////////////////////
                         
                         for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                         {
                             attribute = (Attribute)attributeIter.next();
                          try
                          {
                              SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE, AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute);
                              SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS, AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute);
                              SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS, AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, attribute);
                              SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION, AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, attribute);
                          }
	                         catch(Exception e)
	                         {
	                             EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update attribute " + SparxUtilities.toString(attribute, element));
	                         }
                         }
                         
                         ///////////////////////////////////////////////////
                         // Check the tagged values of the associations                        
                         ///////////////////////////////////////////////////
                         for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                         {
                             connector = (Connector)connectorIter.next();    
                             try
                             {
                                 SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE, AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, connector);
                                 SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS, AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, connector);
                                 SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS, AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, connector);
                                 SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION, AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, connector);
                             }
	                         catch(Exception e)
	                         {
	                             EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update connector " + SparxUtilities.toString(connector));
	                         }

                     
                             ///////////////////////////////////////////////////
                             // Check the tagged values of the source role                        
                             ///////////////////////////////////////////////////
                             sourceRole = connector.GetSupplierEnd();
                                                        
                             if(sourceRole.GetRole().length() > 0)
                             {
                                 try
                                 {
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE, AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, sourceRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS, AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, sourceRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS, AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, sourceRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION, AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, sourceRole);
                                 }
                                 catch(Exception e)
		                         {
		                             EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update role " + SparxUtilities.toString(sourceRole));
		                         }
                             }

                           
                             ///////////////////////////////////////////////////
                             // Check the tagged values of the target role                        
                             ///////////////////////////////////////////////////
                             targetRole = connector.GetClientEnd();

                             if(targetRole.GetRole().length() > 0)
                             {
                                 try
                                 {
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_DEFINITION_SOURCE, AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, targetRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_STATUS, AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, targetRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_SYNONYMS, AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, targetRole);
                                     SparxUtilities.renameTaggedValue(AIRM_DEPRECATED_TAGGED_VALUE_NAME_ABBREVIATION, AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, targetRole);
                                 }
                                 catch(Exception e)
		                         {
		                             EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update role " + SparxUtilities.toString(targetRole));
		                         }
                             }
                         } 
                    } 
                }  
                processPackageContentRecursively(subPackage.GetPackages());
            }
        } 
    }
    
    
    
    

    private String getDefaultValueForTaggedValue(String taggedValueName)
    {
        if(taggedValueName.equals(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS))
            return AIRM_TAGGED_VALUE_DEFINITION_STATUS_DEFAULT_VALUE;
        
        return "";
    }
    
    private void checkTaggedValue(String taggedValueName,Attribute attribute, Element element)
    {            
        try
        {
         if (!SparxUtilities.isTaggedValuePopulated(taggedValueName,attribute))
         {
             SparxUtilities.addTaggedValue(taggedValueName,attribute, getDefaultValueForTaggedValue(taggedValueName));
             EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] added to " + SparxUtilities.toString(attribute,element));
         }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add tagged value [" + taggedValueName + "] to " + SparxUtilities.toString(attribute));
        }
    }
    
    private void checkTaggedValue(String taggedValueName,Element element)
    {            
        try
        {
         if (!SparxUtilities.isTaggedValuePopulated(taggedValueName,element))
         {
             SparxUtilities.addTaggedValue(taggedValueName,element, getDefaultValueForTaggedValue(taggedValueName));
             EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] added to " + SparxUtilities.toString(element));
         }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add tagged value [" + taggedValueName + "] to " + SparxUtilities.toString(element));
        }
    }
    
    private void checkTaggedValue(String taggedValueName,ConnectorEnd role)
    {            
        try
        {
         if (!SparxUtilities.isTaggedValuePopulated(taggedValueName,role))
         {
             SparxUtilities.addTaggedValue(taggedValueName,role, getDefaultValueForTaggedValue(taggedValueName));
             EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] added to " + SparxUtilities.toString(role));
         }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add tagged value [" + taggedValueName + "] to " + SparxUtilities.toString(role));
        }
    }

    private void deleteTaggedValue(String taggedValueName,Connector connector)
    {
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(taggedValueName,connector))
            {
                SparxUtilities.deleteTaggedValue(taggedValueName, connector);       
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from " + SparxUtilities.toString(connector));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from " + SparxUtilities.toString(connector));
        } 
    }

    private void deleteTaggedValue(String taggedValueName,ConnectorEnd role, Connector connector)
    {
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(taggedValueName,role))
            {
                SparxUtilities.deleteTaggedValue(taggedValueName, role);       
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from blank role " + SparxUtilities.toString(connector));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from blank role " + SparxUtilities.toString(connector));
        } 
    }
}
