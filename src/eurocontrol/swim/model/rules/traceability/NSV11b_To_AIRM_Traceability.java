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

package eurocontrol.swim.model.rules.traceability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import eurocontrol.swim.model.util.constants.TraceabilityToAIRMConstants;
import eurocontrol.swim.model.util.traceability.AIRM_Concept;
import eurocontrol.swim.model.util.traceability.NSV11b_Construct;

/**
 * @author hlepori
 */
public class NSV11b_To_AIRM_Traceability extends AbstractRule implements AIRMConstants, TraceabilityToAIRMConstants {

    
    private ArrayList _listOfAIRMConcept;
    private ArrayList _listOfNSV11bConstructs;
        
    private WritableWorkbook _excelWorkbook;
    private WritableSheet _traceSheet;
    
    private int _currentRow = 2;
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[Any Physical Model] TRACEABILITY - Generate Excel spreadsheet with generic traceability back to the AIRM CLDM (Deprecated - does not rely on star model)";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        
        Package cldmRoot = EAConnection.getInstance().getRepository().GetPackageByGuid(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID);
        if(cldmRoot != null)
        {
            try
            {                      
                // Building AIRM list and hashmap
                EAEventManager.getInstance().fireEAEvent(this,"Listing all AIRM concepts...");
                loadListAndHashmap();
                if(_listOfAIRMConcept == null)
                {
                    _listOfAIRMConcept = new ArrayList();
                    buildListOfAIRMConcepts(cldmRoot.GetPackages());
                    saveList();
                }                        
                // Start tracing the elements back to the CLDM
                EAEventManager.getInstance().fireEAEvent(this,"Tracing selected model back to AIRM CLDM...");
                
                initExcelFile();
                
                buildTraceability(myPackage.GetPackages());                             
                                                           
                finaliseTraceability();

                
                EAEventManager.getInstance().fireEAEvent(this,
                "Done...");
                EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not complete the traceability back to the CLDM.");
            }   
        }
        else
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not find the AIRM CLDM.");
        }   
        
    }
    
    
    /**
     * 
     * @param packages
     */
    private void buildListOfAIRMConcepts(Collection packages)
    {
        for (Iterator packageIter = packages.iterator();packageIter.hasNext();)
        {
            Package myPackage = (Package) packageIter.next();
            System.out.println("Processing AIRM package " + myPackage.GetName());
            if (SparxUtilities.belongsToPackage(myPackage,AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID))
            {
                ////////////////////////////////////////////////////////
                // Process the elements from the CLDM.
                ////////////////////////////////////////////////////////  
                for (Iterator elementIter = myPackage.GetElements().iterator();elementIter.hasNext();)
                {
                    Element element = (Element) elementIter.next();
                    
                    AIRM_Concept airmConcept = new AIRM_Concept(element);
                    _listOfAIRMConcept.add(airmConcept);
                                       
                    ////////////////////////////////////////////////////////
                    // Process the attributes.
                    ////////////////////////////////////////////////////////                    
                    
                    for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                    {
                        Attribute attribute = (Attribute) attributeIter.next();
                        
                        AIRM_Concept airmConceptAsAttribute = new AIRM_Concept(attribute, element);
                        _listOfAIRMConcept.add(airmConceptAsAttribute);
                                               
                    }
                    
                    ////////////////////////////////////////////////////////
                    // Process the connectors.
                    ////////////////////////////////////////////////////////                      
                    
                    for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                    {
                        Connector connector = (Connector)connectorIter.next();
                        
                        ConnectorEnd role = connector.GetClientEnd();
                        int attachedElementId = connector.GetClientID();
                        
                        if(attachedElementId == element.GetElementID())
                        {
                            role = connector.GetSupplierEnd();
                            attachedElementId = connector.GetSupplierID();
                        }
                        
                        if(role.GetRole() != null && role.GetRole().length() >0)
                        {
                            AIRM_Concept airmConceptAsRole = new AIRM_Concept(role, connector, element);
                            _listOfAIRMConcept.add(airmConceptAsRole);
                        }                        
                    }
         
                }
                buildListOfAIRMConcepts(myPackage.GetPackages());                   
            }
        }
    }
    
    
    /**
     * 
     * @param packages
     */
    private void buildTraceability(Collection packages) throws Exception
    {
        for (Iterator packageIter = packages.iterator();packageIter.hasNext();)
        {
            Package myPackage = (Package) packageIter.next();
            System.out.println("Procesing package " + myPackage.GetName());

            for (Iterator elementIter = myPackage.GetElements().iterator();elementIter.hasNext();)
            {
                Element element = (Element) elementIter.next();
                
                // discard the notes
                if(element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION))
                {
                    realiseTraceability(element);
                }    
                
                for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                {
                    Attribute attribute = (Attribute) attributeIter.next();
                    realiseTraceability(attribute, element);
                }     
                
                for (Iterator connectorIter = element.GetConnectors().iterator();connectorIter.hasNext();)
                {
                    Connector connector = (Connector)connectorIter.next();
                    
                    ConnectorEnd role = connector.GetClientEnd();
                    int attachedElementId = connector.GetClientID();
                    
                    if(attachedElementId == element.GetElementID())
                    {
                        role = connector.GetSupplierEnd();
                        attachedElementId = connector.GetSupplierID();
                    }
                    
                    if(role.GetRole() != null && role.GetRole().length() > 0)
                    {
                        realiseTraceability(role, connector, element);
                    }
                }
            }
            buildTraceability(myPackage.GetPackages());                   
        }
    }
    
    protected void realiseTraceability(Element element) throws Exception {
        NSV11b_Construct construct = getNSV11bConstruct(element);
        AIRM_Concept airmConcept = findMatchingAIRMConcept(construct);
        realiseTraceability(construct,airmConcept);
    }
    
    protected void realiseTraceability(Attribute attribute, Element element) throws Exception {
        NSV11b_Construct construct = getNSV11bConstruct(attribute, element);
        AIRM_Concept airmConcept = findMatchingAIRMConcept(construct);
        realiseTraceability(construct,airmConcept);
    }
    
    protected void realiseTraceability(ConnectorEnd role, Connector connector, Element element) throws Exception {
        NSV11b_Construct construct = getNSV11bConstruct(role, connector, element);
        AIRM_Concept airmConcept = findMatchingAIRMConcept(construct);
        realiseTraceability(construct,airmConcept);
    }

    /**
     * 
     */
    protected void realiseTraceability(NSV11b_Construct nsv11b_construct, AIRM_Concept airm_concept) throws Exception {
        System.out.println("Realising traceability for " + nsv11b_construct.toString());
        populateExcelFile(nsv11b_construct, airm_concept);
    }
    
    /**
     * 
     */
    protected void populateExcelFile(NSV11b_Construct nsv11b_construct, AIRM_Concept airm_concept) throws Exception 
    {
        
        addCell(TRACE_EXCEL_COLUMN_NSV_11B_TYPE,nsv11b_construct.getType());
        addCell(TRACE_EXCEL_COLUMN_NSV_11B_NAME,nsv11b_construct.getName());
        addCell(TRACE_EXCEL_COLUMN_NSV_11B_DEFINITION,nsv11b_construct.getDefinition());
      
        String traceable = new String();
        String comment = new String();
        
        if(!nsv11b_construct.isATMConcept())
        {
            traceable = "N/A";
            comment = comment + "This construct is not an ATM concept." + " ";
        }
        else
        {
            if(airm_concept == null)
            {
                traceable = Boolean.toString(false);
              	comment = comment + "No equivalent concept found in the CLDM." + " ";
            }
            else
            {      
                addCell(TRACE_EXCEL_COLUMN_AIRM_CLDM_TYPE,airm_concept.getType());
                addCell(TRACE_EXCEL_COLUMN_AIRM_CLDM_NAME,airm_concept.getName());
                addCell(TRACE_EXCEL_COLUMN_AIRM_CLDM_DEFINITION,airm_concept.getDefinition());   
                
                
                // => (same names) and (same definitions)  
                if(nsv11b_construct.isSemanticallyCompliantWith(airm_concept))
                {
                    traceable = Boolean.toString(true);
                    // check use of synonym
	                if(!nsv11b_construct.getName().equals(airm_concept.getName()))
	                {
	                    comment = comment + "A different name is used." + " ";  	                    
	                }
	                // => (nsv11b type) different from (cldm concept type)
	                if(!nsv11b_construct.getType().equals(airm_concept.getType()))
	                    comment = comment + "The NSV11b " + nsv11b_construct.getType() + " matches an " + "AIRM CLDM " + airm_concept.getType() + ". ";      
                }
                else
                {
                    traceable = Boolean.toString(false);
                    comment = comment + "The NSV 11b definition does not match." + " ";                   
                }                
            }
        }
      
        addCell(TRACE_EXCEL_COLUMN_TRACING_OK,traceable); 
        addCell(TRACE_EXCEL_COLUMN_COMMENTS,comment); 
        
        _currentRow++;
    }
       
    
    
    protected AIRM_Concept findMatchingAIRMConcept(NSV11b_Construct nsv11b_construct) throws Exception
    {             
        if(!nsv11b_construct.isATMConcept())
        {
            return null;
        }
        else
        {
            for (Iterator iter = _listOfAIRMConcept.iterator();iter.hasNext();)
            {
                AIRM_Concept airmConcept = (AIRM_Concept)iter.next();
                if(nsv11b_construct.nameMatches(airmConcept))
                {
                    return airmConcept;
                }
            }           
        }        
        return null;       
    }
    

    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook
        Workbook workbook = Workbook.getWorkbook(new File(
                "./resources/Template_Tracing_back_to_CLDM.xls"));
        _excelWorkbook = Workbook.createWorkbook(
                new File("./Tracing_back_to_CLDM.xls"), workbook);
        _traceSheet = _excelWorkbook.getSheet(0);
        
    }
    
    /**
     * 
     * @param column
     * @param row
     * @param str
     */
    protected void addCell(int column, String str) throws Exception {
        Label label = new Label(column, _currentRow, str);
        _traceSheet.addCell(label);
    }
    
    private void saveList()
    {
        save("AIRM_list_of_concepts.dat",_listOfAIRMConcept);
    }
    
    
    private void save(String fileName, ArrayList list)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream("./resources/"+fileName);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        }
        catch (IOException e)
        {
            // do nothing
        } 
    }
    
    private void loadListAndHashmap()
    {
        _listOfAIRMConcept = loadList("AIRM_list_of_concepts.dat");
    }

    private ArrayList loadList(String filename)
    {
        ArrayList list = null;
        try
        {
            FileInputStream fis = new FileInputStream("./resources/"+filename);
            ObjectInputStream in = new ObjectInputStream(fis);
            list = (ArrayList)in.readObject();
            in.close();
        }
        catch (Exception e)
        {
            // do nothing
        } 
        return list;
    }
    
    
    protected void finaliseTraceability() throws Exception
    {
        _excelWorkbook.write();
        _excelWorkbook.close();
    }
    
    
    /**
     * 
     */
    protected void initGUI(){
        _modelBrowser = buildTree();
    }
    

    
    /**
     * 
     * @param element
     * @return
     */
    protected NSV11b_Construct getNSV11bConstruct(Element element)
    {
        return new NSV11b_Construct(element);
    }
    
    /**
     * 
     * @param element
     * @return
     */
    protected NSV11b_Construct getNSV11bConstruct(Attribute attribute, Element element)
    {
        return new NSV11b_Construct(attribute, element);
    }
    
    /**
     * 
     * @param element
     * @return
     */
    protected NSV11b_Construct getNSV11bConstruct(ConnectorEnd role, Connector connector, Element element)
    {
        return new NSV11b_Construct(role, connector, element);
    }
}
