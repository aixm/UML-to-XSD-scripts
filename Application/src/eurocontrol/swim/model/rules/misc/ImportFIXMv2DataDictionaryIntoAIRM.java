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
public class ImportFIXMv2DataDictionaryIntoAIRM  extends AbstractRule implements AIRMConstants {

    protected int _currentRow = 0;    
    protected Workbook _FIXM_v2_Data_Dictionary;
    protected Sheet _FIXM_v2_Data_Dictionary_sheet;

    protected final int LAST_ROW = 4456;
    protected final int FIRST_COLUMN = 1;
    protected final int SECOND_COLUMN = 2;
    
    //protected final String DEFINITION = "Definition";
//    protected final String SOURCE = "Source";
//    protected final String SYNONYMS = "Synonyms";
//    protected final String ABBREVIATIONS = "Abbreviations";
//    protected final String DEFINITION_STATUS = "Definition Status";
//    protected final String USAGE = "Usage";
//    protected final String REMARKS = "Remarks";
//    protected final String REVISIONS = "Revisions";
//    protected final String ACTIONS = "Actions";
    
    /////////////
    protected final String DEFINITION = "Definition";
    protected final String ALTERNATE_NAMES = "Alternate Names";
    protected final String HAS_PARTS = "Has Parts";
    protected final String IS_PART_OF = "Is Part Of";
    protected final String DATA_TYPES = "Data Type(s)";
    protected final String RANGE_OF_VALUES = "Range of Values";
    protected final String BUSINESS_RULES = "Business Rules";
    protected final String NOTES = "Notes";
    protected final String REFERENCE = "Reference";
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getName()
     */
    public String getName() {
        return "[AIRM] IMT PREPARATION - Import FIXM v2 Data Dictionary to AIRM";
    }    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getPackagesToBeProcessed()
     */
    public Package[] getPackagesToBeProcessed() {
        Package fixmDDpackage = EAConnection.getInstance().getRepository().GetPackageByGuid("{D7FC8048-B90F-4e26-B5BC-B8409F322EB0}"); 
        return new Package[] {fixmDDpackage};
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
        _FIXM_v2_Data_Dictionary = Workbook.getWorkbook(new File("./resources/fixm_data_dictionary.xls"));     
        _FIXM_v2_Data_Dictionary_sheet = _FIXM_v2_Data_Dictionary.getSheet(0);    
        
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
        String alternateNames = new String();
        String hasParts = new String();
        String isPartOf = new String();
        String dataTypes = new String();
        String rangeOfValues = new String();
        String businessRules = new String();
        String notes = new String();
        String reference = new String();
        
        
        while(_currentRow < LAST_ROW)
        {
            
            String firstColumnString = getCellInformation(FIRST_COLUMN,_currentRow,_FIXM_v2_Data_Dictionary_sheet);
            String secondColumnString = getCellInformation(SECOND_COLUMN,_currentRow,_FIXM_v2_Data_Dictionary_sheet);

            //          Trigger creation of elements
            if (firstColumnString.length() > 0 && (currentTag !=null && currentTag.equals(REFERENCE)) 
                    && (!firstColumnString.equals(DEFINITION)
                            && !firstColumnString.equals(ALTERNATE_NAMES)
                            && !firstColumnString.equals(HAS_PARTS)
                            && !firstColumnString.equals(IS_PART_OF)
                            && !firstColumnString.equals(DATA_TYPES)
                            && !firstColumnString.equals(RANGE_OF_VALUES)
                            && !firstColumnString.equals(BUSINESS_RULES)
                            && !firstColumnString.equals(NOTES) 
                            && !firstColumnString.equals(REFERENCE)
                            && !firstColumnString.equals(conceptName)))
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
                    SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element, "FIXM v2 Data Dictionary");
                    SparxUtilities.addTaggedValue(AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element, alternateNames);
                    
                    
                    SparxUtilities.addTaggedValue("FIXM::" + HAS_PARTS, element, hasParts);
                    SparxUtilities.addTaggedValue("FIXM::" + IS_PART_OF, element, isPartOf);
                    SparxUtilities.addTaggedValue("FIXM::" + DATA_TYPES, element, hasParts);
                    SparxUtilities.addTaggedValue("FIXM::" + RANGE_OF_VALUES, element, rangeOfValues);
                              
                    //SparxUtilities.addConstraint("FIXM::" + BUSINESS_RULES, element, businessRules);
                    
                    /*
                    SparxUtilities.addTaggedValue("FIXM::" + BUSINESS_RULES, element, businessRules);
                    SparxUtilities.addTaggedValue("FIXM::" + NOTES, element, notes);
                    SparxUtilities.addTaggedValue("FIXM::" + REFERENCE, element, reference);
                    */
                    
                    element.Update();
                    myPackage.Update();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    EAEventManager.getInstance().fireEAEvent(this,
                            MESSAGE_ERROR + ": Could not create tagged values " + e.getMessage() + " for element " + SparxUtilities.toString(element));
                }
                
