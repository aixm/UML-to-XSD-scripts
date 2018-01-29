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

package eurocontrol.swim.model.rules.wxxm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;
import eurocontrol.swim.model.util.constants.WXXMConstants;

/**
 * @author hlepori
 */
public class ApplyAIRMRuleBookToMetPackage extends AbstractRule implements WXXMConstants, AIRMConstants  {

    private HashMap _attributesToBeChangedToAssociations = new HashMap();
    private HashMap _associationsToBeChangedToAttributes = new HashMap();
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM v1.1.1] LEAN AND MEAN WXXM FROM AIRM - 1.Apply AIRM rulebook to the AIRM v1.1.1 MET package.";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) 
    {     
        processPackageContentRecursively(myPackage.GetPackages());       
        replaceAttributesByAssociations();
        replaceAssociationsByAttributes();
    }

   
    
    /**
     * 
     * @param packageCollection
     */
    private void processPackageContentRecursively(Collection packageCollection) {

        if (packageCollection!= null && packageCollection.GetCount() > 0) {
            // local variable to store a subpackage
            Package subPackage = null;
            
            for (Iterator packageIter = packageCollection.iterator(); packageIter
            .hasNext();) {
                subPackage = (Package) packageIter.next();
         
                // local variable to store the element (= a class)
                Element element = null; 
                
                EAEventManager.getInstance().fireEAEvent(this,"Updating package " + subPackage.GetName());
             
                /////////////////////////////////////
                // STEP 1: update the package itself
                /////////////////////////////////////
                updatePackage(subPackage);
                
                
                for (Iterator elementIter = subPackage.GetElements().iterator();elementIter.hasNext();)
                {
                    element = (Element)elementIter.next();
                    
                    // local variable to store an attribute
                    Attribute attribute = null;
                    // local variable to store a connector
                    Connector connector = null;
 

                    if (element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION)) {                        
                        /////////////////////////////////////
                        // STEP 2: update each individual element of the subpackage (class or enumeration) 
                        /////////////////////////////////////
                        updateElement(element);
                        
                        

                        for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                        {
                            /////////////////////////////////////
                            // STEP 3: update each individual attribute of the element 
                            /////////////////////////////////////
                            attribute = (Attribute)attributeIter.next();
                            updateAttribute(attribute, element);
                        }
                        

                        for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                        {
                            /////////////////////////////////////
                            // STEP 4: update each individual connector of the element 
                            /////////////////////////////////////
                            connector = (Connector) connectorIter.next();
                            updateConnector(connector, element);
                                                                                    
                        }                        
                    }   
                }                       
                //////////                
                processPackageContentRecursively(subPackage.GetPackages());
            }           
        }
    }
    
    
	/**
	 * 
	 * @param aPackage
	 */
    private void updatePackage(Package aPackage) {        
        
        /////////////////////
        // 1. Remove WXXM-specific Tagged Values
        /////////////////////
        try
        {
	        for(int i = 0; i < WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM.length;i++)
	        {	
	                if(SparxUtilities.isTaggedValuePopulated(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i],aPackage))
	                    SparxUtilities.deleteTaggedValue(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i], aPackage);
	        }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update the tagged values of package " + aPackage.GetName()); 
        }
        
        
        /////////////////////
        // 2. Remove Stereotypes
        /////////////////////
        aPackage.SetStereotypeEx("");
        
        /////////////////////
        // 3. Update Names
        /////////////////////
        String name = aPackage.GetName();                        
        String newName = name.replaceFirst("AVWX_","");
        newName = newName.replaceAll("WX_","");
        newName = newName.replaceAll("_","");
        newName = newName.replaceAll(" ","");
        aPackage.SetName(newName);
        
        
        /////////////////////
        // 4. Remove Default Language
        /////////////////////
        // TODO
        
        // Update package
        boolean isUpdateSuccessFull = aPackage.Update();
        if(!isUpdateSuccessFull)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not properly update package " + name);
        }
    }
    
	/**
	 * 
	 * @param element
	 */
    private void updateElement(Element element) {
        
        /////////////////////
        // 1. Remove WXXM-specific Tagged Values
        /////////////////////
        try
        {
	        for(int i = 0; i < WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM.length;i++)
	        {	
	                if(SparxUtilities.isTaggedValuePopulated(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i],element))
	                    SparxUtilities.deleteTaggedValue(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i], element);
	        }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update the tagged values of " + SparxUtilities.toString(element)); 
        }        
        
        
        // Update element
        boolean isUpdateSuccessFull = element.Update();
        if(!isUpdateSuccessFull)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not properly update " + SparxUtilities.toString(element));
        }
    }
    
	/**
	 * 
	 * @param attribute
	 */
    private void updateAttribute(Attribute attribute, Element element) {

        /////////////////////
        // 1. Remove WXXM-specific Tagged Values
        /////////////////////

        for(int i = 0; i < WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM.length;i++)
        {	
            try
            {
                if(SparxUtilities.isTaggedValuePopulated(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i],attribute))
                {
                    try
                    {
                        SparxUtilities.deleteTaggedValue(WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i], attribute);
                    }
                    catch(Exception e)
                    {
                        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update the tagged values of " + SparxUtilities.toString(attribute,element)); 
                    }  
                }                
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not check whether the tagged values " + WXXM_TAGGED_VALUES_NOT_RELEVANT_FOR_AIRM[i] +  " is defined for " + SparxUtilities.toString(attribute,element)); 
            }  
        }            
        
        /////////////////////
        // 2. Update the attribute multiplicity
        /////////////////////
        String multiplicity = new String();
        if(attribute.GetLowerBound().equals(attribute.GetUpperBound()))
        {
            multiplicity = attribute.GetLowerBound();
        }
        else
        {
            multiplicity = attribute.GetLowerBound() + ".." + attribute.GetUpperBound();
        }
        if(multiplicity.equals("0..1"))
        {
            // The default multiplicity shall not be captured in the AIRM, according to AIRM rule 22.
            attribute.SetLowerBound("");
            attribute.SetUpperBound("");
        }
        
        /////////////////////
        // 3. Check the validity of the datatype
        /////////////////////
        if(attribute.GetClassifierID() <= 0 )
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Attribute " + SparxUtilities.toString(attribute,element) + " has no datatype.");
        }
        else
        {
            Element datatype = null;
            try
            {
                datatype = EAConnection.getInstance().getRepository().GetElementByID(attribute.GetClassifierID());
            }
            catch(Exception e)
            {
                // Do nothing
            }
	        if(datatype == null)
	        {
	            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Attribute " + SparxUtilities.toString(attribute,element) + " is not typed properly");
	        }
	        else if(SparxUtilities.belongsToPackage(datatype, AIRM_CONSOLIDATED_LOGICAL_MODEL_SUBJECT_FIELDS_PACKAGE_GUID))
	        {
	            _attributesToBeChangedToAssociations.put(attribute,new Object[] {element,datatype, multiplicity});                  
	        }
	        else if(SparxUtilities.belongsToPackage(datatype, AIRM_CONSOLIDATED_LOGICAL_MODEL_CONSTRUCTED_TYPES_PACKAGE_GUID))
	        {
	            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Attribute" + SparxUtilities.toString(attribute,element) + " is typed with a class from package ConstructedTypes. Check manually the validity of this property.");                       
	        }
        }
        // Update element
        boolean isUpdateSuccessFull = attribute.Update();
        if(!isUpdateSuccessFull)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not properly update " + SparxUtilities.toString(element) + "." + attribute.GetName());
        }
    }
    
    
	/**
	 * 
	 * @param connector
	 */
    private void updateConnector(Connector connector, Element element) {
          
        /////////////////////
        // 1. Check the validity of the association 
        /////////////////////
        int id = -1;
        ConnectorEnd role = null;
        
        if(connector.GetSupplierID() != element.GetElementID())
        {
            role = connector.GetSupplierEnd();
            id = connector.GetSupplierID();
        }
        else
        {
            role = connector.GetClientEnd();
            id = connector.GetClientID();
        }
        
        if(connector.GetType().equals(EA_CONNECTOR_TYPE_ASSOCIATION) || connector.GetType().equals(EA_CONNECTOR_TYPE_AGGREGATION))
        { 
            Element attachedElement = EAConnection.getInstance().getRepository().GetElementByID(id);             
            if(attachedElement != null)
            {
                if (SparxUtilities.belongsToPackage(attachedElement,AIRM_CONSOLIDATED_LOGICAL_MODEL_DATATYPES_PACKAGE_GUID) || SparxUtilities.belongsToPackage(attachedElement,AIRM_FOUNDATION_PACKAGE_GUID))
                {
                    _associationsToBeChangedToAttributes.put(connector,new Object[]{role,element, new Integer(id)});
                }
            }
        }
        
    }
    
    
    /**
     * 
     */
    private void replaceAttributesByAssociations() {
        for (Iterator iter = new ArrayList(_attributesToBeChangedToAssociations.keySet()).iterator();iter.hasNext();)
        {
            Attribute attribute = (Attribute)iter.next();
            Object[] objects = (Object[])_attributesToBeChangedToAssociations.get(attribute);
            Element source = (Element)objects[0];
            Element datatype = (Element)objects[1];
            String multiplicity = (String)objects[2];
            replaceAttributeByAssociation(attribute,datatype,source, multiplicity);
            EAEventManager.getInstance().fireEAEvent(this,"Attribute " + SparxUtilities.toString(attribute,source) + " is typed with class " + SparxUtilities.toString(datatype) + "from package SubjectFields. Replacing the attribute by an association.");
            
            try
            {
	            EAEventManager.getInstance().fireEAEvent(this,"Deleting attribute " + SparxUtilities.toString(attribute,source));
                SparxUtilities.deleteAttribute(attribute,source);
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete attribute " + SparxUtilities.toString(attribute,source));
            }
        }        
    }
    
    
    /**
     * 
     * @param attribute
     * @param datatype
     * @param source
     */
    private void replaceAttributeByAssociation(Attribute attribute, Element datatype, Element source, String multiplicity)
    {
        Connector newConnector = (Connector)source.GetConnectors().AddNew("",EA_CONNECTOR_TYPE_ASSOCIATION);
        
        newConnector.SetSupplierID(source.GetElementID());
        newConnector.SetClientID(datatype.GetElementID());
        
        newConnector.Update();
        
		String name = source.GetName();
		String firstChar = name.substring(0, 1).toLowerCase();		
		String newName = firstChar + name.substring(1, name.length());		
        newConnector.GetSupplierEnd().SetRole(newName);  
        newConnector.GetSupplierEnd().SetNavigable(EA_CONNECTOR_NAVIGABILITY_NON_NAVIGABLE);               
                
        newConnector.GetClientEnd().SetRole(attribute.GetName());
        newConnector.GetClientEnd().SetRoleNote(attribute.GetNotes());
        newConnector.GetClientEnd().SetNavigable(EA_CONNECTOR_NAVIGABILITY_NON_NAVIGABLE);
        
        //String multiplicity = attribute.GetLowerBound() + ".." + attribute.GetUpperBound(); 
        if (multiplicity.equals("0..n") || multiplicity.equals("0..*"))
        {
            // do not add multiplicity to the associations, to comply with AIRM rule 26 stating that Relationships shall, by default, be represented with multiplicity [0..*] 
        }
        else if (multiplicity.equals("1"))
        {
            // This value represents the default Sprax EA value for attribute multiplicity, which should be interpreted as [0..1] in an AIRM context
            // Add multiplicty 0..1 to association because it is different from the default value 0..* specified by AIRM rule 26.
            newConnector.GetClientEnd().SetCardinality("0..1");
        }
        else
        {
            // The attribute has a particular multiplicity which is not 0..1 or 0..*
            // Copy the value as is
            newConnector.GetClientEnd().SetCardinality(multiplicity);
        }
                   
        newConnector.GetClientEnd().Update();
        newConnector.GetSupplierEnd().Update();        
    }
    
    
    /**
     * 
     */
    private void replaceAssociationsByAttributes() {
        for (Iterator iter = new ArrayList(_associationsToBeChangedToAttributes.keySet()).iterator();iter.hasNext();)
        {
            Connector connector = (Connector) iter.next();
            Object[] objects = (Object[])_associationsToBeChangedToAttributes.get(connector);
            ConnectorEnd role = (ConnectorEnd)objects[0];
            Element element = (Element)objects[1];
            int datatypeId = ((Integer)objects[2]).intValue();
            
            Attribute newAttribute = (Attribute)element.GetAttributes().AddNew(role.GetRole(), EA_TYPE_ATTRIBUTE);            
            newAttribute.SetClassifierID(datatypeId);
            Element datatype = EAConnection.getInstance().getRepository().GetElementByID(datatypeId);
            newAttribute.SetType(datatype.GetName());
            newAttribute.SetNotes(role.GetRoleNote());
            // add multiplicty
            String roleCardinality = role.GetCardinality();
            
            if(roleCardinality.length() ==1)
            {
                newAttribute.SetLowerBound(roleCardinality);
                newAttribute.SetUpperBound(roleCardinality);
            }
            else if (roleCardinality.length() == 4)
            {
                // multiplicty of type 0..1, 0..n etc...
                newAttribute.SetLowerBound(new Character(roleCardinality.charAt(0)).toString());
                newAttribute.SetUpperBound(new Character(roleCardinality.charAt(3)).toString());
            }
            else
            {
                // do nothing - default values are used
            }
            
            newAttribute.Update();
            element.Update();
            
            try
            {
	            EAEventManager.getInstance().fireEAEvent(this,"Deleting association " + SparxUtilities.toString(connector));
                SparxUtilities.deleteConnector(connector,element);
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete association " + SparxUtilities.toString(connector));
            }
            
        }
    }
   
    
}
