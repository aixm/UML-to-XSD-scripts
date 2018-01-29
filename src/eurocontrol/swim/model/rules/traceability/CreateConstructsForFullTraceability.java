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

package eurocontrol.swim.model.rules.traceability;

import java.util.HashMap;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Constraint;
import org.sparx.Diagram;
import org.sparx.DiagramObject;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;

/**
 * @author hlepori
 */
public class CreateConstructsForFullTraceability extends AbstractRule implements AIRMConstants {

    public final String NOV_7 = "NOV-7";
    public final String NSV_11a = "NSV-11a";
    public final String NSV_11b = "NSV-11b";
    
    public String _taggedValuePrefix = new String();
    
    protected Package _workingPackage;
    protected boolean _isAirmCldm = false;
    protected boolean _isAirmIM = false;
    
    // [GUID of the UML representation of a source class, GUID of the source class]
    protected HashMap _hashMap_GUID_mapping = new HashMap();
    
    // [GUID of the UML representation of a source class, ID of the parent class]
    protected HashMap _hashMapForSpecialisation = new HashMap();
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.Rule#getName()
     */
    public String getName() {
        return "[Any UML Model] GENERATE STAR MODEL - Build UML entities, properties and constraints enabling the traceability to AIRM CLDM.";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#apply()
     */
    public void apply() {
        // Create a parent package where the new constructs will be stored 
        createMainPackage();        
        super.apply();
    }
    
 
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        EAEventManager.getInstance().fireEAEvent(this, "Processing package " + myPackage.GetName());
        
        if(myPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID) || SparxUtilities.belongsToPackage(myPackage,AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID))
        {
            _isAirmCldm = true;
            _isAirmIM = false; 
            _taggedValuePrefix = NSV_11a + ":";
        }
        else if(myPackage.GetPackageGUID().equals(AIRM_INFORMATION_MODEL_PACKAGE_GUID) || SparxUtilities.belongsToPackage(myPackage,AIRM_INFORMATION_MODEL_PACKAGE_GUID))
        {
            _isAirmCldm = false;
            _isAirmIM = true;
            _taggedValuePrefix = NOV_7 + ":";
        }
        // It will be assumed in this case that model being processed qualifies for a component of the NSV-11b layer (UML physical model).
        else
        {
            _isAirmCldm = false;
            _isAirmIM = false;
            _taggedValuePrefix = NSV_11b + ":";
        }
        
        if(myPackage.GetElements().GetCount()>0)
        {
            processPackage(myPackage,_workingPackage);            
        }
        processPackageContentRecursively(myPackage.GetPackages(), _workingPackage);
        
