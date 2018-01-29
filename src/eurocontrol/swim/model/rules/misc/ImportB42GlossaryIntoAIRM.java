/**
 *
##########################################################################
##########################################################################

Copyright (c) 2012, EUROCONTROL
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

package eurocontrol.swim.model.rules.misc;

import java.io.File;
import java.util.Iterator;

import jxl.Cell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;

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
public class ImportB42GlossaryIntoAIRM  extends AbstractRule implements AIRMConstants {

    protected int _currentRow = 0;    
    protected Workbook _B42_Glossary;
    protected Sheet _B42_Glossary_sheet;

    protected final int LAST_ROW = 1278;
    protected final int FIRST_COLUMN = 1;
    protected final int SECOND_COLUMN = 2;
    
    protected final String DEFINITION = "Definition";
    protected final String SOURCE = "Source";
    protected final String SYNONYMS = "Synonyms";
    protected final String ABBREVIATIONS = "Abbreviations";
    protected final String DEFINITION_STATUS = "Definition Status";
    protected final String USAGE = "Usage";
    protected final String REMARKS = "Remarks";
    protected final String REVISIONS = "Revisions";
    protected final String ACTIONS = "Actions";
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getName()
     */
    public String getName() {
        return "[AIRM] IMT PREPARATION - Import B4.2 consolidated glossary to AIRM";
    }    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getPackagesToBeProcessed()
     */
    public Package[] getPackagesToBeProcessed() {
        Package trajectoryManagementThemePackage = EAConnection.getInstance().getRepository().GetPackageByGuid("{DB41238B-28AF-4707-8716-8D8AC349A197}"); 
        return new Package[] {trajectoryManagementThemePackage};
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        try {
            //Initialise the Excel file
            initExcelFile();
        } catch (Exception e) {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this,
                    MESSAGE_ERROR + ": Could not initialise the Excel file.");
        }
        // Read Excel file and create/update the class accordingly
        readExcelFile(myPackage);


    }
        
    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook
        _B42_Glossary = Workbook.getWorkbook(new File("./resources/B4.2_Glossary.xls"));     
        _B42_Glossary_sheet = _B42_Glossary.getSheet(0);    
        
    }
    
    /**
     * 
     * @throws Exception
     */
    private void readExcelFile(Package myPackage) {        
        
        String currentTag = null;
        String currentText = null;
        
        String conceptName = new String();           
        String definition = new String();
        String source = new String();
        String synonyms = new String();
        String abbreviations = new String();
        String definitionStatus = new String();
        String usage = new String();
        String remarks = new String();
        String revisions = new String();
        String actions = new String();
        
        while(_currentRow < LAST_ROW)
        {
            String firstColumnString = getCellInformation(FIRST_COLUMN,_currentRow,_B42_Glossary_sheet);
            String secondColumnString = getCellInformation(SECOND_COLUMN,_currentRow,_B42_Glossary_sheet);
          
            if(firstColumnString.startsWith(DEFINITION))
            {
                currentTag = DEFINITION;
                currentText = definition;
                definition = addTextToString(secondColumnString, definition); 
            }
            else if(firstColumnString.startsWith(SOURCE))
            {
                currentTag = SOURCE;
                currentText = source;
                source = addTextToString(secondColumnString, source);
            }
            else if(firstColumnString.startsWith(SYNONYMS))
            {
                currentTag = SYNONYMS;
                currentText = synonyms;
                synonyms = addTextToString(secondColumnString, synonyms);
            }
            else if(firstColumnString.startsWith(ABBREVIATIONS))
            {
                currentTag = ABBREVIATIONS;
                currentText = abbreviations;
                abbreviations = addTextToString(secondColumnString, abbreviations);
            }
            else if(firstColumnString.startsWith(DEFINITION_STATUS))
            {
                currentTag = DEFINITION_STATUS;
                currentText = definitionStatus;
                definitionStatus = addTextToString(secondColumnString, definitionStatus);
            }
            else if(firstColumnString.startsWith(USAGE))
            {
                currentTag = USAGE;
                currentText = usage;
                usage = addTextToString(secondColumnString, usage);
            }
            else if(firstColumnString.startsWith(REMARKS))
            {
                currentTag = REMARKS;
                currentText = remarks;
                remarks = addTextToString(secondColumnString, remarks);
            }
            else if(firstColumnString.startsWith(REVISIONS))
            {
                currentTag = REVISIONS;
                currentText = revisions;
                revisions = addTextToString(secondColumnString, revisions);
            }
            else if(firstColumnString.startsWith(ACTIONS))
            {
                currentTag = ACTIONS;
                currentText = actions;
                actions = addTextToString(secondColumnString, actions);
            }
            else if(firstColumnString.length() >0)
            {                
                currentTag = null;
                currentText = null;
                conceptName = firstColumnString;
            }
            else
            {
                if (currentTag != null)
                {
                    if(secondColumnString.length() >0)
                    {
                        if(currentTag.equals(DEFINITION))
                        {
                            definition = addTextToString(secondColumnString,definition);   
                        }
                        else if(currentTag.equals(SOURCE))
                        {
                            source = addTextToString(secondColumnString,source);
                        } 
                        else if(currentTag.equals(SYNONYMS))
                        {
                            synonyms = addTextToString(secondColumnString,synonyms);
                        }   
                        else if(currentTag.equals(ABBREVIATIONS))
                        {
                            abbreviations = addTextToString(secondColumnString,abbreviations);
                        }  
                        else if(currentTag.equals(DEFINITION_STATUS))
                        {
                            definitionStatus = addTextToString(secondColumnString,definitionStatus);
                        } 
                        else if(currentTag.equals(USAGE))
                        {
                            usage = addTextToString(secondColumnString,usage);
                        }
                        else if(currentTag.equals(REMARKS))
                        {
                            remarks = addTextToString(secondColumnString,remarks);
                        }    
                        else if(currentTag.equals(REVISIONS))
                        {
                            revisions = addTextToString(secondColumnString,revisions);
                        }
                        else if(currentTag.equals(ACTIONS))
                        {
                            actions = addTextToString(secondColumnString,actions);
                        }
                                                
                        
                    }
                }
            }
            
            if(firstColumnString.length()==0 && secondColumnString.length() == 00)
            {
                if(conceptName.length()>0)
                {
                    //Build class
                    String className = conceptName.replaceAll(" ","");
                    
                    Element element = getElementByName(myPackage,className);
                    
                    if(element == null)
                        element = (Element)myPackage.GetElements().AddNew(className,EA_TYPE_CLASS);                                       
                    
                    element.SetNotes(definition);
                    
                    element.Update();
                    myPackage.Update();
                    
                    try
                    {
                        SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element, source);
                        SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element, synonyms);
                        SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, element, abbreviations);
                        SparxUtilities.addTaggedValue("IMT:" + USAGE, element, usage);
                        SparxUtilities.addTaggedValue("IMT:" + REMARKS, element, remarks);
                        SparxUtilities.addTaggedValue("IMT:" + REVISIONS, element, revisions);
                        SparxUtilities.addTaggedValue("IMT:" + ACTIONS, element, actions);
                        element.Update();
                        myPackage.Update();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        EAEventManager.getInstance().fireEAEvent(this,
                                MESSAGE_ERROR + ": Could not create tagged values for element " + SparxUtilities.toString(element));
                    }
                    

                    
                    // Clear
                    conceptName = new String();
                    definition = new String();
                    source = new String();
                    synonyms = new String();
                    abbreviations = new String();
                    usage = new String();
                    remarks = new String();
                    revisions = new String();
                    actions = new String();
                }
            }
            
            _currentRow++;
        }
    }
    
    private String addTextToString (String text, String string)
    {
        if(string.length() == 0)
        {
            return text;
        }
        else
        {
            return string + "\n" + text;
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
     * @param aPackage
     * @return
     */
    private Element getElementByName(Package aPackage, String name)
    {       
        for(Iterator packageIter = aPackage.GetPackages().iterator();packageIter.hasNext();)
        {
            Package subPackage = (Package) packageIter.next();
            return getElementByName(subPackage,name);
        }
        
        for (Iterator iter = aPackage.GetElements().iterator();iter.hasNext();)
        {
            Element element = (Element) iter.next();
            if(element.GetName().equals(name))
                return element;
        }
        
        return null;
    }
    
}