                // Clear
                conceptName = new String();           
                definition = new String();
                alternateNames = new String();
                hasParts = new String();
                isPartOf = new String();
                dataTypes = new String();
                rangeOfValues = new String();
                businessRules = new String();
                notes = new String();
                reference = new String();            
            }
            
            //////////////////
            
            if(firstColumnString.equals(""))
            {
                if(currentTag != null && !currentTag.equals(""))
                {
                    firstColumnString = currentTag;
                }
            }
                    
            if(firstColumnString.startsWith(DEFINITION))
            {
                currentTag = DEFINITION;
                currentText = definition;
                definition = addTextToString(secondColumnString, definition); 
            }
            else if(firstColumnString.startsWith(ALTERNATE_NAMES))
            {
                currentTag = ALTERNATE_NAMES;
                currentText = alternateNames;
                alternateNames = addTextToString(secondColumnString, alternateNames);
            }
            else if(firstColumnString.startsWith(HAS_PARTS))
            {
                currentTag = HAS_PARTS;
                currentText = hasParts;
                hasParts = addTextToString(secondColumnString, hasParts);
            }
            else if(firstColumnString.startsWith(IS_PART_OF))
            {
                currentTag = IS_PART_OF;
                currentText = isPartOf;
                isPartOf = addTextToString(secondColumnString, isPartOf);
            }
            else if(firstColumnString.startsWith(DATA_TYPES))
            {
                currentTag = DATA_TYPES;
                currentText = dataTypes;
                dataTypes = addTextToString(secondColumnString, dataTypes);
            }
            else if(firstColumnString.startsWith(RANGE_OF_VALUES))
            {
                currentTag = RANGE_OF_VALUES;
                currentText = rangeOfValues;
                rangeOfValues = addTextToString(secondColumnString, rangeOfValues);
            }
            else if(firstColumnString.startsWith(BUSINESS_RULES))
            {
                currentTag = BUSINESS_RULES;
                currentText = businessRules;
                businessRules = addTextToString(secondColumnString, businessRules);
            }
            else if(firstColumnString.startsWith(NOTES))
            {
                currentTag = NOTES;
                currentText = notes;
                notes = addTextToString(secondColumnString, notes);
            }
            else if(firstColumnString.startsWith(REFERENCE))
            {
                currentTag = REFERENCE;
                currentText = reference;
                reference = addTextToString(secondColumnString, reference);
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
                        else if(currentTag.equals(ALTERNATE_NAMES))
                        {
                            alternateNames = addTextToString(secondColumnString,alternateNames);
                        } 
                        else if(currentTag.equals(HAS_PARTS))
                        {
                            hasParts = addTextToString(secondColumnString,hasParts);
                        }   
                        else if(currentTag.equals(IS_PART_OF))
                        {
                            isPartOf = addTextToString(secondColumnString,isPartOf);
                        }  
                        else if(currentTag.equals(DATA_TYPES))
                        {
                            dataTypes = addTextToString(secondColumnString,dataTypes);
                        } 
                        else if(currentTag.equals(RANGE_OF_VALUES))
                        {
                            rangeOfValues = addTextToString(secondColumnString,rangeOfValues);
                        }
                        else if(currentTag.equals(BUSINESS_RULES))
                        {
                            businessRules = addTextToString(secondColumnString,businessRules);
                        }    
                        else if(currentTag.equals(NOTES))
                        {
                            notes = addTextToString(secondColumnString,notes);
                        }
                        else if(currentTag.equals(REFERENCE))
                        {
                            reference = addTextToString(secondColumnString,reference);
                        }
                                                
                        
                    }
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
    
    private String buildElementDescription(String definition, String alternateNames, String hasParts, String isPartOf, String dataTypes, String rangeOfValues, String businessRules, String notes, String reference)
    {        
        return definition + "<BR>"
        + "-------------------------------" + "<BR>" + "<BR>"
        + NOTES + "<BR>"
        + notes + "<BR>"
        + "-------------------------------" + "<BR>" + "<BR>"
        + REFERENCE + "<BR>"
        + reference + "<BR>"
        + "-------------------------------" + "<BR>" + "<BR>"
        ;                       
    }
    
}
