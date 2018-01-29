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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jxl.Cell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
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
public class ImportGuidMEGAtoAIRM extends AbstractRule implements AIRMConstants {

    /** */
    protected int _currentRowDataModel = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;

    protected int _currentRowPackage = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;

    protected int _currentRowEntity = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;

    protected int _currentRowAtribute = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;

    protected int _currentRowClass = AIRM_EXPORT_TO_MEGA_FIRST_EDITABLE_ROW;

    protected Workbook _excelWorkbook;

    /** The list of Excel sheets containing the MEGA and Sparx GUID */
    protected Sheet _dataModelSheet;

    protected Sheet _entitySheet;

    protected Sheet _attributeSheet;

    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM] EXPORT TO MEGA - Import MEGA Unique Identifier to the AIRM";
    }
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getPackagesToBeProcessed()
     */
    public Package[] getPackagesToBeProcessed() {               
       return null;
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuletoModel()
     */
    public void applyRuletoModel() {
        try {
            //Load the Excel file
            loadExcelFile();

            if (_dataModelSheet == null) {
                EAEventManager
                        .getInstance()
                        .fireEAEvent(
                                this,
                                MESSAGE_ERROR
                                        + ": Could not load the Excel sheet [Data Model]");
            }
            if (_entitySheet == null) {
                EAEventManager
                        .getInstance()
                        .fireEAEvent(
                                this,
                                MESSAGE_ERROR
                                        + ": Could not load the Excel sheet [Entity (DM)]");
            }
            if (_attributeSheet == null) {
                EAEventManager
                        .getInstance()
                        .fireEAEvent(
                                this,
                                MESSAGE_ERROR
                                        + ": Could not load the Excel sheet [Attribute (DM)]");
            }
            if (_dataModelSheet != null && _entitySheet != null
                    && _attributeSheet != null) {
                // Add the MEGA UID to the AIRM
                addMEGAIdentifierToAIRM();

                EAEventManager.getInstance().fireEAEvent(this,
                        "Rule applied successfully");
                EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
            } else {
                EAEventManager.getInstance().fireEAEvent(
                        this,
                        MESSAGE_ERROR
                                + ": Could not load the MEGA Excel file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this,
                    MESSAGE_ERROR + ": Could not load the MEGA Excel file.");
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
            _dataModelSheet = _excelWorkbook.getSheet("Data Model");
            _entitySheet = _excelWorkbook.getSheet("Entity (DM)");
            _attributeSheet = _excelWorkbook.getSheet("Attribute (DM)");
        }
    }

    /**
     * 
     *  
     */
    private void addMEGAIdentifierToAIRM() {
        addMEGAIdentifierToAIRMPackages();
        addMEGAIdentifierToAIRMClasses();
        addMEGAIdentifierToAIRMAttributesAndRoles();
    }

    private void addMEGAIdentifierToAIRMPackages() {
        boolean loop = true;
        EAEventManager.getInstance().fireEAEvent(
                this,
                "Adding MEGA identifiers to Packages");
        while (loop) {
            try {
                String MEGA_ID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_MEGA_ID,
                        _currentRowDataModel, _dataModelSheet);
                String SPARX_GUID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_GUID,
                        _currentRowDataModel, _dataModelSheet);

                if (SPARX_GUID == null || SPARX_GUID.length() == 0) {
                    loop = false;
                } else {
                    Package myPackage = EAConnection.getInstance()
                            .getRepository().GetPackageByGuid(SPARX_GUID);
                    if(myPackage == null)
                    {
                        String SHORT_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_DATAMODEL_COLUMN_SHORT_NAME,_currentRowDataModel, _dataModelSheet);                       
                        EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": No package [" + SHORT_NAME + "] found in the AIRM having Sparx GUID = " + SPARX_GUID);                        
                    }
                    else
                    {                        
	                    try {
	                        if (SparxUtilities
	                                .isTaggedValuePopulated(
	                                        AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                        myPackage)) {
	                            SparxUtilities
	                                    .updateTaggedValue(
	                                            AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                            myPackage, MEGA_ID);
	                        } else {
	                            SparxUtilities
	                                    .addTaggedValue(
	                                            AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                            myPackage, MEGA_ID);
	                        }
	                    } catch (Exception e) {
	                        EAEventManager
	                                .getInstance()
	                                .fireEAEvent(
	                                        this,
	                                        MESSAGE_ERROR
	                                                + ": Could not add MEGA identifier to package "
	                                                + myPackage.GetName());                    }
                    }
                    _currentRowDataModel++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                loop = false;
            }
        }
    }

    private void addMEGAIdentifierToAIRMClasses() {
        boolean loop = true;

        EAEventManager.getInstance().fireEAEvent(
                this,
                "Adding MEGA identifiers to Classes");
        
        while (loop) {
            try {
                String MEGA_ID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_MEGA_ID,
                        _currentRowEntity, _entitySheet);
                String SPARX_GUID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_GUID,
                        _currentRowEntity, _entitySheet);

                if (SPARX_GUID == null || SPARX_GUID.length() == 0) {
                    loop = false;
                } else {
                    Element element = EAConnection.getInstance()
                            .getRepository().GetElementByGuid(SPARX_GUID);
                    if(element == null)
                    {
                        String SHORT_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_ENTITY_COLUMN_SHORT_NAME,_currentRowEntity, _entitySheet);                       
                        EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": No class [" + SHORT_NAME + "] found in the AIRM having Sparx GUID = " + SPARX_GUID); 
                    }
                    else
                    {
	                    try {
	                        if (SparxUtilities
	                                .isTaggedValuePopulated(
	                                        AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                        element)) {
	                            SparxUtilities
	                                    .updateTaggedValue(
	                                            AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                            element, MEGA_ID);
	                        } else {
	                            SparxUtilities
	                                    .addTaggedValue(
	                                            AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER,
	                                            element, MEGA_ID);
	                        }
	                    } catch (Exception e) {
	                        EAEventManager
	                                .getInstance()
	                                .fireEAEvent(
	                                        this,
	                                        MESSAGE_ERROR
	                                                + ": Could not add MEGA identifier to element "
	                                                + SparxUtilities.toString(element));
	                    }
                    }
                    _currentRowEntity++;
                }
            } catch (Exception e) {
                loop = false;
            }
        }
    }

    private void addMEGAIdentifierToAIRMAttributesAndRoles() {
        boolean loop = true;

        EAEventManager.getInstance().fireEAEvent(
                this,
                "Adding MEGA identifiers to Attributes and Roles");
        
        while (loop) {
            try {
                String MEGA_ID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_MEGA_ID,
                        _currentRowAtribute, _attributeSheet);
                String SPARX_GUID = getCellInformation(
                        AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_GUID,
                        _currentRowAtribute, _attributeSheet);

                if (SPARX_GUID == null || SPARX_GUID.length() == 0) {
                    loop = false;
                } else {
                    
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    //check how the "GUID" is formed to know whether an attribute or a role should be processed.
                    ///////////////////////////////////////////////////////////////////////////////////////////
                    if (SPARX_GUID.matches("\\{.*\\} .*"))
                    {
                        
                        // this is a role                 
                        String [] splitIds = SPARX_GUID.split("\\s");
                        String SPARX_CONNECTOR_GUID = splitIds[0];
                        String SPARX_TARGET_ELEMENT_GUID = splitIds[1];
                        String roleName = splitIds[2];
                        
                        Connector connector = EAConnection.getInstance().getRepository().GetConnectorByGuid(SPARX_CONNECTOR_GUID);
                        Element targetElement = EAConnection.getInstance().getRepository().GetElementByGuid(SPARX_TARGET_ELEMENT_GUID);
                        ConnectorEnd role = null;
                        if(connector.GetClientID() == targetElement.GetElementID() && connector.GetClientEnd().GetRole().equals(roleName))
                        {
                            role = connector.GetClientEnd();
                        }
                        else if(connector.GetSupplierID() == targetElement.GetElementID() && connector.GetSupplierEnd().GetRole().equals(roleName))
                        {
                            role = connector.GetSupplierEnd();
                        }
                        
                        if(role != null)
                        {
    	                    try {
    	                        if (SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, role)) 
    	                        {
    	                            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, role, MEGA_ID);
    	                        } 
    	                        else 
    	                        {
    	                            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, role, MEGA_ID);
    	                        }
    	                    } 
    	                    catch (Exception e) 
    	                    {
    	                        EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": Could not add MEGA identifier to " + SparxUtilities.toString(role));
    	                    }
                        }
                        else
                        {
                            String SHORT_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_SHORT_NAME,_currentRowAtribute, _attributeSheet);                       
                            String ENTITY_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_ENTITY_DM,_currentRowAtribute, _attributeSheet);
                            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": No property (UML role) [" + SHORT_NAME + "] found in the AIRM for entity " + ENTITY_NAME + " and having Sparx GUID = " + SPARX_GUID); 
                        }
                    }
                    else
                    {                    
                        // this is an attribute
	                    Attribute attribute = EAConnection.getInstance().getRepository().GetAttributeByGuid(SPARX_GUID);
	                    
	                    if(attribute == null)
	                    {
                            String SHORT_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_SHORT_NAME,_currentRowAtribute, _attributeSheet);                       
                            String ENTITY_NAME = getCellInformation(AIRM_EXPORT_TO_MEGA_ATTRIBUTE_COLUMN_ENTITY_DM,_currentRowAtribute, _attributeSheet);
                            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": No property (UML attribute) [" + SHORT_NAME + "] found in the AIRM for entity " + ENTITY_NAME + " and having Sparx GUID = " + SPARX_GUID); 

	                    }
	                    else
	                    {	                    
		                    try {
		                        if (SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, attribute)) 
		                        {
		                            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, attribute, MEGA_ID);
		                        } 
		                        else 
		                        {
		                            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_MEGA_UNIQUE_IDENTIFIER, attribute, MEGA_ID);
		                        }
		                    } 
		                    catch (Exception e) 
		                    {
		                        EAEventManager.getInstance().fireEAEvent(this, MESSAGE_ERROR + ": Could not add MEGA identifier to " + SparxUtilities.toString(attribute));
		                    }
	                    }
                    }
                    _currentRowAtribute++;
                }
            } catch (Exception e) {
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


}