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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jxl.Cell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;

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
public class AIRM_to_WXXM_Rule extends AbstractRule implements WXXMConstants, AIRMConstants {
    
    private HashMap _attributesToBeChangedToAssociations = new HashMap();
    private HashMap _associationsToBeChangedToAttributes = new HashMap();
    
    protected Workbook _excelWorkbook;
    protected Sheet _excelSheet;
    protected int _currentRow = 1;
    
    protected HashMap _listOfNonMETelementsPerPackage = new HashMap(); 
    
    protected Package _metPackage;

    

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM v1.1.1] LEAN AND MEAN WXXM FROM AIRM - 2.Generate WXXM from AIRM";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        _metPackage = myPackage;
        
        EAEventManager.getInstance().fireEAEvent(this,"== Include WXXM-specific information...");
        includeWXXMspecificInformation();
        
        EAEventManager.getInstance().fireEAEvent(this,"== Apply AIRM=>WXXM mapping rules...");
        processPackageContentRecursively(_metPackage.GetPackages());                
        replaceAttributesByAssociations();                
        replaceAssociationsByAttributes();
                        
        EAEventManager.getInstance().fireEAEvent(this,"== Check MET package dependencies & Clean-up model...");               
        cleanUpModel();
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
        
        // Update package
//        boolean isUpdateSuccessFull = aPackage.Update();
//        if(!isUpdateSuccessFull)
//        {
//            EAEventManager.getInstance().fireEAEvent(this,EA_MESSAGE_ERROR + ": Could not properly update package " + aPackage.GetName());
//        }
    }
    
	/**
	 * 
	 * @param element
	 */
    private void updateElement(Element element) {
        
        if(element.GetStereotype() == null || element.GetStereotype().length() == 0)
        {
            if(SparxUtilities.belongsToPackage(element,AIRM_CONSOLIDATED_LOGICAL_MODEL_SUBJECT_FIELDS_PACKAGE_GUID))
                element.SetStereotype(WXXM_STEREOTYPE_FEATURE_TYPE);
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
        // Update the attribute multiplicity
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
        if(multiplicity.equals("1"))
        {
            // The default attribute multiplicity in the AIRM is [0..1] but is captured in SPARX as lowerBound=upperBound=1.
            // In WXXM, [0..1] will be displayed
            attribute.SetLowerBound("0");
            attribute.SetUpperBound("1");
        }
        
        /////////////////////
        // Check the validity of the datatype
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
	        else if(SparxUtilities.belongsToPackage(datatype, AIRM_CONSOLIDATED_LOGICAL_MODEL_DATATYPES_PACKAGE_GUID) || SparxUtilities.belongsToPackage(datatype, AIRM_FOUNDATION_PACKAGE_GUID))
	        {
	            _attributesToBeChangedToAssociations.put(attribute,new Object[] {element,datatype, multiplicity});                  
	        }
	        
	        if(datatype != null)
	        {
	            Package aPackage = EAConnection.getInstance().getRepository().GetPackageByID(datatype.GetPackageID());
                if(_listOfNonMETelementsPerPackage.keySet().contains(aPackage.GetPackageGUID()))
                {
                    ArrayList list = (ArrayList)_listOfNonMETelementsPerPackage.get(aPackage.GetPackageGUID());
                    if(!list.contains(datatype.GetElementGUID()))
                        list.add(datatype.GetElementGUID());
                }
                else
                {
                    ArrayList list = new ArrayList();
                    list.add(datatype.GetElementGUID());
                    _listOfNonMETelementsPerPackage.put(aPackage.GetPackageGUID(),list);
                }  
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
                if (SparxUtilities.belongsToPackage(attachedElement,AIRM_CONSOLIDATED_LOGICAL_MODEL_CONSTRUCTED_TYPES_PACKAGE_GUID) || SparxUtilities.belongsToPackage(attachedElement,AIRM_CONSOLIDATED_LOGICAL_MODEL_SUBJECT_FIELDS_PACKAGE_GUID))
                {
                    _associationsToBeChangedToAttributes.put(connector,new Object[]{role,element, new Integer(id)});
                }
                
                if(!SparxUtilities.belongsToPackage(attachedElement,_metPackage.GetPackageGUID()) && !SparxUtilities.belongsToPackage(attachedElement,AIRM_FOUNDATION_PACKAGE_GUID))
                {
                    Package aPackage = EAConnection.getInstance().getRepository().GetPackageByID(attachedElement.GetPackageID());
                    if(_listOfNonMETelementsPerPackage.keySet().contains(aPackage.GetPackageGUID()))
                    {
                        ArrayList list = (ArrayList)_listOfNonMETelementsPerPackage.get(aPackage.GetPackageGUID());
                        if(!list.contains(attachedElement.GetElementGUID()))
                            list.add(attachedElement.GetElementGUID());
                    }
                    else
                    {
                        ArrayList list = new ArrayList();
                        list.add(attachedElement.GetElementGUID());
                        _listOfNonMETelementsPerPackage.put(aPackage.GetPackageGUID(),list);
                    }                        
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
            if(datatype.GetStereotype().equals(WXXM_STEREOTYPE_FEATURE_TYPE) || datatype.GetStereotype().equals(WXXM_STEREOTYPE_ENUMERATION) || SparxUtilities.belongsToPackage(datatype,AIRM_CONSOLIDATED_LOGICAL_MODEL_ENUMERATION_PACKAGE) || SparxUtilities.belongsToPackage(datatype,AIRM_FOUNDATION_PACKAGE_GUID))
            {
                replaceAttributeByAssociation(attribute,datatype,source, multiplicity);
                                
                try
                {
    	            //EAEventManager.getInstance().fireEAEvent(this,"Replacing attribute " + EAUtil.toString(attribute,source) + " by an association.");
                    SparxUtilities.deleteAttribute(attribute,source);
                }
                catch(Exception e)
                {
                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete attribute " + SparxUtilities.toString(attribute,source));
                }
            }
            else if (datatype.GetStereotype().equals(WXXM_STEREOTYPE_TYPE))
            {
                // do nothing   
            }
            else
            {
                // do nothing
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
        newConnector.GetSupplierEnd().SetNavigable(EA_CONNECTOR_NAVIGABILITY_NON_NAVIGABLE);               
                
        newConnector.GetClientEnd().SetRole(attribute.GetName());
        newConnector.GetClientEnd().SetRoleNote(attribute.GetNotes());
        newConnector.GetClientEnd().SetNavigable(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
        
        //String multiplicity = attribute.GetLowerBound() + ".." + attribute.GetUpperBound(); 
        if (multiplicity.equals("0..n") || multiplicity.equals("0..*"))
        {
            newConnector.GetClientEnd().SetCardinality(multiplicity);
            // do not add multiplicity to the associations, to comply with AIRM rule 26 stating that Relationships shall, by default, be represented with multiplicity [0..*] 
        }
        else if (multiplicity.equals("1"))
        {
            // This value represents the default Sprax EA value for attribute multiplicity, which should be interpreted as [0..1] in an AIRM context
            // Add multiplicty 0..1 to association
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
            Element datatype = EAConnection.getInstance().getRepository().GetElementByID(datatypeId);
            
            if(datatype.GetStereotype().equals(WXXM_STEREOTYPE_TYPE) || SparxUtilities.belongsToPackage(datatype,AIRM_CONSOLIDATED_LOGICAL_MODEL_TYPES_PACKAGE))
            {
                Attribute newAttribute = (Attribute)element.GetAttributes().AddNew(role.GetRole(), EA_TYPE_ATTRIBUTE);            
                newAttribute.SetClassifierID(datatypeId);
                
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
    	            //EAEventManager.getInstance().fireEAEvent(this,"Replacing association " + EAUtil.toString(connector) + " by an attribute.");
                    SparxUtilities.deleteConnector(connector,element);
                }
                catch(Exception e)
                {
                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not delete association " + SparxUtilities.toString(connector));
                }
            }   
        }
    }
    
    /**
     * 
     *
     */
    public void includeWXXMspecificInformation()
    {
        try
        {
            loadExcelFile();
            addWXXMInformationToModel();
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR+ ": Could not add WXXM information");
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    private void loadExcelFile() throws Exception {
        //Initialise the excel workbook
        JFileChooser fileChooser = new JFileChooser(new File(System
                .getProperty("user.dir")));
        fileChooser.setName("Please select an Excel file");
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File arg0) {
                return arg0.isDirectory() || arg0.getName().endsWith(".xls")
                        || arg0.getName().endsWith(".XLS");
            }

            public String getDescription() {
                return "Excel File (*.xls)";
            }
        });

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.OPEN_DIALOG) {

            _excelWorkbook = Workbook
                    .getWorkbook(fileChooser.getSelectedFile());
            _excelSheet = _excelWorkbook.getSheet("WXXM_Info");
        }
    }

    
    private void addWXXMInformationToModel() 
    {        
        boolean loop = true;

        while (loop) 
        {
            try 
            {               
                String GUID = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_GUID,_currentRow, _excelSheet);
                String taggedValueName = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_NAME,_currentRow, _excelSheet);
                               
                boolean isGUIDPopulated = !((GUID == null) || (GUID.length() == 0)); 
                boolean isTaggedValuePopulated = !((taggedValueName == null) || (taggedValueName.length() == 0));
                
                if (!isGUIDPopulated && !isTaggedValuePopulated) 
                {
                    loop = false;
                } 
                else 
                { 
                    if(isGUIDPopulated)
                    {
                        String type = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE,_currentRow, _excelSheet);
                        String name = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_NAME,_currentRow, _excelSheet);
                        String stereotype = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE,_currentRow, _excelSheet);
                        
                        if(type.equals(EA_TYPE_PACKAGE))
                        {
                            Package myPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(GUID);
                            if(myPackage != null)
                            {
                                myPackage.SetName(name);
                                myPackage.SetStereotypeEx(stereotype);
                                
                                boolean isUpdateSucessful = myPackage.Update();
                                if (!isUpdateSucessful)
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM information to package " + myPackage.GetName());
                            }
                            else
                            {
                                // Do nothing
                            }
                        }
                        else if (type.equals(EA_TYPE_CLASS) || type.equals(EA_TYPE_ENUMERATION))
                        {
                            Element element = EAConnection.getInstance().getRepository().GetElementByGuid(GUID);
                            if(element != null)
                            {
                                // Do not change the name because it could create a gap with the AIRM glossary
                                element.SetStereotype(stereotype);
                                
                                boolean isUpdateSucessful = element.Update();
                                if (!isUpdateSucessful)
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM information to " + SparxUtilities.toString(element));
                            }
                            else
                            {
                                // Do nothing
                            }                            
                        }
                        else if (type.equals(EA_TYPE_ATTRIBUTE))
                        {
                            Attribute attribute = EAConnection.getInstance().getRepository().GetAttributeByGuid(GUID);
                            if(attribute != null)
                            {
                                // Do not change the name because it could create a gap with the AIRM glossary
                                attribute.SetStereotype(stereotype);
                                
                                boolean isUpdateSucessful = attribute.Update();
                                if (!isUpdateSucessful)
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM information to attribute " + SparxUtilities.toString(attribute));
                            }
                            else
                            {
                                // Do nothing
                            }
                        }
                        else if (type.equals(EA_TYPE_CONNECTOR_END))
                        {
                            if (GUID.matches("\\{.*\\} .*"))
                            {
                                String [] splitIds = GUID.split("\\s");
                                String SPARX_CONNECTOR_GUID = splitIds[0];
                                String SPARX_TARGET_ELEMENT_GUID = splitIds[1];
                                String roleName = splitIds[2];
                                
                                Connector connector = EAConnection.getInstance().getRepository().GetConnectorByGuid(SPARX_CONNECTOR_GUID);
                                Element targetElement = EAConnection.getInstance().getRepository().GetElementByGuid(SPARX_TARGET_ELEMENT_GUID);
                                ConnectorEnd role = null;
                                if(connector!= null && connector.GetClientID() == targetElement.GetElementID() && connector.GetClientEnd().GetRole().equals(roleName))
                                {
                                    role = connector.GetClientEnd();
                                }
                                else if(connector!= null && connector.GetSupplierID() == targetElement.GetElementID() && connector.GetSupplierEnd().GetRole().equals(roleName))
                                {
                                    role = connector.GetSupplierEnd();
                                }
                                
                                if(role != null)
                                {
                                    // Do not change the name because it could create a gap with the AIRM glossary
                                    role.SetStereotype(stereotype);
                                    
                                    boolean isUpdateSucessful = role.Update();
                                    if (!isUpdateSucessful)
                                        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM information to role " + SparxUtilities.toString(role));
                 
                                }
                            }
                        }         
                    }
                    if(isTaggedValuePopulated)
                    {
                        String taggedValueValue = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_VALUE,_currentRow, _excelSheet);
                        String taggedValueValueParentGUID = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_GUID,_currentRow, _excelSheet);
                        String taggedValueValueParentType = getCellInformation(WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_TYPE,_currentRow, _excelSheet);
                        
                        if(taggedValueValueParentType.equals(EA_TYPE_PACKAGE))
                        {
                            Package myPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(taggedValueValueParentGUID);
                            if(myPackage != null)
                            {
                                try
                                {
                                    SparxUtilities.addTaggedValue(taggedValueName,myPackage,taggedValueValue);
                                }
                                catch(Exception e)
                                {
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM tagged value to package " + myPackage.GetName());
                                }
                            }
                        }
                        else if(taggedValueValueParentType.equals(EA_TYPE_CLASS) || taggedValueValueParentType.equals(EA_TYPE_ENUMERATION))
                        {
                            Element element = EAConnection.getInstance().getRepository().GetElementByGuid(GUID);
                            if(element != null)
                            {
                                try
                                {
                                    SparxUtilities.addTaggedValue(taggedValueName,element,taggedValueValue);
                                }
                                catch(Exception e)
                                {
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM tagged value to " + SparxUtilities.toString(element));
                                } 
                            }

                        }
                        else if(taggedValueValueParentType.equals(EA_TYPE_ATTRIBUTE))
                        {
                            Attribute attribute = EAConnection.getInstance().getRepository().GetAttributeByGuid(GUID);
                            if(attribute != null)
                            {
                                try
                                {
                                    SparxUtilities.addTaggedValue(taggedValueName,attribute,taggedValueValue);
                                }
                                catch(Exception e)
                                {
                                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM tagged value to " + SparxUtilities.toString(attribute));
                                } 
                            }
                        }
                        else if(taggedValueValueParentType.equals(EA_TYPE_CONNECTOR_END))
                        {
                            if (GUID.matches("\\{.*\\} .*"))
                            {
                                String [] splitIds = GUID.split("\\s");
                                String SPARX_CONNECTOR_GUID = splitIds[0];
                                String SPARX_TARGET_ELEMENT_GUID = splitIds[1];
                                String roleName = splitIds[2];
                                
                                Connector connector = EAConnection.getInstance().getRepository().GetConnectorByGuid(SPARX_CONNECTOR_GUID);
                                Element targetElement = EAConnection.getInstance().getRepository().GetElementByGuid(SPARX_TARGET_ELEMENT_GUID);
                                ConnectorEnd role = null;
                                if(connector!= null && connector.GetClientID() == targetElement.GetElementID() && connector.GetClientEnd().GetRole().equals(roleName))
                                {
                                    role = connector.GetClientEnd();
                                }
                                else if(connector!= null && connector.GetSupplierID() == targetElement.GetElementID() && connector.GetSupplierEnd().GetRole().equals(roleName))
                                {
                                    role = connector.GetSupplierEnd();
                                }
                                
                                if(role != null)
                                {
                                    try
                                    {
                                        SparxUtilities.addTaggedValue(taggedValueName,role,taggedValueValue);
                                    }
                                    catch(Exception e)
                                    {
                                        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not add WXXM tagged value to " + SparxUtilities.toString(role));
                                    } 
                                }
                            }
                        }
                    }                                       
                }
                _currentRow++;
            }
            catch(Exception e)
            {
                loop = false;
            }
        }
    }
    
    /**
     * 
     * @param column
     * @return @throws
     *         Exception
     */
    private String getCellInformation(int column, int row, Sheet sheet) {
        Cell cell = sheet.getCell(column, row);
        if (cell instanceof LabelCell) {
            return ((LabelCell) cell).getString();
        } else if (cell instanceof EmptyCell) {
            return "";
        } else {
            // ASCII code for 'A' is 65
            int asciiCode = column + 65;
            char col = (char) asciiCode;
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    "WARNING: The cell (" + col + "," + row
                            + ") could not be evaluated");
            return "";
        }
    }
    
    
    /**
     * 
     *
     */
    public void cleanUpModel()
    {
        Package cdlmPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID);
        if(cdlmPackage != null)
        {
            cleanUpElementsRecursively(cdlmPackage.GetPackages());
            cleanUpPackagesRecursively(cdlmPackage.GetPackages());
        }
    }
    
    private void cleanUpElementsRecursively(Collection packageCollection)
    {
        if (packageCollection!= null && packageCollection.GetCount() > 0) 
        {
            // local variable to store a subpackage
            Package subPackage = null;                                    
            
            for (Iterator packageIter = packageCollection.iterator(); packageIter.hasNext();) 
            {
                subPackage = (Package) packageIter.next();                
                int numberOfClasses = SparxUtilities.getClassCount(subPackage);

                
                
                // DO NOT PROCESS THE MET PACKAGE nor its subpackages
                if(subPackage.GetPackageGUID().equals(_metPackage.GetPackageGUID()) || SparxUtilities.belongsToPackage(subPackage,_metPackage.GetPackageGUID()))
                {
                    // do nothing
                }
                else
                {
                    if(_listOfNonMETelementsPerPackage.keySet().contains(subPackage.GetPackageGUID()))
                    {
                        ArrayList listOfElementsGUIDToBeKept = (ArrayList)_listOfNonMETelementsPerPackage.get(subPackage.GetPackageGUID());
                        short numberOfElementsToBeKept = (short)listOfElementsGUIDToBeKept.size();
                        //EAEventManager.getInstance().fireEAEvent(this,"Package " + subPackage.GetName() + " contains " + numberOfElementsToBeKept + " elements linked to the Met package. " + (numberOfClasses - numberOfElementsToBeKept) + " classes will now be removed.");                        
                        
                        short index = 0;                   
                        short numberOfElementsRemoved = 0;
                        boolean loop = true;
                        
                        while (loop)
                        {
                            subPackage.GetElements().Refresh();
                            
                            if(numberOfElementsRemoved == numberOfClasses - numberOfElementsToBeKept)
                            {
                                loop = false;
                            }
                            else
                            {   
                                Object obj = subPackage.GetElements().GetAt(index);
                                if(obj instanceof Element)
                                {
                                    Element element = (Element)subPackage.GetElements().GetAt(index);
                                    if(listOfElementsGUIDToBeKept.contains(element.GetElementGUID()))
                                    {
                                        // keep element - move to the next one
                                        index++;
                                    }
                                    else
                                    {
                                        // delete element
                                        try
                                        {
                                            //System.out.println("==> Deleting : " + EAUtil.toString(element) + " from package " + subPackage.GetName());
                                            SparxUtilities.deleteElement(element,subPackage);
                                            numberOfElementsRemoved++;
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            loop = false;
                                        }
                                        // set the index to 0 so that the altered list of elements is correctly processed
                                        index = 0;
                                    }
                                }
                            }
                        }                   
                    } 
                    else
                    {
                        //EAEventManager.getInstance().fireEAEvent(this,"Package " + subPackage.GetName() + " contains no element linked to the Met package. All classes will now be removed.");
                        while(subPackage.GetElements().GetCount() > 0)
                        {
                            subPackage.GetElements().Delete((short)0);
                            subPackage.Update();
                            subPackage.GetElements().Refresh();                            
                        }
                    }
                }
                cleanUpElementsRecursively(subPackage.GetPackages());                          
            }
        }           
    }
    
    private void cleanUpPackagesRecursively(Collection packageCollection)
    {
            // local variable to store a subpackage
            Package subPackage = null;                                    
            
            for (Iterator packageIter = packageCollection.iterator(); packageIter.hasNext();) 
            {
                subPackage = (Package) packageIter.next();
                if(subPackage.GetPackages().GetCount() >0)
                {
                    cleanUpPackagesRecursively(subPackage.GetPackages());
                }
                
                subPackage.GetPackages().Refresh();
                int numberOfClasses = SparxUtilities.getClassCount(subPackage);
                short numberOfSubPackages = subPackage.GetPackages().GetCount();
                if(numberOfClasses == 0 && numberOfSubPackages == 0)
                {
                    try
                    {
                        SparxUtilities.deletePackage(subPackage);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }                
            }
    }
//    
//
//    /* (non-Javadoc)
//     * @see eurocontrol.swim.model.rules.Rule#check()
//     */
//    public void check() {
//        EAEventManager.getInstance().fireEAEvent(this,SEPARATOR);
//        EAEventManager.getInstance().fireEAEvent(this,"Checking rule " + getName());
//        
//        EAEventManager.getInstance().fireEAEvent(this,EA_MESSAGE_WARNING + ": NOT IMPLEMENTED YET !");
//        EAEventManager.getInstance().fireEAEvent(this,SEPARATOR);
//    }
//    
    
}
