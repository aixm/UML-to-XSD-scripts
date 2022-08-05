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

package eurocontrol.swim.model.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.AttributeTag;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.ConnectorTag;
import org.sparx.Constraint;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.RoleTag;
import org.sparx.TaggedValue;

import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.sparx.EAConstants;

/**
 * @author hlepori
 */
public class SparxUtilities implements EAConstants{

	public static void addAll(Collection source, ArrayList target) {
		for (Iterator iter = source.iterator(); iter.hasNext();)
		{
			Object obj = iter.next();
			if(!target.contains(obj))
				target.add(obj);
		}
	}
	
	public static String toString(Element element)
	{
		return element.GetType() + " [" + element.GetName() + "]";
	}
	
	public static String toString(Attribute attribute)
	{
		return "Attribute [" + attribute.GetName() + "]";
	}
	
	public static String toString(Attribute attribute, Element element)
	{
		return " [" + element.GetName() + "]." + attribute.GetName();
	}

	
	public static String toString(Connector connector)
	{
		String str1 = "[" + EAConnection.getInstance().getElementByID(connector.GetClientID()).GetName() + "] ";
		String str2 = toString(connector.GetClientEnd())+ " ";
		String str3 = (connector.GetClientEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE)?"<":"");
		String str4 = "---";
		String str5 = (connector.GetSupplierEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE)?"> ":" ");
		String str6 = toString(connector.GetSupplierEnd())+ " ";
		String str7 = "[" + EAConnection.getInstance().getElementByID(connector.GetSupplierID()).GetName() + "] ";
		return connector.GetType() + " {" + str1 + str2 +str3 +str4 +str5 +str6 +str7 + "}";
	}
	
	public static String toString(ConnectorEnd connectorEnd)
	{
		if(connectorEnd.GetRole().length() >0)
			return "+[" + connectorEnd.GetRole()+"]";
		return "";
	}
	
	/**
	 * 
	 * @param taggedValueName
	 * @param aPackage
	 * @return
	 * @throws Exception
	 */
	    public static boolean isTaggedValuePopulated(String taggedValueName,
	            Package aPackage) throws Exception  {
	        Element element = aPackage.GetElement();
	        return isTaggedValuePopulated(taggedValueName, element);
	    }
	

	/**
	 * 
	 * @param taggedValueName
	 * @param element
	 * @return
	 * @throws Exception
	 */
	    public static boolean isTaggedValuePopulated(String taggedValueName,
	            Element element) throws Exception  {
	        element.GetTaggedValues().Refresh();
	        // Note: method GetTaggedValues().GetByName("...") does not
	        // seem to work.
	        for (Iterator taggedValuesIterator = element.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return true;

	            }
	        }
	        return false;
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param connector
	     * @return
	     * @throws Exception
	     */
	    public static boolean isTaggedValuePopulated(String taggedValueName,
	            Connector connector) throws Exception {
	        connector.GetTaggedValues().Refresh();
	        // Note: method GetTaggedValues().GetByName("...") does not
	        // seem to work.
	        for (Iterator taggedValuesIterator = connector.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            ConnectorTag taggedValue = (ConnectorTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return true;

	            }
	        }
	        return false;
	    }

	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param attribute
	     * @return
	     * @throws Exception
	     */
	    public static boolean isTaggedValuePopulated(String taggedValueName,
	            Attribute attribute) throws Exception  {
        	attribute.GetTaggedValues().Refresh();
	        // Note: method GetTaggedValues().GetByName("...") does not
	        // seem to work.
	        for (Iterator taggedValuesIterator = attribute.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            AttributeTag taggedValue = (AttributeTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return true;

	            }
	        }
	        return false;
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param role
	     * @return
	     * @throws Exception
	     */
	    public static boolean isTaggedValuePopulated(String taggedValueName,
	            ConnectorEnd role) throws Exception {
	        role.GetTaggedValues().Refresh();
	        // Note: method GetTaggedValues().GetByName("...") does not
	        // seem to work.
	        for (Iterator taggedValuesIterator = role.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            RoleTag taggedValue = (RoleTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetTag().equals(taggedValueName)) {
	                return true;

	            }
	        }
	        return false;
	    }   
	    
	    /**
	     * Add a tagged value to an element (class)
	     * @param taggedValueName
	     * @param element
	     */
	    public static void addEmptyTaggedValue(String taggedValueName, Element element) throws Exception
	    {
	        addTaggedValue (taggedValueName,element,"");
	    }
	    
	    /**
	     * Add a tagged value to a package
	     * @param taggedValueName
	     * @param aPackage
	     */
	    public static void addTaggedValue(String taggedValueName, Package aPackage, String value) throws Exception
	    {
	        aPackage.GetElement().GetTaggedValues().Refresh();
    	    TaggedValue newTag = (TaggedValue)aPackage.GetElement().GetTaggedValues().AddNew(taggedValueName,value);
    	    boolean isUpdateSuccessful = newTag.Update() && aPackage.Update() && aPackage.GetElement().Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception();
    	    }
	    }
	    
	    /**
	     * Add a tagged value to an element (class)
	     * @param taggedValueName
	     * @param element
	     */
	    public static void addTaggedValue(String taggedValueName, Element element, String value) throws Exception
	    {
	        element.GetTaggedValues().Refresh();
    	    TaggedValue newTag = (TaggedValue)element.GetTaggedValues().AddNew(taggedValueName,value);
    	    boolean isUpdateSuccessful = newTag.Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception(taggedValueName);
    	    }
	    }
	    
	    
	    /**
	     * Add a tagged value to a connector
	     * @param taggedValueName
	     * @param connector
	     */
	    public static void addEmptyTaggedValue(String taggedValueName, Connector connector) throws Exception
	    {
	        addTaggedValue(taggedValueName,connector,"");
	    }
	    
	    /**
	     * Add a tagged value to a connector
	     * @param taggedValueName
	     * @param connector
	     */
	    public static void addTaggedValue(String taggedValueName, Connector connector, String value) throws Exception
	    {
	        connector.GetTaggedValues().Refresh();
            ConnectorTag newTag = (ConnectorTag)connector.GetTaggedValues().AddNew(taggedValueName,value);
            boolean isUpdateSuccessful = newTag.Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception();
    	    }
	    }
	    
	    
	    /**
	     * Add a tagged value to an attribute
	     * @param taggedValueName
	     * @param attribute
	     */
	    public static void addEmptyTaggedValue(String taggedValueName, Attribute attribute) throws Exception
	    {
	        addTaggedValue(taggedValueName,attribute, "");
	    }
	    
	    /**
	     * Add a tagged value to an attribute
	     * @param taggedValueName
	     * @param attribute
	     */
	    public static void addTaggedValue(String taggedValueName, Attribute attribute, String value) throws Exception
	    {
	        attribute.GetTaggedValues().Refresh();
    	    AttributeTag newTag = (AttributeTag)attribute.GetTaggedValues().AddNew(taggedValueName,value);
    	    boolean isUpdateSuccessful = newTag.Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception();
    	    }
	    }
	    
	    
	    
	    /**
	     * Add a tagged value to a role  
	     * @param taggedValueName
	     * @param role
	     */
	    public static void addEmptyTaggedValue(String taggedValueName, ConnectorEnd role) throws Exception
	    {
	        addTaggedValue(taggedValueName, role, "");
	    }
	    
	    /**
	     * Add a tagged value to a role  
	     * @param taggedValueName
	     * @param role
	     */
	    public static void addTaggedValue(String taggedValueName, ConnectorEnd role, String value) throws Exception
	    {        
	        role.GetTaggedValues().Refresh();
            RoleTag newTag = (RoleTag) role.GetTaggedValues().AddNew(
                    taggedValueName, value);
            boolean isUpdateSuccessful = newTag.Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception();
    	    }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param myPackage
	     * @param value
	     * @throws Exception
	     */
	    public static void updateTaggedValue(String taggedValueName, Package myPackage, String value) throws Exception
	    {
	        myPackage.GetElement().GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = myPackage.GetElement().GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                taggedValue.SetValue(value);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @param value
	     * @throws Exception
	     */
	    public static void updateTaggedValue(String taggedValueName, Element element, String value) throws Exception
	    {
	        element.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = element.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                taggedValue.SetValue(value);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param attribute
	     * @param value
	     * @throws Exception
	     */
	    public static void updateTaggedValue(String taggedValueName, Attribute attribute, String value) throws Exception
	    {
	        attribute.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = attribute.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            AttributeTag taggedValue = (AttributeTag) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                taggedValue.SetValue(value);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param attribute
	     * @param value
	     * @throws Exception
	     */
	    public static void updateTaggedValue(String taggedValueName, ConnectorEnd role, String value) throws Exception
	    {
	        role.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = role.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            RoleTag taggedValue = (RoleTag) taggedValuesIterator.next();
	            if (taggedValue.GetTag().equals(taggedValueName)) {
	                taggedValue.SetValue(value);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param myPackage
	     * @return
	     * @throws Exception
	     */
	    public static String getContentOfTaggedValue(String taggedValueName,Package myPackage) throws Exception
	    {
	        for (Iterator taggedValuesIterator = myPackage.GetElement().GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return taggedValue.GetValue();

	            }
	        }
	        return null;
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @return
	     * @throws Exception
	     */
	    public static String getContentOfTaggedValue(String taggedValueName,Element element) throws Exception
	    {
	        for (Iterator taggedValuesIterator = element.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return taggedValue.GetValue();

	            }
	        }
	        return null;
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param connector
	     * @return
	     * @throws Exception
	     */
	    public static String getContentOfTaggedValue(String taggedValueName,Connector connector) throws Exception
	    {
	        for (Iterator taggedValuesIterator = connector.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            ConnectorTag taggedValue = (ConnectorTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return taggedValue.GetValue();

	            }
	        }
	        return null;
	        
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param attribute
	     * @return
	     * @throws Exception
	     */
	    public static String getContentOfTaggedValue(String taggedValueName,Attribute attribute) throws Exception
	    {
	        for (Iterator taggedValuesIterator = attribute.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            AttributeTag taggedValue = (AttributeTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetName().equals(taggedValueName)) {
	                return taggedValue.GetValue();

	            }
	        }
	        return null;
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param role
	     * @return
	     * @throws Exception
	     */
	    public static String getContentOfTaggedValue(String taggedValueName,ConnectorEnd role) throws Exception
	    {
	        for (Iterator taggedValuesIterator = role.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            RoleTag taggedValue = (RoleTag) taggedValuesIterator
	                    .next();
	            if (taggedValue.GetTag().equals(taggedValueName)) {
	                return taggedValue.GetValue();

	            }
	        }
	        return null; 
	    }
	
	    /**
	     * 
	     * @param taggedValueName
	     * @param aPackage
	     * @throws Exception
	     */
	    public static void deleteTaggedValue(String taggedValueName,
            Package aPackage) throws Exception {
	        Element element = aPackage.GetElement();
	        deleteTaggedValue(taggedValueName, element);
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @throws Exception
	     */
	    public static void deleteTaggedValue(String taggedValueName,
            Element element) throws Exception {
	        element.GetTaggedValues().Refresh();
	        
	        short index = -1;
	        boolean deleteTaggedValue = false;
            
	        LOOP: for (Iterator taggedValuesIterator = element
                    .GetTaggedValues().iterator(); taggedValuesIterator
                    .hasNext();) {
	            index++;
                TaggedValue taggedValue = (TaggedValue) taggedValuesIterator
                        .next();
                
                if (taggedValue.GetName().equals(taggedValueName)) {
                    deleteTaggedValue = true;
                   break LOOP; 
                }
            }
        
        if (deleteTaggedValue) {

            element.GetTaggedValues().Delete(index);
            boolean isUpdateSucessful = element.Update();
            if (!isUpdateSucessful) {
                throw new Exception();
            }

        }
    }
  
	    
	    public static void deleteTaggedValue(String taggedValueName,
	            Connector connector) throws Exception {
	        connector.GetTaggedValues().Refresh();
	        
		        short index = -1;
		        boolean deleteTaggedValue = false;
	            
		        LOOP: for (Iterator taggedValuesIterator = connector
	                    .GetTaggedValues().iterator(); taggedValuesIterator
	                    .hasNext();) {
		            index++;
	                ConnectorTag taggedValue = (ConnectorTag) taggedValuesIterator
	                        .next();
	                
	                if (taggedValue.GetName().equals(taggedValueName)) {
	                    deleteTaggedValue = true;
	                   break LOOP; 
	                }
	            }
	        
	        if (deleteTaggedValue) {

	            connector.GetTaggedValues().Delete(index);
	            boolean isUpdateSucessful = connector.Update();
	            if (!isUpdateSucessful) {
	                throw new Exception();
	            }

	        }
	    }   
	    
	    
	    public static void deleteTaggedValue(String taggedValueName,
	            Attribute attribute) throws Exception {
	        	attribute.GetTaggedValues().Refresh();
		        short index = -1;
		        boolean deleteTaggedValue = false;
	            
		        LOOP: for (Iterator taggedValuesIterator = attribute
	                    .GetTaggedValues().iterator(); taggedValuesIterator
	                    .hasNext();) {
		            index++;
	                AttributeTag taggedValue = (AttributeTag) taggedValuesIterator
	                        .next();
	                
	                if (taggedValue.GetName().equals(taggedValueName)) {
	                    deleteTaggedValue = true;
	                   break LOOP; 
	                }
	            }
	        
	        if (deleteTaggedValue) {

	            attribute.GetTaggedValues().Delete(index);	            
	            boolean isUpdateSucessful = attribute.Update();
	            if (!isUpdateSucessful) {
	                throw new Exception();
	            }

	        }
	    }
	    
	    
	    public static void deleteTaggedValue(String taggedValueName,
	            ConnectorEnd role) throws Exception {

	        role.GetTaggedValues().Refresh();
	        
		        short index = -1;
		        boolean deleteTaggedValue = false;
	            
		        LOOP: for (Iterator taggedValuesIterator = role
	                    .GetTaggedValues().iterator(); taggedValuesIterator
	                    .hasNext();) {
		            index++;
	                RoleTag taggedValue = (RoleTag) taggedValuesIterator
	                        .next();
	                
	                if (taggedValue.GetTag().equals(taggedValueName)) {
	                    deleteTaggedValue = true;
	                   break LOOP; 
	                }
	            }
	        
	        if (deleteTaggedValue) {

	            role.GetTaggedValues().Delete(index);
	            boolean isUpdateSucessful = role.Update();
	            if (!isUpdateSucessful) {
	                throw new Exception();
	            }

	        }
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @param value
	     * @throws Exception
	     */
	    public static void renameTaggedValue(String taggedValueOldName, String taggedValueNewName, Element element) throws Exception
	    {
	        element.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = element.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            TaggedValue taggedValue = (TaggedValue) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueOldName)) {
	                taggedValue.SetName(taggedValueNewName);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @param value
	     * @throws Exception
	     */
	    public static void renameTaggedValue(String taggedValueOldName, String taggedValueNewName, Attribute attribute) throws Exception
	    {
	        attribute.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = attribute.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            AttributeTag taggedValue = (AttributeTag) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueOldName)) {
	                taggedValue.SetName(taggedValueNewName);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @param value
	     * @throws Exception
	     */
	    public static void renameTaggedValue(String taggedValueOldName, String taggedValueNewName, Connector connector) throws Exception
	    {
	        connector.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = connector.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            ConnectorTag taggedValue = (ConnectorTag) taggedValuesIterator.next();
	            if (taggedValue.GetName().equals(taggedValueOldName)) {
	                taggedValue.SetName(taggedValueNewName);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @param value
	     * @throws Exception
	     */
	    public static void renameTaggedValue(String taggedValueOldName, String taggedValueNewName, ConnectorEnd role) throws Exception
	    {
	        role.GetTaggedValues().Refresh();
	        for (Iterator taggedValuesIterator = role.GetTaggedValues()
	                .iterator(); taggedValuesIterator.hasNext();) {
	            RoleTag taggedValue = (RoleTag) taggedValuesIterator.next();
	            if (taggedValue.GetTag().equals(taggedValueOldName)) {
	                taggedValue.SetTag(taggedValueNewName);
		    	    boolean isUpdateSuccessful = taggedValue.Update();
		    	    if(!isUpdateSuccessful)
		    	    {
		    	        throw new Exception();
		    	    }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param htmlText
	     * @return
	     */
	    public static String removeHTMLTagsFromText(String htmlText)
	    {	
	        return htmlText.replaceAll("\\<.*?>","");	    
	    }
	    
	    /**
	     * 
	     * @param element
	     * @param targetPackageGUID
	     * @return
	     */
	    public static boolean belongsToPackage (Element element, String targetPackageGUID)
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
	    public static boolean belongsToPackage(Package aPackage, String targetPackageGUID)
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
	    
	    /**
	     * 
	     * @param element
	     * @param targetPackageGUID
	     * @return
	     */
	    public static boolean belongsToPackageNamed (Element element, String targetPackageName)
	    {
	        Package myPackage = EAConnection.getInstance().getRepository().GetPackageByID(element.GetPackageID());
	        return belongsToPackageNamed(myPackage, targetPackageName);
	    }
	
	    /**
	     * 
	     * @param aPackage
	     * @param targetPackageGUID
	     * @return
	     */
	    public static boolean belongsToPackageNamed(Package aPackage, String targetPackageName)
	    {
	        if(aPackage.GetName().equals(targetPackageName))
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
	        
	        return belongsToPackageNamed(parentPackage, targetPackageName);
	    }
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @throws Exception
	     */
	    public static void deleteAttribute(Attribute attribute,
            Element element) throws Exception {
	        
	        element.GetAttributes().Refresh();
	        
	        short index = -1;
	        boolean deleteAttribute = false;
            
	        LOOP: for (Iterator attributeIterator = element
                    .GetAttributes().iterator(); attributeIterator
                    .hasNext();) {
	            index++;
                Attribute attr = (Attribute) attributeIterator
                        .next();
                
                if (attribute.GetAttributeGUID().equals(attr.GetAttributeGUID())) {
                    deleteAttribute = true;
                   break LOOP; 
                }
            }
        
        if (deleteAttribute) {

            element.GetAttributes().Delete(index);
            boolean isUpdateSucessful = element.Update();
            if (!isUpdateSucessful) {
                throw new Exception();
            }

        }
    }
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @throws Exception
	     */
	    public static void deleteConnector(Connector connector,
            Element element) throws Exception {
	        
	        element.GetConnectors().Refresh();
	        
	        short index = -1;
	        boolean deleteConnector = false;
            
	        LOOP: for (Iterator connectorIterator = element
                    .GetConnectors().iterator(); connectorIterator
                    .hasNext();) {
	            index++;
	            Connector c = (Connector) connectorIterator
                        .next();
                
                if (connector.GetConnectorGUID().equals(c.GetConnectorGUID())) {
                    deleteConnector = true;
                   break LOOP; 
                }
            }
        
        if (deleteConnector) {

            element.GetConnectors().Delete(index);
            boolean isUpdateSucessful = element.Update();
            if (!isUpdateSucessful) {
                throw new Exception();
            }

        }
    }  
	    
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @throws Exception
	     */
	    public static void deleteElement(Element element,
            Package aPackage) throws Exception {
	        
	        aPackage.GetElements().Refresh();
	        
	        short index = -1;
	        boolean deleteElement = false;
            
	        LOOP: for (Iterator elementIterator = aPackage
                    .GetElements().iterator(); elementIterator
                    .hasNext();) {
	            index++;
                Element e = (Element) elementIterator.next();
                
                if (element.GetElementGUID().equals(e.GetElementGUID())) {
                    deleteElement = true;
                   break LOOP; 
                }
            }
        
        if (deleteElement) {

            aPackage.GetElements().Delete(index);
            boolean isUpdateSucessful = aPackage.Update();
            if (!isUpdateSucessful) {
                throw new Exception();
            }

        }
    }
	    
	    /**
	     * 
	     * @param taggedValueName
	     * @param element
	     * @throws Exception
	     */
	    public static void deletePackage(Package aPackage) throws Exception {
	        
	        Package parentPackage = EAConnection.getInstance().getRepository().GetPackageByID(aPackage.GetParentID());
	        if(parentPackage != null)
	        {
		        parentPackage.GetElements().Refresh();
		        
		        short index = -1;
		        boolean deletePackage = false;
	            
		        LOOP: for (Iterator packageIterator = parentPackage
	                    .GetPackages().iterator(); packageIterator
	                    .hasNext();) 
		        {
		            index++;
	                Package p = (Package) packageIterator.next();
	                
	                if (aPackage.GetPackageGUID().equals(p.GetPackageGUID())) {
	                    deletePackage = true;
	                   break LOOP; 
	                }
	            }	        
		        if (deletePackage) 
		        {
		            parentPackage.GetPackages().Delete(index);
		            boolean isUpdateSucessful = parentPackage.Update();
		            if (!isUpdateSucessful) 
		            {
		                throw new Exception();
		            }
		        }
	        }
	    }
	    
	    
	    public static int getClassCount(Package aPackage)
	    {
	        int count = 0;	        
	        for (Iterator iter = aPackage.GetElements().iterator();iter.hasNext();)
	        {
	            Element element = (Element) iter.next();
	            if(element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION))
	                count++;
	        }	        
	        return count;
	    }
	    
	    
	    public static String getLowerMultiplicity(Connector connector, Element targetElement) throws Exception
	    {
	        String multiplicity = null;
	        if (connector.GetClientID() == targetElement.GetElementID())
	            multiplicity = connector.GetClientEnd().GetCardinality();
	        
	        else if (connector.GetSupplierID() == targetElement.GetElementID())
	            multiplicity = connector.GetSupplierEnd().GetCardinality();
	        
	        else
	            throw new Exception();

            return getLowerMultiplicity(multiplicity);
        }



	    
	    public static String getUpperMultiplicity(Connector connector, Element targetElement) throws Exception
	    {
	        String multiplicity = null;
	        if (connector.GetClientID() == targetElement.GetElementID())
	            multiplicity = connector.GetClientEnd().GetCardinality();
	        
	        else if (connector.GetSupplierID() == targetElement.GetElementID())
	            multiplicity = connector.GetSupplierEnd().GetCardinality();
	        
	        else
	            throw new Exception();

            return getUpperMultiplicity(multiplicity);
        }

	// OVA2015 AIXMSCR-4
    public static String getLowerMultiplicity(String cardinality) throws Exception
    {
        if(cardinality.contains(".."))
        {
            String result = cardinality.substring(0,1);
            if(result.equals("*")) return "unbounded";
            return result;
        }
        else if(cardinality.length()== 1)
        {
            return cardinality;
        }
        else
        {
            throw new Exception();
        }

    }

		// OVA2015 AIXMSCR-4
        public static String getUpperMultiplicity(String cardinality) throws Exception
        {
	        if(cardinality.contains(".."))
	        {
                String result = cardinality.substring(cardinality.length()-1);
                if(result.equals("*")) return "unbounded";
                return result;
	        }
	        else if(cardinality.length()== 1)
	        {
	            return cardinality;
	        }
	        else
	        {
	            throw new Exception();
	        }
	       
	    }

	    
	    /**
	     * 
	     * @param attribute
	     * @return
	     */
	    public static Element getUMLDatatype(Attribute umlAttribute)
	    {
	        if(umlAttribute.GetClassifierID()<=0)
	            return null;
            return EAConnection.getInstance().getElementByID(umlAttribute.GetClassifierID()); 
	    }    
	    
	    
	    /**
	     * Add a constraint to an element (class)
	     * @param taggedValueName
	     * @param element
	     */
	    public static void addConstraint(String constraintName, Element element, String value) throws Exception
	    {
	        element.GetConstraints().Refresh();
    	    Constraint newConstraint = (Constraint)element.GetConstraints().AddNew(constraintName,value);
    	    boolean isUpdateSuccessful = newConstraint.Update();
    	    if(!isUpdateSuccessful)
    	    {
    	        throw new Exception(constraintName);
    	    }
	    }
	    
	    /**
	     * Test is a connector is a self-association 
	     * @param connector the connector to be tested
	     * @return true is the the connector is a self-association, or false otherwise.
	     */
	    //HLE2022 AIXM CR...
	    public static boolean isSelfAssociation(Connector connector)
	    {
	    	return connector.GetClientID() == connector.GetSupplierID();
	    }
}
