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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
import eurocontrol.swim.model.util.constants.MEGAConstants;

/**
 * @author hlepori
 */
public class ExportToMega extends AbstractRule implements AIRMConstants, MEGAConstants {

    /** */
    protected int _currentRowDataModel = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;
    //protected int _currentRowPackage = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;
    protected int _currentRowEntity = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;
    protected int _currentRowAtribute = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;
    //protected int _currentRowClass = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;
    

    /** */
    protected WritableWorkbook _excelWorkbook;
   
    
    /** A list storing the "unique identifiers" of the roles that have been exported to Mega - implemented to avoid exporting a connector twice */
    protected ArrayList _roleGUIDList = new ArrayList();
    
    /** The list of writable Excel sheets in which the AIRM information will be copied */
    protected WritableSheet _dataModelSheet;
    //protected WritableSheet _packageSheet;
    protected WritableSheet _entitySheet;
    protected WritableSheet _attributeSheet;
    //protected WritableSheet _classSheet; 
    
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM] EXPORT TO MEGA - Export the AIRM content to the MEGA Excel template";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getPackagesToBeProcessed()
     */
    public Package[] getPackagesToBeProcessed() {       
        
        Package rootAIRMPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(AIRM_ROOT_PACKAGE_GUID);
        
        return new Package[]{rootAIRMPackage};
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        try {
            //Initialise the Excel file
            initExcelFile();
            // export the AIRM content to Excel
            exportToExcel(myPackage);
            
                            //save Excel file
            _excelWorkbook.write();
            _excelWorkbook.close();

            EAEventManager.getInstance().fireEAEvent(this,
                    "Rule applied successfully");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }
        catch (Exception e) {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this,
                    MESSAGE_ERROR + ": Could not initialise the Excel MEGA template.");
        }
    }
    
    
    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook
        Workbook workbook = Workbook.getWorkbook(new File("./resources/Parametrized_file_AIRM_Export.XLS"));
        _excelWorkbook = Workbook.createWorkbook(new File("./output/AIRM_Export_To_MEGA.xls"), workbook);
        
        _dataModelSheet = _excelWorkbook.getSheet("Data Model");
        //_packageSheet = _excelWorkbook.getSheet("Package");
        _entitySheet = _excelWorkbook.getSheet("Entity (DM)");
        _attributeSheet = _excelWorkbook.getSheet("Attribute (DM)");
        //_classSheet = _excelWorkbook.getSheet("Class");        
    }
    
    
    protected void exportToExcel(Package rootPackage)
    {     
        // Include recursively all the AIRM elements
        processPackageContentRecursively(rootPackage
                .GetPackages(), MEGA_TARGET_ARCHITECTURE, null);
    }
    
    /**
     * 
     * @param packageCollection
     */
    private void processPackageContentRecursively(Collection packageCollection,
            String parentPackageReference, String nafArchitectureViewType) {

        if (packageCollection != null && packageCollection.GetCount() > 0) {
            // local variable to store a subpackage
            Package subPackage = null;
            // local variable to store the element (= a class)
            Element element = null;
            // local variable to store an attribute
            Attribute attribute = null;
            // local variable to store a connector
            Connector connector = null;
            // local variable to store a role
            ConnectorEnd role = null;
            // local variable to store the second role (if any) of a self association 
            ConnectorEnd secondRoleForSelfAssociation = null;
            // local variable to store the type of MEGA elements that is being proccesed (entity,data type, enum) 
            String typeOfMegaElement = null;
            // local variable
            Element targetElement = null;

            //String packageHierarchy = parentPackageName;

            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) {
                subPackage = (Package) packageIter.next();
                
                
                ///////////////////////////////////////////////////
                // Do not process the ParkingArea and the Proposals
                ///////////////////////////////////////////////////
                if (!subPackage.GetPackageGUID().equals(
                        AIRM_PARKING_AREA_PACKAGE_GUID)
                        && !subPackage.GetPackageGUID().equals(
                                AIRM_PROPOSALS_PACKAGE_GUID)) {

                    EAEventManager.getInstance().fireEAEvent(
                            this,
                            "Extracting information from package "
                                    + subPackage.GetName());
 
                    String nafType = nafArchitectureViewType;
                    if(subPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID))
                        nafType = MEGA_NAF_SYSTEM_ARCHITECTURE_VIEW;
                    else if (subPackage.GetPackageGUID().equals(AIRM_INFORMATION_MODEL_PACKAGE_GUID))
                        nafType = MEGA_NAF_OPERATIONAL_ARCHITECTURE_VIEW;
                    else if (subPackage.GetPackageGUID().equals(AIRM_FOUNDATION_PACKAGE_GUID))
                        nafType = MEGA_NAF_ALL_ARCHITECTURE_VIEW;
                    
                            
                    
                    exportPackageToMega(subPackage, parentPackageReference, nafType);

                    /////////////////////////////////////////////////
                    // Process the classes defined in this subpackage
                    /////////////////////////////////////////////////
                    for (Iterator elementsIter = subPackage.GetElements()
                            .iterator(); elementsIter.hasNext();) {

                        element = (Element) elementsIter.next();

                        // The class element is used generically
                        // WARNING: in some cases, an element of type
                        // enumeration
                        // ...
                        if (element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION)) {

                            
                            typeOfMegaElement = MEGA_ENTITY_TYPE;
                            
                            ////////////////////////////////////////////////////////////////////////
                            // Decision 21-Feb-2011: all classes should map to entities. 
                            // No differences in MEGA between datatypes, enumerations and entities
                            ////////////////////////////////////////////////////////////////////////
                            /*
                            if(subPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_ENUMERATION_PACKAGE))
                                typeOfMegaElement = AIRM_EXPORT_TO_MEGA_ENUMERATION_TYPE;
                            else if (subPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_IDENTIFIER_PACKAGE) || subPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_TYPES_PACKAGE))    
                                typeOfMegaElement = AIRM_EXPORT_TO_MEGA_DATATYPE_TYPE;
                            */  
                            exportElementToMega(element, parentPackageReference + "::" + subPackage.GetName(), typeOfMegaElement);

                            ///////////////////////////////////////////////////
                            // Process the class attributes
                            ///////////////////////////////////////////////////
                            for (Iterator attributeIter = element
                                    .GetAttributes().iterator(); attributeIter
                                    .hasNext();) {
                                attribute = (Attribute) attributeIter.next();
                                exportAttributeToMega(attribute,
                                        parentPackageReference + "::" + subPackage.GetName() + "::" + element.GetName());
                            }

                            ///////////////////////////////////////////////////
                            // Process the class associations
                            ///////////////////////////////////////////////////
                            
                            for(Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                            {
                                connector = (Connector) connectorIter.next();
                                role = null;
                                targetElement = null;
                                secondRoleForSelfAssociation = null;
                                
                                ///////////////////////////////////////////////////////
                                // Check if the connector is a self association or not
                                ///////////////////////////////////////////////////////
                                if (connector.GetClientID() == connector.GetSupplierID() )
                                {
                                    targetElement = element;
                                    ////////////////////////////////////////////////////////
                                    // for self associations, the two roles have to be processed  
                                    ////////////////////////////////////////////////////////
                                    role = connector.GetClientEnd();
                                    secondRoleForSelfAssociation = connector.GetSupplierEnd();
                                    
                                }
                                /////////////////////////////////////////
                                // for "normal" associations, process only the role played by the other element   
                                /////////////////////////////////////////
                                else
                                {
                                    ///////////////////////////////////
                                    // Depending on the way the connector is modelled, the target element can be either the source or the target, i.e. the supplier or the client (using EA wording). 
                                    ///////////////////////////////////                                   
	                                if(connector.GetClientID() != element.GetElementID())
	                                {
	                                    role = connector.GetClientEnd();
	                                    targetElement = EAConnection.getInstance().getRepository().GetElementByID(connector.GetClientID());
	                                }
	                                else
	                                {
	                                    role = connector.GetSupplierEnd();
	                                    targetElement = EAConnection.getInstance().getRepository().GetElementByID(connector.GetSupplierID());
	                                }
                                }
                                
                                //////////////////////////////////////////////////////
                                // All the roles do not always correspond to a concept. Example:
                                // [AirportHeliport] +airportHeliport ----- +ARP [ElevatedPoint]
                                // In this example, the role +ARP is obviously a concept which should map to a property of AirportHeliport in MEGA
                                // On the contrary, airportHeliport is not a property of ElevatedPoint and should not be mapped to MEGA.
                                // The following filtering rules are applied:
                                // - the role is not exported if the name of the role is equivalent to the name of the associated class (e.g. airportHeliport ~ AirportHeliport) 
                                // - the role is not exported if no role name is provided
                                //////////////////////////////////////////////////////                               
                                if(isRoleNameProvided(role) && isConcept(role, targetElement))
                                {
                                    String roleGUID = connector.GetConnectorGUID() + " " + targetElement.GetElementGUID() + " " + role.GetRole();
                                    if(!_roleGUIDList.contains(roleGUID))
                                    {
                                        exportRoleToMega(role, parentPackageReference + "::" + subPackage.GetName() + "::" + element.GetName(), roleGUID);
                                    }
                                }
                                // process the second role of a self association, if any
                                if(secondRoleForSelfAssociation != null)
                                {
                                    if(isRoleNameProvided(secondRoleForSelfAssociation) && isConcept(secondRoleForSelfAssociation, targetElement))
                                    {
                                        String secondRoleGUID = connector.GetConnectorGUID() + " " + targetElement.GetElementGUID() + " " + secondRoleForSelfAssociation.GetRole();
                                        if(!_roleGUIDList.contains(secondRoleGUID))
                                        {
                                            exportRoleToMega(secondRoleForSelfAssociation, parentPackageReference + "::" + subPackage.GetName() + "::" + element.GetName(), secondRoleGUID);
                                        }
                                    }
                                }
                            }                            
                        }

                    }
                    processPackageContentRecursively(subPackage.GetPackages(),
                            parentPackageReference + "::" + subPackage.GetName(), nafType);
                }
            }
        }
    }
       
    /**
     * This methods is used when exporting an AIRM role into a MEGA property. It accounts for a filtering mechanism: a role which is nnot considered a concept is not exported. 
     * @param role
     * @param targetElement
     * @return
     */
    private boolean isConcept(ConnectorEnd role, Element targetElement)
    {
        //discard the roles +annotation [Note] 
        if(role.GetRole().equals("annotation"))
            return false;
        // discard the roles +annotatedEntity [Entity]
        if(role.GetRole().startsWith("annotated"))
            return false;
        
        return !(role.GetRole().toUpperCase().equals(targetElement.GetName().toUpperCase()));
    }
    
    /**
     * 
     * @param role
     * @return
     */
    private boolean isRoleNameProvided(ConnectorEnd role)
    {
        return role.GetRole().length()>0;
    }
    
    
    /**
     * 
     * @param myPackage
     */
    protected void exportPackageToMega(Package myPackage, String parentPackageReference, String nafArchitectureViewType) {
        try {
            
            
//            // Step 1 : populate the sheet "Package"
//                        
//            // ADD the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new package not yet imported to MEGA)
//            // Note: for the packages, the MEGA id is stored in the "Flags" property
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_MEGA_ID,_currentRowPackage,myPackage.GetFlags());
//            // MEGA short name = SparxEA Name
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_SHORT_NAME,_currentRowPackage, myPackage.GetName());
//            // MEGA Comment = SparxEA notes
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_COMMENT,_currentRowPackage, EAUtil.removeHTMLTagsFromText(myPackage.GetNotes()));
//            // MEGA Owner Package = absolute reference of the parent project
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_OWNER_PACKAGE,_currentRowPackage, parentPackageReference);
//            // MEGA Abstract = "No" for packages 
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_ABSTRACT,_currentRowPackage, "No");
//            // MEGA Owner Packager = AIRM_MEGA_TARGET_ARCHITECTURE only for the packages ConsolidatedLogicalDataModel, InformationModel and Foundation
//            if (myPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID) || myPackage.GetPackageGUID().equals(AIRM_INFORMATION_MODEL_PACKAGE_GUID)|| myPackage.GetPackageGUID().equals(AIRM_FOUNDATION_PACKAGE_GUID))
//                addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_OWNER_PACKAGER,_currentRowPackage, AIRM_MEGA_TARGET_ARCHITECTURE);
//            // MEGA Stereotype = "Data Model" for all the packages
//            addCell(_packageSheet, AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_STEREOTYPE,_currentRowPackage, "Data Model");
//            ////////////////////////////////////////////////////////////////////////
//            // Decision 21-Feb-2011: add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.  
//            ////////////////////////////////////////////////////////////////////////
//            addCell(_packageSheet,AIRM_EXPORT_TO_MEGA_PACKAGE_COLUMN_GUID,_currentRowPackage, myPackage.GetPackageGUID());
            
            
            // Step 2: populate the sheet "Data Model"

            // ADD the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new package not yet imported to MEGA)
            //addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_MEGA_ID,_currentRowDataModel,myPackage.GetFlags());            
            // For classes, the MEGA identifier is stored in a dedicated tagged value
            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,myPackage))
            {
                addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_MEGA_ID,_currentRowDataModel, SparxUtilities.getContentOfTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,myPackage));
            }   
            
            
            
            // MEGA short name = SparxEA Name 
            addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_SHORT_NAME, _currentRowDataModel, myPackage.GetName());
            // MEGA Comment = SparxEA Notes 
            addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_COMMENT, _currentRowDataModel, SparxUtilities.removeHTMLTagsFromText(myPackage.GetNotes()));
            
            // MEGA Owner Data Model = absolute reference of the parent project - This information is NOT provided for the root packages CLDM, IM and foundation
            if (!myPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID) && !myPackage.GetPackageGUID().equals(AIRM_INFORMATION_MODEL_PACKAGE_GUID)&& !myPackage.GetPackageGUID().equals(AIRM_FOUNDATION_PACKAGE_GUID))
                addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_OWNER_DATAMODEL,_currentRowDataModel, parentPackageReference);
            // MEGA Owner Packager = AIRM_MEGA_TARGET_ARCHITECTURE only for the packages ConsolidatedLogicalDataModel, InformationModel and Foundation
            if (myPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID) || myPackage.GetPackageGUID().equals(AIRM_INFORMATION_MODEL_PACKAGE_GUID) || myPackage.GetPackageGUID().equals(AIRM_FOUNDATION_PACKAGE_GUID))
                addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_OWNER_PACKAGER,_currentRowDataModel, MEGA_TARGET_ARCHITECTURE);
            // MEGA NAF Architecture View
            if(nafArchitectureViewType != null)
                addCell(_dataModelSheet, AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_NAF_ARCHITECTURE_VIEW, _currentRowDataModel, nafArchitectureViewType);
            ////////////////////////////////////////////////////////////////////////
            // Decision 21-Feb-2011: add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.  
            ////////////////////////////////////////////////////////////////////////
            addCell(_dataModelSheet,AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_GUID,_currentRowDataModel, myPackage.GetPackageGUID());
            
            
            
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export package [" + myPackage.GetName()
                            + "] to the MEGA/Excel file");
        }

        //_currentRowPackage++;
        _currentRowDataModel++;
    }
    
    
    /**
     * 
     * @param element
     */
    protected void exportElementToMega(Element element, String packageOwner, String typeOfMegaElement) {
        try {
            // step 1 : populate the sheet "Entity" 
            if (typeOfMegaElement.equals(MEGA_ENTITY_TYPE))
            {
                // Add the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new classes not yet imported to MEGA)
                // For classes, the MEGA identifier is stored in a dedicated tagged value
                if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,element))
                {
                    addCell(_entitySheet, AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_MEGA_ID, _currentRowEntity, SparxUtilities.getContentOfTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,element));
                }                    
                // MEGA short name = SparxEA Name 
                addCell(_entitySheet, AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_SHORT_NAME,_currentRowEntity, element.GetName());
            	// MEGA Comment = SparxEA notes
            	addCell(_entitySheet, AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_COMMENT,_currentRowEntity, SparxUtilities.removeHTMLTagsFromText(element.GetNotes()));
            	// MEGA Owner Data Model = packageOwner
            	addCell(_entitySheet, AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_OWNER_DATA_MODEL,_currentRowEntity, packageOwner);
                ////////////////////////////////////////////////////////////////////////
                // Decision 21-Feb-2011: add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.  
                ////////////////////////////////////////////////////////////////////////
                addCell(_entitySheet,AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_GUID,_currentRowEntity, element.GetElementGUID());

            }
            
