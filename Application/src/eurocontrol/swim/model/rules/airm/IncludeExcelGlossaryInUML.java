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
import jxl.read.biff.BlankCell;

import org.sparx.Attribute;
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
public class IncludeExcelGlossaryInUML extends AbstractRule implements AIRMConstants {

    protected int _currentRow = AIRM_GLOSSARY_FIRST_EDITABLE_ROW;
    protected  Workbook  _excelGlossary;
    protected  Sheet _glossarySheet;
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM] GLOSSARY - Import Excel content to the AIRM (Excel => UML)";
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
        try
        {
            //Load the Excel file
            loadExcelFile();
            // Build the lists
            updateAIRMinformation();
                            
            EAEventManager.getInstance().fireEAEvent(this, "Rule applied successfully");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this, "ERROR: Could not initialise the Excel glossary.");
        }
    }
    

    
    /**
     * 
     * @throws Exception
     */
    private void loadExcelFile() throws Exception
    { 
        //Initialise the excel workbook
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        fileChooser.setName("Please select an Excel file");
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File arg0) {
                return arg0.isDirectory() ||arg0.getName().endsWith(".xls");
            }

            public String getDescription() {
                return "Excel File (*.xls)";
            }
        });
        
        int result = fileChooser.showOpenDialog(null);
        
        if (result == JFileChooser.OPEN_DIALOG) {
    	    
            _excelGlossary = Workbook.getWorkbook(fileChooser.getSelectedFile()); 
            _glossarySheet = _excelGlossary.getSheet(0);   
            
        }
    }
    
    /**
     * 
     *
     */
    private void updateAIRMinformation()
    {
        boolean loop = true;
        
        String name = null;
        String abbreviation = null;
        String definition = null;
        String definitionSource = null;
        String synonym = null;   
        String status = null;
        String type = null;
        
        
        while(loop)
        {
            try
            {
                String GUID = getCellInformation(AIRM_GLOSSARY_COLUMN_GUID);
                if(GUID == null || GUID.length() == 0)
                {
                    loop = false;
                }
                else
                {
                    type = getCellInformation(AIRM_GLOSSARY_COLUMN_UML_TYPE);
                    name = getCellInformation(AIRM_GLOSSARY_COLUMN_NAME);
                    abbreviation = getCellInformation(AIRM_GLOSSARY_COLUMN_ABBREVIATION);
                    definition = getCellInformation(AIRM_GLOSSARY_COLUMN_DEFINITION);
                    definitionSource = getCellInformation(AIRM_GLOSSARY_COLUMN_DEFINITION_SOURCE);
                    synonym = getCellInformation(AIRM_GLOSSARY_COLUMN_SYNONYM);
                    status = getCellInformation(AIRM_GLOSSARY_COLUMN_STATUS);
                    
                    if (status == null || status == "")
                    {
                        status = AIRM_TAGGED_VALUE_DEFINITION_STATUS_DEFAULT_VALUE;
                    }
                    
                    if(type.equals(EA_TYPE_PACKAGE))
                    {
                        try
                        {
                            EAEventManager.getInstance().fireEAEvent(this, "Processing package " + name);
                            updatePackage(GUID,name,abbreviation,definition,definitionSource,synonym, status);
                        }
                        catch(Exception e)
                        {
                            EAEventManager.getInstance().fireEAEvent(this, "ERROR: Could not update package " + name); 
                        }
                    }
                    else if(type.equals(EA_TYPE_CLASS))
                    {
                        try
                        {
                            updateClass(GUID,name,abbreviation,definition,definitionSource,synonym, status);
                        }
                        catch(Exception e)
                        {
                            EAEventManager.getInstance().fireEAEvent(this, "ERROR: Could not update class " + name); 
                        }
                    }
                    else if(type.equals(EA_TYPE_ATTRIBUTE))
                    {
                        try
                        {
                            updateAttribute(GUID,name,abbreviation,definition,definitionSource,synonym, status);
                        }
                        catch(Exception e)
                        {
                            EAEventManager.getInstance().fireEAEvent(this, "ERROR: Could not update attribute " + name); 
                        }
                    }
                    _currentRow++;
                }
            }
            catch (Exception e)
            {
                loop = false;
            }
        }
    }
    
    /**
     * 
     * @param GUID
     * @param name
     * @param abbreviation
     * @param definition
     * @param definitionSource
     * @param synonym
     */
    private void updatePackage(String GUID, String name, String abbreviation,
            String definition, String definitionSource, String synonym, String status)
            throws Exception {
        Package myPackage = EAConnection.getInstance().getRepository()
                .GetPackageByGuid(GUID);
        if (myPackage == null)
            throw new Exception();

        myPackage.SetName(name);
        myPackage.SetNotes(definition);
        
        // Update Abbreviation
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,myPackage))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, myPackage, abbreviation);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, myPackage, abbreviation);
        }
        // Update Definition Source        
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,myPackage))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, myPackage, definitionSource);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, myPackage, definitionSource);
        }
        // Update Synonyms
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, myPackage))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, myPackage, synonym);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, myPackage, synonym);
        }
        // Update status
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, myPackage))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, myPackage, status);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, myPackage, status);
        }
        
        
	    boolean isUpdateSuccessful = myPackage.Update();
	    if(!isUpdateSuccessful)
	    {
	        throw new Exception();
	    }
    }
    
    /**
     * 
     * @param GUID
     * @param name
     * @param abbreviation
     * @param definition
     * @param definitionSource
     * @param synonym
     * @throws Exception
     */
    private void updateClass(String GUID, String name, String abbreviation,
            String definition, String definitionSource, String synonym, String status)
            throws Exception {
        Element element = EAConnection.getInstance().getRepository()
                .GetElementByGuid(GUID);
        if (element == null)
        {
            throw new Exception();
        }

        element.SetName(name);
        element.SetNotes(definition);
       
        // Update Abbreviation
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,element))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, element, abbreviation);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, element, abbreviation);
        }
        // Update Definition Source        
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,element))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element, definitionSource);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element, definitionSource);
        }
        // Update Synonyms
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element, synonym);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element, synonym);
        }
        // Update status
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element, status);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element, status);
        }

	    boolean isUpdateSuccessful = element.Update();
	    if(!isUpdateSuccessful)
	    {
	        throw new Exception();
	    }
        
    }
    
    
    /**
     * 
     * @param GUID
     * @param name
     * @param abbreviation
     * @param definition
     * @param definitionSource
     * @param synonym
     * @throws Exception
     */
    private void updateAttribute(String GUID, String name, String abbreviation,
            String definition, String definitionSource, String synonym, String status)
            throws Exception {
        Attribute attribute = EAConnection.getInstance().getRepository()
                .GetAttributeByGuid(GUID);
        if (attribute == null)
            throw new Exception();

        attribute.SetName(name);
        attribute.SetNotes(definition);

        // Update Abbreviation
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION,attribute))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, attribute, abbreviation);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, attribute, abbreviation);
        }
        // Update Definition Source        
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE,attribute))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute, definitionSource);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute, definitionSource);
        }
        // Update Synonyms
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, attribute))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, attribute, synonym);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, attribute, synonym);
        }
        // Update status
        if(SparxUtilities.isTaggedValuePopulated(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute))
        {
            SparxUtilities.updateTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute, status);
        }
        else
        {
            SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute, status);
        }

       
	    boolean isUpdateSuccessful = attribute.Update();
	    if(!isUpdateSuccessful)
	    {
	        throw new Exception();
	    }
    }
   
    
    /**
     * 
     * @param column
     * @return @throws
     *         Exception
     */
    private String getCellInformation(int column) {
        Cell cell = _glossarySheet.getCell(column, _currentRow);
        if (cell instanceof LabelCell) {
            return ((LabelCell) cell).getString();
        } else if (cell instanceof EmptyCell) {
            return "";
        } else if (cell instanceof BlankCell) {
            return "";
        } else 
        {
            // ASCII code for 'A' is 65
            int asciiCode = column+65;
            char col = (char) asciiCode;
            EAEventManager.getInstance().fireEAEvent(this, "WARNING: The cell (" + col + "," + _currentRow + ") could not be evaluated");
            return "";
        }
    }
    
    
    
}
