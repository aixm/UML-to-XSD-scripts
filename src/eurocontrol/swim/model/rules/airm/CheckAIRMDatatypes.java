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
import java.util.HashMap;
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

/**
 * @author hlepori
 */
public class CheckAIRMDatatypes extends AbstractRule implements AIRMConstants {

    protected WritableWorkbook _excelWorkbook;
    
    protected WritableSheet _datatypeSheet;
    protected WritableSheet _attributeSheet;
    protected WritableSheet _unusedDatatypeSheet;
    
    protected int _currentRowDataType = 2;
    protected int _currentRowAttribute = 2;
    protected int _currentRowUnusedDatatype = 2;
    
    protected HashMap _datatypesMap = new HashMap();
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIRM] QUALITY CHECK - Check AIRM datatypes" ;
    }
        
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getPackagesToBeProcessed()
     */
    public Package[] getPackagesToBeProcessed() {       
        
        Package consolidatedLogicalDataModelPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(AIRM_CONSOLIDATED_LOGICAL_MODEL_PACKAGE_GUID);

        return new Package[]{consolidatedLogicalDataModelPackage};
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#checkPackageAgainstRule(org.sparx.Package)
     */
    public void checkPackageAgainstRule(Package myPackage) {
        try
        {
            initExcelFile();
        
            // Build the lists
            processPackageContentRecursively(myPackage
                    .GetPackages(), myPackage.GetName());
            
            for (Iterator iter = _datatypesMap.keySet().iterator();iter.hasNext();)
            {
                String key = (String) iter.next();
                ArrayList value = (ArrayList)_datatypesMap.get(key);
                String list = new String();
                for (Iterator strIter = value.iterator(); strIter.hasNext();)
                {
                    list = list + "\n" + (String) strIter.next();
                }
                try
                {
                    addCell(_datatypeSheet,1,_currentRowDataType,key);
                    addCell(_datatypeSheet,2,_currentRowDataType,list);
                }
                catch(Exception e)
                {
                    EAEventManager.getInstance().fireEAEvent(
                            this,
                            MESSAGE_ERROR + ": Could not export [" + key
                                    + "] to the Excel file");
                }
                _currentRowDataType++;
            }
            
            listUnusedDatatypes();
            
            
            //save Excel file
            _excelWorkbook.write();
            _excelWorkbook.close();
            EAEventManager.getInstance().fireEAEvent(this, "Done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EAEventManager.getInstance().fireEAEvent(this,
                    MESSAGE_ERROR + ": Could not create the Excel file.");

        }
        
    }
    
    
    
    /**
     * 
     * @throws Exception
     */
    private void initExcelFile() throws Exception {
        //Initialise the excel workbook        
        _excelWorkbook = Workbook.createWorkbook(new File("./Check_AIRM_Datatypes.xls"));
        
        _attributeSheet = _excelWorkbook.createSheet("AIRM Attributes",0);
        _datatypeSheet = _excelWorkbook.createSheet("AIRM Datatypes", 1);
        _unusedDatatypeSheet = _excelWorkbook.createSheet("Unused AIRM Datatypes", 2);
    
    }
    
    
    /**
     * 
     * @param packageCollection
     */
    private void processPackageContentRecursively(Collection packageCollection,
            String parentPackageName) 
    {

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
            
            String packageHierarchy = null; 

            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) 
            {
                subPackage = (Package) packageIter.next();
                
                EAEventManager.getInstance().fireEAEvent(
                        this,
                        "Extracting information from package "
                                + subPackage.GetName());
                
                packageHierarchy = parentPackageName + "::" + subPackage.GetName(); 
                
                
                /////////////////////////////////////////////////
                // Process the classes defined in this subpackage
                /////////////////////////////////////////////////
                for (Iterator elementsIter = subPackage.GetElements()
                        .iterator(); elementsIter.hasNext();) 
                {
                    element = (Element)elementsIter.next();
                    if(!belongsToPackage(element,AIRM_CONSOLIDATED_LOGICAL_MODEL_ENUMERATION_PACKAGE))
                    {
                        if(belongsToPackage(element,AIRM_CONSOLIDATED_LOGICAL_MODEL_SUBJECT_FIELDS_PACKAGE_GUID) || belongsToPackage(element,AIRM_CONSOLIDATED_LOGICAL_MODEL_CONSTRUCTED_TYPES_PACKAGE_GUID))
                        {
                        
                            for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                            {
                                attribute = (Attribute)attributeIter.next();
                                String datatypePath = getAbsoluteReferenceOfDatatype(attribute);
                            
                                // Export to sheet "attributes"
                                try
                                {
                                    addCell(_attributeSheet,1,_currentRowAttribute,attribute.GetName());
                                	addCell(_attributeSheet,2,_currentRowAttribute,parentPackageName + "::" + element.GetName() + "::" + attribute.GetName());
                                	addCell(_attributeSheet,3,_currentRowAttribute,datatypePath);
                                }
                                catch(Exception e)
                            	{
                                	EAEventManager.getInstance().fireEAEvent(
                                        this,
                                        MESSAGE_ERROR + ": Could not export [" + SparxUtilities.toString(element)
                                                + "] to the Excel file");
                            	}
                            	if(datatypePath.length() > 0)
                            	{
                                	updateDatatypesMap(datatypePath,parentPackageName + "::" + element.GetName() + "::" + attribute.GetName());
                            	}
                            	_currentRowAttribute++;
                        	}
                    	}
                    }
                }
                processPackageContentRecursively(subPackage.GetPackages(),subPackage.GetName());
            }
        }
        
    }

    
    protected void listUnusedDatatypes()
    {
        Package datatypesPackage = EAConnection
        .getInstance().getRepository().GetPackageByGuid(
                AIRM_CONSOLIDATED_LOGICAL_MODEL_DATATYPES_PACKAGE_GUID);
        
        if(datatypesPackage != null)
        {
            processDatatypePackageContentRecursively(datatypesPackage.GetPackages());
        }
        else
        {
            EAEventManager.getInstance().fireEAEvent(
                    this,
                    "ERROR: Could not locate package "
                            + datatypesPackage.GetName());
        }
        
    }
    
    
    protected void processDatatypePackageContentRecursively(Collection packageCollection)
    {
        for(Iterator packageIter = packageCollection.iterator();packageIter.hasNext();)
        {
            Package subPackage = (Package)packageIter.next();
            
            /////////////////////////////////////////////////
            // Process the classes defined in this subpackage
            /////////////////////////////////////////////////
            for (Iterator elementsIter = subPackage.GetElements()
                    .iterator(); elementsIter.hasNext();) 
            {
                Element datatype = (Element)elementsIter.next();
                String datatypePath = getAbsoluteReferenceOfElement(datatype);
                if(!_datatypesMap.keySet().contains(datatypePath))
                {
                    try
                    {
                        addCell(_unusedDatatypeSheet,1,_currentRowUnusedDatatype,datatypePath);
                    }
                    catch(Exception e)
                    {
                        EAEventManager.getInstance().fireEAEvent(
                                this,
                                MESSAGE_ERROR + ": Could not export [" + SparxUtilities.toString(datatype)
                                        + "] to the Excel file");
                    }
                    _currentRowUnusedDatatype++;
                }
            }           
            processDatatypePackageContentRecursively(subPackage.GetPackages());
        }
    }
    
    protected boolean belongsToPackage (Element element, String targetPackageGUID)
    {
        Package myPackage = EAConnection.getInstance().getRepository().GetPackageByID(element.GetPackageID());
        return belongsToPackage(myPackage, targetPackageGUID);
    }
    
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
    
    
    protected void updateDatatypesMap (String datatypePath, String attributePath)
    {
        if(_datatypesMap.keySet().contains(datatypePath))
        {
            ((ArrayList)_datatypesMap.get(datatypePath)).add(attributePath);
        }
        else
        {
            ArrayList list = new ArrayList();
            list.add(attributePath);
            _datatypesMap.put(datatypePath,list);
        }
    }
    
    /**
     * 
     * @param datatypeId
     * @return
     */
    protected String getAbsoluteReferenceOfDatatype(Attribute attribute)
    {
        Element datatype = null;
        
        // some attributes have no datatype => list of enumerated values for instance
        if(attribute.GetClassifierID()==0)
            return "";
       try
       {
           datatype = EAConnection.getInstance().getRepository().GetElementByID(attribute.GetClassifierID());
       }
       catch (Exception e)
       {
           return "";
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
     * @param datatypeId
     * @return
     */
    protected String getAbsoluteReferenceOfElement(Element element)
    {
           String reference = element.GetName();
           Package myPackage = EAConnection.getInstance().getRepository().GetPackageByID(element.GetPackageID());
           if(myPackage != null)
           {
               reference = updateReferenceOfDatatype(myPackage, reference);
           }
           return reference;          
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
       Package parentPackage = null;
       try
       {
           parentPackage = EAConnection.getInstance().getRepository().GetPackageByID(myPackage.GetParentID());
       }
       catch (Exception e)
       {
           //do nothing
       }
       if(parentPackage != null)
       {
           result = updateReferenceOfDatatype(parentPackage, result);
       }
       return result;
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