//            // step 2: populate the sheet "Class"
//            
//            // Add the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new classes not yet imported to MEGA)
//            // For classes, the MEGA identifier is stored in a dedicated tagged value
//            if(EAUtil.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,element))
//            {
//                addCell(_classSheet, AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_MEGA_ID, _currentRowClass, EAUtil.getContentOfTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,element));
//            }    
//            // MEGA short name = SparxEA Name 
//            addCell(_classSheet, AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_SHORT_NAME,_currentRowClass, element.GetName());
//        	// MEGA Comment = SparxEA notes
//        	addCell(_classSheet, AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_COMMENT,_currentRowClass, EAUtil.removeHTMLTagsFromText(element.GetNotes()));
//        	// MEGA Owner Data Model = packageOwner
//        	addCell(_classSheet, AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_OWNER_DATA_MODEL,_currentRowClass, packageOwner);
//        	// MEGA stereotype = "Entity" for entity and "Data Type"
//        	addCell(_classSheet, AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_STEREOTYPE,_currentRowClass, typeOfMegaElement);
//            ////////////////////////////////////////////////////////////////////////
//            // Decision 21-Feb-2011: add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.  
//            ////////////////////////////////////////////////////////////////////////
//            addCell(_classSheet,AIRM_EXPORT_TO_MEGA_CLASS_COLUMN_GUID,_currentRowClass, element.GetElementGUID());
//        	
//        	
//            _currentRowClass++;
            
     
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export [" + SparxUtilities.toString(element)
                            + "] to the MEGA/Excel file");
        }

        _currentRowEntity++;
        
    }
    
    
    
    /**
     * 
     * @param attribute
     */
    protected void exportAttributeToMega(Attribute attribute, String className) {
        try {
            // Add the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new attributes not yet imported to MEGA)
            // For attributes, the MEGA identifier is stored in a dedicated tagged value
            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,attribute))
            {
                addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_MEGA_ID, _currentRowAtribute, SparxUtilities.getContentOfTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,attribute));
            }              
            // MEGA short name = SparxEA Name 
            addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_SHORT_NAME,_currentRowAtribute, attribute.GetName());
        	// MEGA Comment = SparxEA notes
        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_COMMENT,_currentRowAtribute, SparxUtilities.removeHTMLTagsFromText(attribute.GetNotes()));