        // build specialisations
        buildSpecialisations();
    }
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void processPackageContentRecursively(Collection packages, Package parentPackageInNewStructure) {
        
        for (Iterator packageIter = packages.iterator(); packageIter.hasNext();)
        {
           
            Package myPackage = (Package) packageIter.next();           
            Package packageInTheNewStructure = processPackage(myPackage, parentPackageInNewStructure);            
            processPackageContentRecursively(myPackage.GetPackages(),packageInTheNewStructure);
        }              
    }
    
    /**
     * 
     * @param myPackage
     * @return
     */
    protected Package processPackage(Package myPackage, Package parentPackageInNewStructure)
    {
        // Add new package to the new structure
        Package packageInTheNewStructure = createPackageInNewStructure(myPackage, parentPackageInNewStructure);
        
        //Create class diagram (only if there is at least one element stored in the corresponding package
        Diagram diagram = null;
        if(myPackage.GetElements().GetCount()>0)
            diagram = createDiagramInPackage(packageInTheNewStructure);
        
        // process all elements from the package 
        for (Iterator iter = myPackage.GetElements().iterator();iter.hasNext();)
        {
            Element element = (Element) iter.next();
            
            // create class matching the UML class
            if(element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION))
            {
                // create UML representation of the UML class and add it to the diagram
                Element representationOfUMLClass = createRepresentationOfUMLClass(element, packageInTheNewStructure);
                addUMLClassToDiagram(representationOfUMLClass, diagram);
                
                for (Iterator attrIter = element.GetAttributes().iterator();attrIter.hasNext();)
                {
                    Attribute attribute = (Attribute) attrIter.next();
                    
                    // create class matching the UML attribute and add it to the diagram
                    Element representationOfUMLAttribute = createRepresentationOfUMLAttribute(attribute, element, packageInTheNewStructure);
                    addUMLClassToDiagram(representationOfUMLAttribute, diagram);
                    
                    // Build aggregation
                    createAggregationBetweenClassAndProperty(representationOfUMLClass, representationOfUMLAttribute, getCardinalityForAttribute(attribute));
                }
                
                for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                {
                    Connector connector = (Connector) connectorIter.next();
                    
                    // Pick up the right UML role (it can be indifferently the Source or the Target role, depending on how the association is created in Sparx)
                    ConnectorEnd role = connector.GetClientEnd();
                    int attachedElementId = connector.GetClientID();
                    
                    if(attachedElementId == element.GetElementID())
                    {
                        role = connector.GetSupplierEnd();
                        attachedElementId = connector.GetSupplierID();
                    }
                    
                    if(role.GetRole() != null && role.GetRole().length() > 0)
                    {
                    
                        // create class matching the UML role and add it to the diagram
                        Element representationOfUMLRole  = createRepresentationOfUMLRole(role, connector, element, packageInTheNewStructure, attachedElementId); 
                        addUMLClassToDiagram(representationOfUMLRole, diagram);
                        
                        // Build aggregation
                        createAggregationBetweenClassAndProperty(representationOfUMLClass,representationOfUMLRole, getCardinalityForRole(role));
                    }
                }  
                
                // TODO: remove following code 
                for (Iterator constraintIter = element.GetConstraints().iterator();constraintIter.hasNext();)
                {
                    Constraint constraint = (Constraint) constraintIter.next();
                    
                    // create class matching the constraint and add it to the diagram
                    Element representationOfConstraint = createRepresentationOfConstraint(constraint, element, packageInTheNewStructure);
                    addUMLClassToDiagram(representationOfConstraint, diagram);
                    
                    // Build aggregation
                    createAggregationBetweenClassAndConstraint(representationOfUMLClass, representationOfConstraint);
                }
                
            }            
        }    
        return packageInTheNewStructure;
    }

    /**
     * 
     * @param umlClass
     * @return
     */
    protected Element createRepresentationOfUMLClass(Element umlClass, Package parentPackage)
    {
        Element representationOfUMLClass = (Element) parentPackage.GetElements().AddNew("", EA_TYPE_CLASS);
        
        representationOfUMLClass.SetName(umlClass.GetName());
        representationOfUMLClass.SetNotes(umlClass.GetNotes());
        representationOfUMLClass.SetStereotype(umlClass.GetStereotype());
        representationOfUMLClass.SetStatus(umlClass.GetStatus());
        
        representationOfUMLClass.Update();
        
        // TODO: add specialisation, if any. To be processed at the end only: the parent class in the source model may not have been processed yet.
        _hashMap_GUID_mapping.put(representationOfUMLClass.GetElementGUID(),umlClass.GetElementGUID());
        if(umlClass.GetBaseClasses().GetCount()>0)
            _hashMapForSpecialisation.put(representationOfUMLClass.GetElementGUID(),umlClass.GetBaseClasses().GetAt((short)0));
        
        
        // Adding specific tagged values
        try
        { 
            // TODO update script once the urn information is available.
            String urn = "N/A";
            SparxUtilities.addTaggedValue(_taggedValuePrefix + "urn",representationOfUMLClass,urn);
           
           // TODO remove this temporary solution once urn information is available
           SparxUtilities.addTaggedValue(_taggedValuePrefix + "GUID",representationOfUMLClass,umlClass.GetElementGUID());
           
           // TODO update script to add the urn information of the parents, once available
           if(umlClass.GetBaseClasses().GetCount()>0)
           {
               String parentName = new String("");
               for (Iterator iter = umlClass.GetBaseClasses().iterator();iter.hasNext();)
               {
                   Element parent = (Element)iter.next();
                   parentName = parentName + parent.GetName() + ";";
               }
               SparxUtilities.addTaggedValue(_taggedValuePrefix + "isSpecialisationOf",representationOfUMLClass,parentName);
           }              
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return representationOfUMLClass;
    }
    
    /**
     * 
     * @param umlAttribute
     * @return
     */
    protected Element createRepresentationOfUMLAttribute(Attribute umlAttribute, Element umlClass, Package parentPackage)
    {
        Element representationOfUMLAttribute = (Element) parentPackage.GetElements().AddNew("", EA_TYPE_CLASS);
               
        representationOfUMLAttribute.SetName(umlClass.GetName() + "." + umlAttribute.GetName());
        if(umlClass.GetStereotype().toUpperCase().equals("CODELIST") || umlClass.GetStereotype().toUpperCase().equals("ENUMERATION"))
        {
            representationOfUMLAttribute.SetStereotype("Value");
        }
        else
        {
            representationOfUMLAttribute.SetStereotype("Attribute");
        }
        representationOfUMLAttribute.SetNotes(umlAttribute.GetNotes());                           
        representationOfUMLAttribute.Update();
        
        // Adding specific tagged values
        try
        {
            
            ///////////////////////////////
            // Add urn
            ///////////////////////////////
            
            // TODO update script once the urn information is available.
            String urn = "N/A";
            SparxUtilities.addTaggedValue(_taggedValuePrefix + "urn",representationOfUMLAttribute,urn);
           
           // TODO remove this temporary solution once urn information is available
           SparxUtilities.addTaggedValue(_taggedValuePrefix + "GUID",representationOfUMLAttribute,umlAttribute.GetAttributeGUID());   
            
            ///////////////////////////
            // Add the property type
            ///////////////////////////
            
            // Get the type of the attribute           
            Element attributeType = null;
            try
            {
                attributeType = (Element)EAConnection.getInstance().getElementByID(umlAttribute.GetClassifierID());
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + " : The attribute " + SparxUtilities.toString(umlAttribute,umlClass) + " has no valid type.");
            }
            
            if(attributeType == null)
            {
                SparxUtilities.addTaggedValue(_taggedValuePrefix + "propertyType",representationOfUMLAttribute,"N/A");
            }
            else
            {                
                //TODO update script to add the urn rather than the name (when the urn information is available)     
                SparxUtilities.addTaggedValue(_taggedValuePrefix + "propertyType",representationOfUMLAttribute,attributeType.GetName());
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return representationOfUMLAttribute;
    }
    
    /**
     * 
     * @param umlRole
     * @return
     */
    protected Element createRepresentationOfUMLRole(ConnectorEnd umlRole, Connector connector, Element umlClass, Package parentPackage, int attachedElementId)
    {
        
        Element representationOfUMLRole = (Element) parentPackage.GetElements().AddNew("", EA_TYPE_CLASS);
        
        representationOfUMLRole.SetName(umlClass.GetName() + "." + umlRole.GetRole());
        representationOfUMLRole.SetStereotype("Role");
        representationOfUMLRole.SetNotes(umlRole.GetRoleNote());
              
        representationOfUMLRole.Update();
        
        // TODO: add tagged values
        try
        {
            ///////////////////////////////
            // Add urn
            ///////////////////////////////
            
            // TODO update script once the urn information is available.
            String urn = "N/A";
            SparxUtilities.addTaggedValue(_taggedValuePrefix + "urn",representationOfUMLRole,urn);
           
           // TODO remove this temporary solution once urn information is available
           SparxUtilities.addTaggedValue(_taggedValuePrefix + "GUID",representationOfUMLRole,umlClass.GetElementGUID() + " " + connector.GetConnectorGUID());              
            
           
           ///////////////////////////
           // Add the property type
           ///////////////////////////
            
           // Get the type of the role
           Element roleType = null;
           try
           {
               roleType = (Element)EAConnection.getInstance().getElementByID(attachedElementId);
           }
           catch (Exception e)
           {
               EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + " : The property " + SparxUtilities.toString(umlRole) + " has no valid type.");
           }
                         
           if(roleType == null)
           {
               SparxUtilities.addTaggedValue(_taggedValuePrefix + "propertyType",representationOfUMLRole,"N/A");
           }
           else
           {                
               //TODO update script to add the urn rather than the name (when the urn information is available)     
               SparxUtilities.addTaggedValue(_taggedValuePrefix + "propertyType",representationOfUMLRole,roleType.GetName());
           }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return representationOfUMLRole;
    }
    
    /**
     * 
     * @param umlAttribute
     * @param umlClass
     * @param parentPackage
     * @return
     */
    protected Element createRepresentationOfConstraint(Constraint constraint, Element umlClass, Package parentPackage)
    {
        Element representationOfConstraint = (Element) parentPackage.GetElements().AddNew("", EA_TYPE_CLASS);
               
        representationOfConstraint.SetName(constraint.GetName());
        representationOfConstraint.SetNotes(constraint.GetNotes());
        if(constraint.GetType().equals("SBVR"))
        {
            representationOfConstraint.SetStereotype("SBVR Constraint");
        }
        else if(constraint.GetType().equals("OCL"))
        {
            representationOfConstraint.SetStereotype("OCL Constraint");
        }
        else
        {
            representationOfConstraint.SetStereotype("Constraint");
        }
            
        representationOfConstraint.Update();
        
        return representationOfConstraint;
    }
    
    
    
    protected Connector createAggregationBetweenClassAndProperty(Element representationOfUMLClass, Element representationOfUMLProperty, String cardinality)
    {
        
        Connector composition = (Connector)representationOfUMLClass.GetConnectors().AddNew("",EA_CONNECTOR_TYPE_AGGREGATION);
        composition.GetSupplierEnd().SetAggregation(2);
        
        composition.SetSupplierID(representationOfUMLClass.GetElementID());
        composition.SetClientID(representationOfUMLProperty.GetElementID());
        composition.GetClientEnd().SetCardinality(cardinality);
        
        composition.SetStereotype("Contains");
        
        representationOfUMLClass.Update();       

        composition.Update();
        
        return composition;
    }
    
    protected Connector createAggregationBetweenClassAndConstraint(Element representationOfUMLClass, Element representationOfConstraint)
    {
        
        Connector composition = (Connector)representationOfUMLClass.GetConnectors().AddNew("",EA_CONNECTOR_TYPE_AGGREGATION);
        composition.GetSupplierEnd().SetAggregation(2);
        
        composition.SetSupplierID(representationOfUMLClass.GetElementID());
        composition.SetClientID(representationOfConstraint.GetElementID());
        
        composition.SetStereotype("Is constrained by");
        
        representationOfUMLClass.Update();       

        composition.Update();
        
        return composition;
    }
    
    /**
     * 
     *
     */
    protected void createMainPackage()
    {
        Package rootPackage = (Package)EAConnection.getInstance().getRepository().GetModels().AddNew("", EA_TYPE_PACKAGE);
        rootPackage.SetName("Full Traceability constructs");
        
        rootPackage.Update();        
        
        
        _workingPackage = (Package)rootPackage.GetPackages().AddNew("", EA_TYPE_PACKAGE);
        _workingPackage.SetName("[Generated programmatically]");
        
        _workingPackage.Update();
        
    }
    
    /**
     * 
     * @param myPackage
     * @param parentPackageInNewStructure
     * @return
     */
    protected Package createPackageInNewStructure(Package myPackage, Package parentPackageInNewStructure)
    {
        Package packageInNewStructure = (Package)parentPackageInNewStructure.GetPackages().AddNew("", EA_TYPE_PACKAGE);
        packageInNewStructure.SetName(myPackage.GetName());
        packageInNewStructure.SetNotes(myPackage.GetNotes());
        
        packageInNewStructure.Update();
        
        return packageInNewStructure;
    }
    
    /**
     * 
     * @param packageInNewStructure
     * @return
     */
    protected Diagram createDiagramInPackage(Package packageInNewStructure)
    {
        Diagram diagram = (Diagram) packageInNewStructure.GetDiagrams().AddNew("","Logical");
        diagram.SetName(packageInNewStructure.GetName() + "-" + "Mapping");
        
        diagram.Update();
        
        return diagram;
    }
    
    /**
     * 
     * @param representationOfUMLClass
     * @param diagram
     */
    protected void addUMLClassToDiagram(Element representationOfUMLClass, Diagram diagram)   
    {
       DiagramObject diagramObject = (DiagramObject) diagram.GetDiagramObjects().AddNew("", "Link");
       diagramObject.SetElementID(representationOfUMLClass.GetElementID());
       diagramObject.Update();       
    }
    
    /**
     * 
     * @param attribute
     * @return
     */
    protected String getCardinalityForAttribute(Attribute attribute)
    {
        String cardinality = new String();
        String lowerBound = attribute.GetLowerBound();
        String upperBound = attribute.GetUpperBound();
        
        if(lowerBound.equals(upperBound))
            return lowerBound;
        else
            return lowerBound + ".." + upperBound;   
    }

    /**
     * 
     * @param role
     * @return
     */
    protected String getCardinalityForRole(ConnectorEnd role)
    {
        return role.GetCardinality();
    }
    
    
    protected void buildSpecialisations()
    {

    }
}
