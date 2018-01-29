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
import java.util.Iterator;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.sparx.Attribute;
import org.sparx.AttributeTag;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.RoleTag;
import org.sparx.TaggedValue;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.constants.WXXMConstants;

/**
 * @author hlepori
 */
public class ExtractWXXMSpecificInformationtoExcel extends AbstractRule implements WXXMConstants {   
    

    /** */
    protected int _currentRow = 1;
            
    /** */
    protected WritableWorkbook _excelWorkbook;
    /** The list of writable Excel sheets in which the WXXM information will be copied */
    protected WritableSheet _excelSheet;
    

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {       
        return "[WXXM] LEAN AND MEAN WXXM FROM AIRM - 3.Extract WXXM-specific Information";
    }
        
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        //Initialise the Excel file
        try
        {
            initExcelFile();            
            // export the AIRM content to Excel
            exportToExcel(myPackage);
            
            //save Excel file
            _excelWorkbook.write();
            _excelWorkbook.close();
            EAEventManager.getInstance().fireEAEvent(this,"== Done ! == ");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": Could not initialise the Excel file.");
        }
    }
    
   
    
    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook
        Workbook workbook = Workbook.getWorkbook(new File("./resources/WXXM_information.xls"));              
        _excelWorkbook = Workbook.createWorkbook(new File("./WXXM_information.xls"), workbook);
        _excelSheet = _excelWorkbook.getSheet("WXXM_Info");
     
    }
    
    protected void exportToExcel(Package aPackage)
    {     
        // Include recursively all the AIRM elements
        processPackageContentRecursively(aPackage.GetPackages());
    }
    
    /**
     * 
     * @param packageCollection
     */
    private void processPackageContentRecursively(Collection packageCollection) {

        if (packageCollection != null && packageCollection.GetCount() > 0) {
            // local variable to store a subpackage
            Package subPackage = null;

            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) {
                subPackage = (Package) packageIter.next();
                
                // local variable to store the element (= a class)
                Element element = null;
                // local variable to store a connector
                Connector connector = null;
                // local variable to store a role
                ConnectorEnd role = null;
                // local variable to store the second role (if any) of a self association 
                ConnectorEnd secondRoleForSelfAssociation = null;

                
                
                
                ///////////////////////////////////////////////////
                // Do not process the ParkingArea and the Proposals
                ///////////////////////////////////////////////////                                                
                exportPackageToExcel(subPackage);

                /////////////////////////////////////////////////
                // Process the classes defined in this subpackage
                /////////////////////////////////////////////////
                for (Iterator elementsIter = subPackage.GetElements()
                        .iterator(); elementsIter.hasNext();) 
                {
                    
                    // local variable to store an attribute
                    Attribute attribute = null;
                    
                    
                    element = (Element) elementsIter.next();
                    exportElementToExcel(element);

    				///////////////////////////////////////////////////
                    // Process the class attributes
                    ///////////////////////////////////////////////////
                    for (Iterator attributeIter = element
                            .GetAttributes().iterator(); attributeIter
                            .hasNext();) 
                    {
                        attribute = (Attribute) attributeIter.next();
                        exportAttributeToExcel(attribute);
                    }

                    ///////////////////////////////////////////////////
                    // Process the class associations
                    ///////////////////////////////////////////////////
                    
                    for(Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                    {
                        // local variable
                        Element targetElement = null;
                        connector = (Connector) connectorIter.next();
                        role = null;
                        targetElement = null;

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
                        
                        if(role.GetRole().length() >0)
                            exportRoleToExcel(role, connector, targetElement);
                    }                            
                }
            }
            processPackageContentRecursively(subPackage.GetPackages());
        }
    }

    /**
     * 
     * @param myPackage
     */
    protected void exportPackageToExcel(Package myPackage) {
        try {
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_GUID,_currentRow,myPackage.GetPackageGUID());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE,_currentRow,myPackage.GetElement().GetType());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_NAME,_currentRow,myPackage.GetName());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE,_currentRow,myPackage.GetElement().GetStereotype());
            
            _currentRow++;
            
            for (Iterator iter = myPackage.GetElement().GetTaggedValues().iterator();iter.hasNext();)
            {
                TaggedValue taggedValue = (TaggedValue)iter.next();
                exportTaggedValueToExcel(taggedValue, myPackage.GetPackageGUID(), myPackage.GetElement().GetType());
            }
            
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export package [" + myPackage.GetName()
                            + "] to the Excel file");
        }
    }
    
    /**
     * 
     * @param element
     */
    protected void exportElementToExcel(Element element) {
        try {

            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_GUID,_currentRow,element.GetElementGUID());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE,_currentRow,element.GetType());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_NAME,_currentRow,element.GetName());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE,_currentRow,element.GetStereotype());
            
            _currentRow++;
            
            for (Iterator iter = element.GetTaggedValues().iterator();iter.hasNext();)
            {
                TaggedValue taggedValue = (TaggedValue)iter.next();
                exportTaggedValueToExcel(taggedValue, element.GetElementGUID(), element.GetType());
            }

        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export class [" + element.GetName()
                            + "] to the Excel file");
        }
    }
    
    /**
     * 
     * @param element
     */
    protected void exportAttributeToExcel(Attribute attribute) {
        try {

            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_GUID,_currentRow,attribute.GetAttributeGUID());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE,_currentRow,EA_TYPE_ATTRIBUTE);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_NAME,_currentRow,attribute.GetName());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE,_currentRow,attribute.GetStereotype());
            
            _currentRow++;
            
            for (Iterator iter = attribute.GetTaggedValues().iterator();iter.hasNext();)
            {
                AttributeTag taggedValue = (AttributeTag)iter.next();
                exportTaggedValueToExcel(taggedValue, attribute.GetAttributeGUID(), attribute.GetType());
            }
                        
       
            
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export attribute [" + attribute.GetName()
                            + "] to the Excel file");
        }
    }
    
    /**
     * 
     * @param element
     */
    protected void exportRoleToExcel(ConnectorEnd role, Connector connector, Element targetElement) {
        try {
            String roleGUID = connector.GetConnectorGUID() + " " + targetElement.GetElementGUID() + " " + role.GetRole();
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_GUID,_currentRow,roleGUID);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TYPE,_currentRow,EA_TYPE_CONNECTOR_END);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_NAME,_currentRow,role.GetRole());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_STEREOTYPE,_currentRow,role.GetStereotype());
            
            _currentRow++;
            
            for (Iterator iter = role.GetTaggedValues().iterator();iter.hasNext();)
            {
                RoleTag taggedValue = (RoleTag)iter.next();
                exportTaggedValueToExcel(taggedValue, connector.GetConnectorGUID(), connector.GetType());
            }
                        
       
            
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    MESSAGE_ERROR + ": Could not export role [" + role.GetRole()
                            + "] to the Excel file");
        }
    }
    
    
    /**
     * 
     * @param myPackage
     */
    protected void exportTaggedValueToExcel(TaggedValue taggedValue, String parentGUID, String parentType)
    {
        try
        {
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_NAME,_currentRow,taggedValue.GetName());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_VALUE,_currentRow,taggedValue.GetValue());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_GUID,_currentRow,parentGUID);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_TYPE,_currentRow,parentType);
            _currentRow++;
        }
        catch (Exception e) {
        EAEventManager.getInstance().fireEAEvent(
                this,
                MESSAGE_ERROR + ": Could not export tagged value [" + taggedValue.GetName()
                        + "] to the Excel file");
        }
    }
    
    /**
     * 
     * @param myPackage
     */
    protected void exportTaggedValueToExcel(AttributeTag taggedValue, String parentGUID, String parentType)
    {
        try
        {
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_NAME,_currentRow,taggedValue.GetName());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_VALUE,_currentRow,taggedValue.GetValue());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_GUID,_currentRow,parentGUID);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_TYPE,_currentRow,parentType);
            _currentRow++;
        }
        catch (Exception e) {
        EAEventManager.getInstance().fireEAEvent(
                this,
                MESSAGE_ERROR + ": Could not export tagged value [" + taggedValue.GetName()
                        + "] to the Excel file");
        }
    }

    /**
     * 
     * @param myPackage
     */
    protected void exportTaggedValueToExcel(RoleTag taggedValue, String parentGUID, String parentType)
    {
        try
        {
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_NAME,_currentRow,taggedValue.GetTag());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_VALUE,_currentRow,taggedValue.GetValue());
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_GUID,_currentRow,parentGUID);
            addCell(_excelSheet, WXXM_EXPORT_TO_EXCEL_COLUMN_TAGGED_VALUE_PARENT_TYPE,_currentRow,parentType);
            _currentRow++;
        }
        catch (Exception e) {
        EAEventManager.getInstance().fireEAEvent(
                this,
                MESSAGE_ERROR + ": Could not export tagged value [" + taggedValue.GetTag()
                        + "] to the Excel file");
        }
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

}