//        	// MEGA Datatype (DM) = name of the SparxEA class used as the datatype for the attribute 
//        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_DATATYPE_DM,_currentRowAtribute, attribute.GetType());
//        	// MEGA Data type  = absolute reference of the SparxEA class used as the datatype for the attribute
//        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_DATATYPE,_currentRowAtribute, getAbsoluteReferenceOfDatatype(attribute)); 

        	// MEGA Entity (DM) = absolute reference of the SparxEA class to whioch the attrbute belong
        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_ENTITY_DM,_currentRowAtribute, className);      	
            ////////////////////////////////////////////////////////////////////////
            // Decision 21-Feb-2011: add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.  
            ////////////////////////////////////////////////////////////////////////
            addCell(_attributeSheet,AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_GUID,_currentRowAtribute, attribute.GetAttributeGUID());
            
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export [" + SparxUtilities.toString(attribute)
                            + "] to the MEGA/Excel file");
        }

        _currentRowAtribute++;
    }
    
    
    /**
     * Export a UML role to the Excel Mega Template.
     * A UML role is processed like a UML attribute
     * @param attribute
     */
    protected void exportRoleToMega(ConnectorEnd role, String className, String roleGUID) {
        try {
            // Add the MEGA UID if already provided in the SPARX EA file - left blank otherwise (blank for new attributes not yet imported to MEGA)
            // For attributes, the MEGA identifier is stored in a dedicated tagged value
            if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,role))
            {
                addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_MEGA_ID, _currentRowAtribute, SparxUtilities.getContentOfTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,role));
            }              
            // MEGA short name = SparxEA Name => given by method GetRole() 
            addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_SHORT_NAME,_currentRowAtribute, role.GetRole());
        	// MEGA Comment = SparxEA notes => given by method GetRoleNote()
        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_COMMENT,_currentRowAtribute, SparxUtilities.removeHTMLTagsFromText(role.GetRoleNote()));

        	// MEGA Entity (DM) = absolute reference of the SparxEA class to which the property "belongs"
        	addCell(_attributeSheet, AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_ENTITY_DM,_currentRowAtribute, className);      	

        	////////////////////////////////////////////////////////////////////////
            // Add GUID in a dedicated column. No column header should be provided for now to prevent MEGA from misinterpreting the data.                          
        	// Note: it is not possible to get the GUID of the role using the Sparx EA JAVA API. A unique identifier is required to enable the inclusion of the MEGA Id into the AIRM, once the import in MEGA is realised.
        	// A home made "Sparx unique Id" for the role is built as follows: "{ConnectorGUI} {TargetElementGUID} roleName"
        	addCell(_attributeSheet,AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_GUID,_currentRowAtribute, roleGUID);
            
        	_roleGUIDList.add(roleGUID);
        	
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export role " + SparxUtilities.toString(role) + " to the MEGA/Excel file");
        }

        _currentRowAtribute++;
    }
    
    
    /**
     * 
     * @param column
     * @param row
     * @param str
     */
    protected void addCell(WritableSheet sheet, int column, int row, String str) throws Exception {
        Label label = new Label(column, row, str);
        sheet.addCell(label);
    }
    
    
    /**
     * 
     * @param datatypeId
     * @return
     */
    protected String getAbsoluteReferenceOfDatatype(Attribute attribute)
    {     
        Element datatype = null;
        
        try
        {
            datatype = EAConnection.getInstance().getRepository().GetElementByID(attribute.GetClassifierID());
        }
        catch(Exception e)
        {
            ////////////////////////////////////////////////////////////////////////
            // Decision 21-Feb-2011: 
            // The import of an attribute into MEGA will fail if no datatype is provided. 
            // In the AIRM v1.1.0 (and future versions), the ENUMERATION values are no longer typed with CharacterString. 
            // In order to make sure the import into MEGA is sucessfull, CharacterString is now returned as the reference datatype for any enumerated values, even if this does not reflect the AIRM content.
            // This is a known gap between the AIRM and the MEGA import           
            ////////////////////////////////////////////////////////////////////////
            Element parentClass = EAConnection.getInstance().getRepository().GetElementByID(attribute.GetParentID());
            if(belongsToPackage(parentClass,AIRM_CONSOLIDATED_LOGICAL_MODEL_ENUMERATION_PACKAGE))
            {
                return MEGA_TARGET_ARCHITECTURE + "::Foundation::ISO TC211::ISO 19103 Schema Language::Basic Types::Primitive::Text::CharacterString";
            }
        }      
        
       if(datatype != null)
       {
           String reference = datatype.GetName();
           Package myPackage = EAConnection.getInstance().getRepository().GetPackageByID(datatype.GetPackageID());
           if(myPackage != null)
           {
               reference = updateReferenceOfDatatype(myPackage, reference);
           }
           
           return reference;
       }
       
       EAEventManager.getInstance().fireEAEvent(
               this,
               MESSAGE_ERROR + ": [" + SparxUtilities.toString(attribute)
                       + "] is not typed correctly.");
       return "";
           
    }
    
    /**
     * 
     * @param myPackage
     * @param reference
     * @return
     */
    protected String updateReferenceOfDatatype(Package myPackage, String reference)
    {
       String result = myPackage.GetName() + "::" + reference;
       Package parentPackage = EAConnection.getInstance().getRepository().GetPackageByID(myPackage.GetParentID());
       if(parentPackage != null)
       {
           if(parentPackage.GetPackageGUID().equals(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID))
           {
               return MEGA_TARGET_ARCHITECTURE + "::" + parentPackage.GetName() + "::" + result;
           }
           else if(parentPackage.GetPackageGUID().equals(AIRM_FOUNDATION_PACKAGE_GUID))
           {
               return MEGA_TARGET_ARCHITECTURE + "::" + parentPackage.GetName() + "::" + result;
           }
           else
           {
               result = updateReferenceOfDatatype(parentPackage, result);
           }
       }
       return result;
    }
    
    /**
     * 
     * @param element
     * @param targetPackageGUID
     * @return
     */
    protected boolean belongsToPackage (Element element, String targetPackageGUID)
    {
        Package myPackage = EAConnection.getInstance().getRepository().GetPackageByID(element.GetPackageID());
        return belongsToPackage(myPackage, targetPackageGUID);
    }
    
    /**
     * 
     * @param aPackage
     * @param targetPackageGUID
     * @return
     */
    protected boolean belongsToPackage(Package aPackage, String targetPackageGUID)
    {
        if(aPackage.GetPackageGUID().equals(targetPackageGUID))
            return true;
        
        Package parentPackage = null;
        
        try
        {
            parentPackage = EAConnection.getInstance().getRepository().GetPackageByID(aPackage.GetParentID());
        }
        catch (Exception e)
        {
            return false;
        }
        if(parentPackage == null)
            return false;
        
        return belongsToPackage(parentPackage, targetPackageGUID);
    }
    
}
