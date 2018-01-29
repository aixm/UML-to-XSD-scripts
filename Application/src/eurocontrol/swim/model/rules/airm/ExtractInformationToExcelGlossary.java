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
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;

/**
 * @author hlepori
 */
public class ExtractInformationToExcelGlossary extends AbstractRule implements AIRMConstants {

    
    protected int _currentRow = AIRM_GLOSSARY_FIRST_EDITABLE_ROW;

    protected WritableWorkbook _excelGlossary;

    protected WritableSheet _glossarySheet;

 
    /*
     * (non-Javadoc)
     * 
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM] GLOSSARY - Export AIRM content to an Excel spreadsheet (UML => Excel)";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        try {
            //Initialise the Excel file
            initExcelFile();
            
            // Process the selected package itself
            processPackage(myPackage,"");
            
            // Build the lists
            processPackageContentRecursively(myPackage
                    .GetPackages(), myPackage.GetName());

            //save Excel file
            _excelGlossary.write();
            _excelGlossary.close();

            EAEventManager.getInstance().fireEAEvent(this,
                    "Rule applied successfully");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        } catch (Exception e) {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this,
                    MESSAGE_ERROR + ": Could not initialise the Excel glossary.");
        }
    }
    

    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook
        Workbook workbook = Workbook.getWorkbook(new File(
                "./resources/SESAR_Template.xls"));
        _excelGlossary = Workbook.createWorkbook(
                new File("./output/AIRM_Glossary.xls"), workbook);
        _glossarySheet = _excelGlossary.getSheet(0);

        //Add required columns that are not provided by default in the template
        Label label = new Label(AIRM_GLOSSARY_COLUMN_UML_TYPE, AIRM_GLOSSARY_FIRST_EDITABLE_ROW - 1,
                "Type of UML element");
        _glossarySheet.addCell(label);
        label = new Label(AIRM_GLOSSARY_COLUMN_GUID, AIRM_GLOSSARY_FIRST_EDITABLE_ROW - 1, "GUID");
        _glossarySheet.addCell(label);
        label = new Label(AIRM_GLOSSARY_COLUMN_SYNONYM, AIRM_GLOSSARY_FIRST_EDITABLE_ROW - 1, "Synonyms");
        _glossarySheet.addCell(label);
        label = new Label(AIRM_GLOSSARY_COLUMN_STATUS, AIRM_GLOSSARY_FIRST_EDITABLE_ROW - 1, "Status");
        _glossarySheet.addCell(label);

        label = new Label(AIRM_GLOSSARY_COLUMN_PACKAGE_HIERARCHY, AIRM_GLOSSARY_FIRST_EDITABLE_ROW - 1, "Package Hierarchy");
        _glossarySheet.addCell(label);
        
    }

    /**
     * 
     * @param packageCollection
     */
    private void processPackageContentRecursively(Collection packageCollection, String parentPackageName) {

        if (packageCollection != null && packageCollection.GetCount() > 0) {
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
            
            String packageHierarchy = parentPackageName; 

            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) {
                
                
                subPackage = (Package) packageIter.next();

                processPackage(subPackage, packageHierarchy);
                
                processPackageContentRecursively(subPackage.GetPackages(), packageHierarchy);
            }
        }
    }
    
    protected void processPackage(Package myPackage, String parentPackageName)
    {

        // local variable to store the element (= a class)
        Element element = null;
        // local variable to store an attribute
        Attribute attribute = null;
        // local variable to store a connector
        Connector connector = null;
        // local variable to store the roles
        ConnectorEnd sourceRole = null;
        ConnectorEnd targetRole = null;
        
        String packageHierarchy = null; 

        
        EAEventManager.getInstance().fireEAEvent(
                this,
                "Extracting information from package "
                        + myPackage.GetName());

        if(parentPackageName.equals(""))
            packageHierarchy = myPackage.GetName();
        else
            packageHierarchy = parentPackageName + "/" + myPackage.GetName(); 
        
        addPackageToGlossary(myPackage, packageHierarchy);

        /////////////////////////////////////////////////
        // Process the classes defined in this subpackage
        /////////////////////////////////////////////////
        for (Iterator elementsIter = myPackage.GetElements()
                .iterator(); elementsIter.hasNext();) {

            element = (Element) elementsIter.next();

            // The class element is used generically
            // WARNING: in some cases, an element of type enumeration
            // ...
            if (element.GetType().equals(EA_TYPE_CLASS)) {

                addElementToGlossary(element, packageHierarchy);

                ///////////////////////////////////////////////////
                // Process the class attributes
                ///////////////////////////////////////////////////
                for (Iterator attributeIter = element.GetAttributes()
                        .iterator(); attributeIter.hasNext();) {
                    attribute = (Attribute) attributeIter.next();
                    addAttributeToGlossary(attribute, packageHierarchy);
                }

            }
        }
        
    }
    

    /**
     * 
     * @param myPackage
     */
    protected void addPackageToGlossary(Package myPackage, String packageHierarchy) {
        try {
            addCell(AIRM_GLOSSARY_COLUMN_NAME, myPackage.GetName());
            addCell(AIRM_GLOSSARY_COLUMN_DEFINITION, SparxUtilities.removeHTMLTagsFromText(myPackage.GetNotes()));
            addCell(AIRM_GLOSSARY_COLUMN_UML_TYPE, EA_TYPE_PACKAGE);
            addCell(AIRM_GLOSSARY_COLUMN_GUID, myPackage.GetPackageGUID());
            addCell(AIRM_GLOSSARY_COLUMN_PACKAGE_HIERARCHY, packageHierarchy);
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    "ERROR: Could not add package [" + myPackage.GetName()
                            + "] to the Excel glossary");
        }

        _currentRow++;
    }

    /**
     * 
     * @param element
     */
    protected void addElementToGlossary(Element element, String packageHierarchy) {
        try {
            addCell(AIRM_GLOSSARY_COLUMN_NAME, element.GetName());
            addCell(AIRM_GLOSSARY_COLUMN_DEFINITION, SparxUtilities.removeHTMLTagsFromText(element.GetNotes()));
            addCell(AIRM_GLOSSARY_COLUMN_DEFINITION_SOURCE, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, element));
            addCell(AIRM_GLOSSARY_COLUMN_ABBREVIATION, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, element));

            addCell(AIRM_GLOSSARY_COLUMN_SYNONYM, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, element));
            addCell(AIRM_GLOSSARY_COLUMN_STATUS, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, element));

            addCell(AIRM_GLOSSARY_COLUMN_UML_TYPE, EA_TYPE_CLASS);
            addCell(AIRM_GLOSSARY_COLUMN_GUID, element.GetElementGUID());
            addCell(AIRM_GLOSSARY_COLUMN_PACKAGE_HIERARCHY, packageHierarchy);
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    "ERROR: Could not add [" + SparxUtilities.toString(element)
                            + "] to the Excel glossary");
        }

        _currentRow++;
    }

    /**
     * 
     * @param attribute
     */
    protected void addAttributeToGlossary(Attribute attribute, String packageHierarchy) {
        try {
            addCell(AIRM_GLOSSARY_COLUMN_NAME, attribute.GetName());
            addCell(AIRM_GLOSSARY_COLUMN_DEFINITION, SparxUtilities.removeHTMLTagsFromText(attribute.GetNotes()));
            addCell(AIRM_GLOSSARY_COLUMN_DEFINITION_SOURCE, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_SOURCE, attribute));
            addCell(AIRM_GLOSSARY_COLUMN_ABBREVIATION, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_ABBREVIATION, attribute));

            addCell(AIRM_GLOSSARY_COLUMN_SYNONYM, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_SYNONYMS, attribute));
            addCell(AIRM_GLOSSARY_COLUMN_STATUS, SparxUtilities.getContentOfTaggedValue(
                    AIRM_TAGGED_VALUE_NAME_DEFINITION_STATUS, attribute));

            addCell(AIRM_GLOSSARY_COLUMN_UML_TYPE, EA_TYPE_ATTRIBUTE);
            addCell(AIRM_GLOSSARY_COLUMN_GUID, attribute.GetAttributeGUID());
            addCell(AIRM_GLOSSARY_COLUMN_PACKAGE_HIERARCHY, packageHierarchy);
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    "ERROR: Could not add [" + SparxUtilities.toString(attribute)
                            + "] to the Excel glossary");
        }

        _currentRow++;
    }

    /**
     * 
     * @param column
     * @param row
     * @param str
     */
    protected void addCell(int column, String str) throws Exception {
        Label label = new Label(column, _currentRow, str);
        _glossarySheet.addCell(label);
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see eurocontrol.swim.model.rules.Rule#check()
//     */
//    public void check() {
//        // TODO Auto-generated method stub
//
//    }


}