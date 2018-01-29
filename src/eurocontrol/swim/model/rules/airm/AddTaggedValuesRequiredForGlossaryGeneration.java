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
import eurocontrol.swim.model.rules.common.Rule;
import eurocontrol.swim.model.sparx.EAConstants;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;

/**
 * This rule addws four tagged values to the AIRM
 * classes/relationships/attributes and roles
 * 
 * Target packages: Consolidated and Information
 * 
 * Tagged values:
 * 
 * 
 * @author hlepori
 */
public class AddTaggedValuesRequiredForGlossaryGeneration extends AbstractRule implements Rule, EAConstants, AIRMConstants {

    
    /*
     * (non-Javadoc)
     * 
     * @see eurocontrol.swim.model.rules.other.Rule#getName()
     */
    public String getName() {
        return "[AIRM] TAGGED VALUES - Add (if required) the tagged values required for glossary generation: source, status, synonyms & abbreviation";
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

                //////////////////////////////////////////////
                // Check the tagged values of the package                        
                //////////////////////////////////////////////
                for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                {
                    checkTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i], subPackage);
                }
                
                //////////////////////////////////////////////
                // Delete any deprecated tagged values from the package
                //////////////////////////////////////////////
                for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                {
                    deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],subPackage);
                }
                
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
                        for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                        {
                            checkTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i], element);
                        }
                  
                        //////////////////////////////////////////////
                        // Delete any deprecated tagged values from the class
                        //////////////////////////////////////////////
                        for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                        {
                            deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],element);
                        }
                        
                         ///////////////////////////////////////////////////
                         // Check the tagged values of the class attributes                        
                         ///////////////////////////////////////////////////
                         
                         for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                         {
                             attribute = (Attribute)attributeIter.next();
                             for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                             {
                                 checkTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],attribute, element);
                             }
                             
                             //////////////////////////////////////////////
                             // Delete any deprecated tagged values from the attribute
                             //////////////////////////////////////////////
                             for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                             {
                                 deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],attribute);
                             }
                         }
                         
                         ///////////////////////////////////////////////////
                         // Check the tagged values of the associations                        
                         ///////////////////////////////////////////////////
                         for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                         {
                             connector = (Connector)connectorIter.next();                            
                             
                             for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                             {
                                 deleteTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],connector);
                             }
                             
                             //////////////////////////////////////////////
                             // Delete any deprecated tagged values from the connector
                             //////////////////////////////////////////////
                             for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                             {
                                 deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],connector);
                             }                                                          
                             
                             ///////////////////////////////////////////////////
                             // Check the tagged values of the source role                        
                             ///////////////////////////////////////////////////
                             sourceRole = connector.GetSupplierEnd();
                                                        
                             if(sourceRole.GetRole().length() == 0)
                             {
                                 for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                                 {
                                     deleteTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],sourceRole, connector);
                                 }                              
                             }
                             else
                             {
                                 for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                                 {
                                     checkTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],sourceRole);
                                 }
                             }
                             
                             //////////////////////////////////////////////
                             // Delete any deprecated tagged values from the source role
                             //////////////////////////////////////////////
                             for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                             {
                                 deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],sourceRole, connector);
                             }   

                           
                             ///////////////////////////////////////////////////
                             // Check the tagged values of the target role                        
                             ///////////////////////////////////////////////////
                             targetRole = connector.GetClientEnd();

                             if(targetRole.GetRole().length() == 0)
                             {
                                 for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                                 {
                                     deleteTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],targetRole, connector);
                                 }                                 
                             }
                             else
                             {
                                 for(int i = 0; i< AIRM_TAGGED_VALUES_FOR_GLOSSARY.length;i++)
                                 {
                                     checkTaggedValue(AIRM_TAGGED_VALUES_FOR_GLOSSARY[i],targetRole);
                                 }
                             }
                             
                             //////////////////////////////////////////////
                             // Delete any deprecated tagged values from the target role
                             //////////////////////////////////////////////
                             for(int i = 0; i< AIRM_DEPRECATED_TAGGED_VALUES.length;i++)
                             {
                                 deleteTaggedValue(AIRM_DEPRECATED_TAGGED_VALUES[i],targetRole, connector);
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
        if(taggedValueName.equals(AIRM_TAGGED_VALUE_NAME_DEFINITION_IN_LEXICON))
            return AIRM_TAGGED_VALUE_DEFINITION_IN_LEXICON_DEFAULT_VALUE;
        
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
    
    private void checkTaggedValue(String taggedValueName,Package myPackage)
    {            
        try
        {
         if (!SparxUtilities.isTaggedValuePopulated(taggedValueName,myPackage))
         {
             SparxUtilities.addTaggedValue(taggedValueName,myPackage, getDefaultValueForTaggedValue(taggedValueName));
             EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] added to " + SparxUtilities.toString(myPackage.GetElement()));
         }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add tagged value [" + taggedValueName + "] to " + SparxUtilities.toString(myPackage.GetElement()));
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
    
    private void deleteTaggedValue(String taggedValueName,Package myPackage)
    {
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(taggedValueName,myPackage))
            {
                SparxUtilities.deleteTaggedValue(taggedValueName, myPackage);       
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from " + SparxUtilities.toString(myPackage.GetElement()));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from " + SparxUtilities.toString(myPackage.GetElement()));
        } 
    }
    
    private void deleteTaggedValue(String taggedValueName,Element element)
    {
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(taggedValueName,element))
            {
                SparxUtilities.deleteTaggedValue(taggedValueName, element);       
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from " + SparxUtilities.toString(element));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from " + SparxUtilities.toString(element));
        } 
    }
    
    private void deleteTaggedValue(String taggedValueName,Attribute attribute)
    {
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(taggedValueName,attribute))
            {
                SparxUtilities.deleteTaggedValue(taggedValueName, attribute);       
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from " + SparxUtilities.toString(attribute));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from " + SparxUtilities.toString(attribute));
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
                EAEventManager.getInstance().fireEAEvent(this,"Tagged value [" + taggedValueName + "] deleted from role " + SparxUtilities.toString(connector));
            }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete tagged value [" + taggedValueName + "] from role " + SparxUtilities.toString(connector));
        } 
    }
    
}