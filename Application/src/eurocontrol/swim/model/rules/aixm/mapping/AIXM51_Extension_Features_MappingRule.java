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

package eurocontrol.swim.model.rules.aixm.mapping;

import java.io.IOException;
import java.util.Iterator;

import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIXMExtensionsConstants;

/**
 * @author hlepori
 */
public class AIXM51_Extension_Features_MappingRule extends
        AIXM51_Features_MappingRule implements AIXMExtensionsConstants{

    /**
     * AIXM extension properties
     */
    protected String _extensionVersion;
    
    protected String _extensionDatatypesFileName;
    
    protected String _extensionFeaturesFileNameForMessage;
    
    protected Package _featurePackageForMessage;
    
    /**
     * @param targetPackage
     * @throws IOException
     */
    public AIXM51_Extension_Features_MappingRule(Package targetPackage, boolean isExtFeatures,String extensionDatatypesFileName, Package featurePackageForMessage, String extensionFeaturesFileNameForMessage) throws IOException {
        super(targetPackage);
        _extensionDatatypesFileName = extensionDatatypesFileName;
        isGeneratingExtensionFeatures = isExtFeatures;
        _extensionFeaturesFileNameForMessage = extensionFeaturesFileNameForMessage;
        _featurePackageForMessage = featurePackageForMessage;
    }

    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.AIXM51_Datatypes_MappingRule#initXSDFileProperties()
     */
    protected void initXSDFileProperties() {        
        super.initXSDFileProperties();
       
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(AIXM_TAGGED_VALUE_EXTENSION_VERSION,_targetPackage.GetElement()))
                _extensionVersion = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_EXTENSION_VERSION,_targetPackage.GetElement());
            else
                throw new Exception();
        }
        catch (Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + AIXM_TAGGED_VALUE_EXTENSION_VERSION  + ". Using default value.");
        }
    }    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.AIXM51_Datatypes_MappingRule#setDefaultValues()
     */
    protected void setDefaultValues() {
        _extensionVersion = AIXM_51_EXTENSION_DEFAULT_EXTENSION_VERSION;
    	_generateFileName = AIXM_51_EXTENSION_DEFAULT_GENERATE_FILE_NAME_FEATURES;
        _targetNamespace = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE;
        _targetNamespacePrefix = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE_PREFIX;
    	_attributeFormDefault = AIXM_51_EXTENSION_DEFAULT_ATTRIBUTE_FORM;
    	_elementFormDefault = AIXM_51_EXTENSION_DEFAULT_ELEMENT_FORM;
    }


    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.AIXM51_Datatypes_MappingRule#createRoot()
     */
    protected org.w3c.dom.Element createRoot() {
        
        org.w3c.dom.Element root = _xmldoc.createElement(XSD_TAG_ROOT);
        root.setAttribute(XSD_XML_NAMESPACE, XSD_DEFAULT_TARGET_NAMESPACE);
        
        ////////////////////////////////////////
        // Define the XML attribute of the root
        ////////////////////////////////////////
        String coreTargetNamespacePrefix = AIXM_51_DEFAULT_TARGET_NAMESPACE_PREFIX;
        String coreTargetNamespace = AIXM_51_DEFAULT_TARGET_NAMESPACE;
		try
		{
		    coreTargetNamespacePrefix = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_FEATURES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace prefix for the core AIXM datatypes. Using a default value.");
		}
		// OVA2015 AIXMSCR-3
        try
        {
            extensionRelativePathPrefix = null;
            extensionFileLocation = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION, _targetPackage.GetElement());
            if (extensionFileLocation==null || extensionFileLocation.length()==0) throw new Exception("tagged value not found");
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the extension file location ("+ AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION +") from the AIXM Features package. Using the default value.");
            extensionFileLocation = AIXM_51_DEFAULT_EXTENSION_FILE_LOCATION;
        }

		try
		{
		    coreTargetNamespace = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_FEATURES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace for the core AIXM datatypes. Using a default value.");
		}
        
	    root.setAttribute(XSD_XML_NAMESPACE + ":" + _targetNamespacePrefix, _targetNamespace); 	    
	    root.setAttribute(XSD_XML_NAMESPACE, XSD_DEFAULT_TARGET_NAMESPACE);	       
		root.setAttribute(XSD_XML_NAMESPACE + ":" + XSD_DEFAULT_TARGET_NAMESPACE_PREFIX, XSD_DEFAULT_TARGET_NAMESPACE);		
		//root.setAttribute(XSD_XML_NAMESPACE + ":" + "gml", XSD_HTTP_GML_SCHEMA);
		//root.setAttribute(XSD_XML_NAMESPACE + ":" + "xlink", XSD_HTTP_XLINK_SCHEMA);
	    //root.setAttribute(XSD_XML_NAMESPACE + ":" + coreTargetNamespacePrefix, coreTargetNamespace);
		root.setAttribute("targetNamespace",_targetNamespace);		
		root.setAttribute("elementFormDefault", _elementFormDefault);
		root.setAttribute("attributeFormDefault", _attributeFormDefault);
		root.setAttribute("version", _extensionVersion);

        String extensionTargetNamespacePrefix = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE_PREFIX;
        String extensionTargetNamespace = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE;
		String extensionFeaturesFileName = AIXM_51_EXTENSION_DEFAULT_GENERATE_FILE_NAME_FEATURES;
		
		if(_featurePackageForMessage != null)
		{
			try
			{
			    extensionTargetNamespacePrefix = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, _featurePackageForMessage.GetElement());
			}
			catch(Exception e)
			{
			    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace prefix for the extension. Using a default value.");
			}
			
			try
			{
			    extensionTargetNamespace = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE, _featurePackageForMessage.GetElement());
			}
			catch(Exception e)
			{
			    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace for the extension. Using a default value.");
			}
			try
			{
			    extensionFeaturesFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, _featurePackageForMessage.GetElement());
			}
			catch(Exception e)
			{
			    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the file name for the extension. Using a default file name.");
			}
			
			root.setAttribute(XSD_XML_NAMESPACE + ":" + extensionTargetNamespacePrefix, extensionTargetNamespace);
		}
        ////////////////////////////////////////
        // Define the <import> elements
        ////////////////////////////////////////

        //include dependencies between files
		// OVA2015 AIXMSCR-1
        importsMap.put(XSD_HTTP_GML_SCHEMA_LOCATION, new String[]{XSD_HTTP_GML_SCHEMA_PREFIX, XSD_HTTP_GML_SCHEMA,null});
        importsMap.put(XSD_HTTP_XLINK_SCHEMA_LOCATION, new String[]{XSD_HTTP_XLINK_SCHEMA_PREFIX, XSD_HTTP_XLINK_SCHEMA,null});

        if(_featurePackageForMessage != null  && !extensionTargetNamespace.equals(_targetNamespace)) {
            // OVA2015 AIXMSCR-3
            importsMap.put(_extensionDatatypesFileName, new String[]{extensionTargetNamespacePrefix, extensionTargetNamespace,null});
        }

        ////////////////////////////////////////
        // Define the <include> elements
        ////////////////////////////////////////
        /*             already set through dynamic links
        if(_extensionDatatypesFileName != null) {
            includesSet.add(new String[]{_extensionDatatypesFileName,""});
        }
        */

        if(_featurePackageForMessage != null) {
            inspectPackageForLinks(_targetPackage, extensionTargetNamespace);
        } else {
            inspectPackageForLinks(_targetPackage, _targetNamespace);
        }
		// OVA2015 AIXMSCR-3
        addLinksToXSDRoot(root,true);
	
        return root;
    }

    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.AIXM51_Features_MappingRule#processPackage(org.sparx.Package)
     */
    protected void processPackage(Package aPackage) {
        EAEventManager.getInstance().fireEAEvent(this,"Processing package " + aPackage.GetName());
        for (Iterator elementIter = aPackage.GetElements().iterator();elementIter.hasNext();)
        {
            Element element = (Element)elementIter.next();

            if (autoInheritance)
            {
                if (timeslicesAutoInheritance && element.GetStereotype().equals(AIXM_51_STEREOTYPE_FEATURE) )
                {
                    //AIXMSCR-13
                    if (!element.GetAbstract().equalsIgnoreCase("1") && !element.GetName().toLowerCase().startsWith("abstract"))
                    {
                        _root.appendChild(createExtension(element,false));
                        _root.appendChild(createExtensionType(element));
                        _root.appendChild(createExtensionPropertyGroup(element));
                    }
                }
                else if (objectsAutoInheritance && element.GetStereotype().equals(AIXM_51_STEREOTYPE_OBJECT))
                {
                    //AIXMSCR-13
                    if (!element.GetAbstract().equalsIgnoreCase("1") && !element.GetName().toLowerCase().startsWith("abstract"))
                    {
                        _root.appendChild(createExtension(element,false));
                        _root.appendChild(createExtensionType(element));
                        _root.appendChild(createExtensionPropertyGroup(element));
                    }
                }
            }
            else
            {
                if(element.GetStereotype().equals(AIXM_51_STEREOTYPE_FEATURE))
                {
                    _root.appendChild(createFeaturePropertyType(element));
                    _root.appendChild(createFeature(element));
                    _root.appendChild(createFeatureType(element));
                    if(!element.GetAbstract().equals("1"))
                    {
                        _root.appendChild(createFeatureTimeSlicePropertyType(element));
                        _root.appendChild(createFeatureTimeSlice(element));
                        _root.appendChild(createFeatureTimeSliceType(element));
                    }
                    _root.appendChild(createFeaturePropertyGroup(element));
                    _root.appendChild(createAbstractFeatureExtension(element));
                }
                else if (element.GetStereotype().equals(AIXM_51_STEREOTYPE_OBJECT))
                {
                        _root.appendChild(createObject(element));
                        _root.appendChild(createObjectType(element));
                        _root.appendChild(createObjectPropertyGroup(element));
                        _root.appendChild(createObjectPropertyType(element));
                        _root.appendChild(createAbstractObjectExtension(element));
                }
                else if (element.GetStereotype().equals(AIXM_51_STEREOTYPE_CHOICE))
                {
                    // Do nothing - the choices are processed directly when creating the "property groups"
                }
                else if (element.GetStereotype().equals(AIXM_51_STEREOTYPE_EXTENSION))
                {
                    if (!ignoreExtensionStereotypes)
                    {
                        _root.appendChild(createExtension(element,skipSubstitutionGroup));
                        _root.appendChild(createExtensionType(element));
                        _root.appendChild(createExtensionPropertyGroup(element));
                    }
                }
                else if (element.GetStereotype().equals(AIXM_51_STEREOTYPE_MESSAGE))
                {
                    _root.appendChild(createMessage(element));
                    _root.appendChild(createMessageType(element));
                    _root.appendChild(createMessagePropertyGroup(element));
                }
                else if (element.GetStereotype().equals(AIXM_51_STEREOTYPE_COLLECTION_MEMBER_CHOICE))
                {
                    _root.appendChild(createCollectionMemberChoicePropertyType(element));
                }
                else
                {
                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Unknown stereotype for " + SparxUtilities.toString(element));
                }
            }
        }
    }
    
    
    /**
     * 
     * @param umlExtension
     * @return
     */
	protected org.w3c.dom.Element createExtension(Element umlExtension,boolean skipSubstitutionGroup)
	{	    
	    String name = umlExtension.GetName() + "Extension";	  
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);

        boolean _includeDeprecation = umlExtension.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
	    
	    if(umlExtension.GetAbstract().equalsIgnoreCase("1"))
	    {
	        name = "Abstract" + name;
	        element.setAttribute("abstract","true");
	    }
	    element.setAttribute("name",name);
	    element.setAttribute("type", getNamespacePrefixForElement(umlExtension) + ":" + name + "Type");	    	    
	    if (!skipSubstitutionGroup) element.setAttribute("substitutionGroup","aixm" + ":" + "Abstract"+ name);
	    
	    if(_includeDocumentation && !autoInheritance || _includeDeprecation)
	        element.appendChild(createAnnotation(umlExtension, _includeDeprecation, umlExtension.GetNotes()));
	    
	    return element;
	}
    
	/**                                               "abstract"
	 *
	 * @return
	 */
	protected org.w3c.dom.Element createExtensionType(Element umlExtension)
	{
	    String name = umlExtension.GetName() + "Extension";
	    
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    if(umlExtension.GetAbstract().equals("1"))
	    {
	        name = "Abstract" + name;
	        complexType.setAttribute("abstract","true");
	    }
	    
	    complexType.setAttribute("name", name + "Type");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    extension.setAttribute("base", "aixm" + ":" + "AbstractExtensionType");
	    	    
	    // For non abstract classes
	    if(!umlExtension.GetAbstract().equals("1"))
	    {
		    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
		    
		    org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
		    group.setAttribute("ref", getNamespacePrefixForElement(umlExtension) + ":" + name + "PropertyGroup");
		    
		    sequence.appendChild(group);
		    extension.appendChild(sequence);
	    }
	    
	    complexContent.appendChild(extension);
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
	/**
	 * 
	 * @param umlExtension
	 * @return
	 */
	protected org.w3c.dom.Element createExtensionPropertyGroup(Element umlExtension)
	{	    
	    // "For the most part, the XML schema entities are created in the same way as for Features, following the object-property model."	    
	    umlExtension.SetName(umlExtension.GetName() + "Extension");  
	    return createFeaturePropertyGroup(umlExtension);
	}
	
    
    /**
     *
     * @return
     */
	protected org.w3c.dom.Element createMessage(Element message)
	{	    
	    String name = message.GetName();	  
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);

	    element.setAttribute("name",name);
	    element.setAttribute("type", getNamespacePrefixForElement(message) + ":" + name + "Type");	    	    
	    element.setAttribute("substitutionGroup","gml" + ":" + "AbstractFeature");

        boolean _includeDeprecation = message.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
	    if(_includeDocumentation || _includeDeprecation)
	        element.appendChild(createAnnotation(message, _includeDeprecation, message.GetNotes()));
	    
	    return element;
	}
	
	/**
	 * @return
	 */
	protected org.w3c.dom.Element createMessageType(Element message)
	{
	    String name = message.GetName();
	    
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);	    
	    complexType.setAttribute("name", name + "Type");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    extension.setAttribute("base", "aixm" + ":" + "AbstractAIXMMessageType");
	    	    
	    // For non abstract classes
	    if(!message.GetAbstract().equals("1"))
	    {
		    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
		    
		    org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
		    group.setAttribute("ref", getNamespacePrefixForElement(message) + ":" + name + "PropertyGroup");
		    
		    sequence.appendChild(group);
		    extension.appendChild(sequence);
	    }
	    
	    complexContent.appendChild(extension);
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
	/**
	 *
	 * @return
	 */
	protected org.w3c.dom.Element createMessagePropertyGroup(Element message)
	{	    
	    String name = message.GetName();
	    org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
	    group.setAttribute("name", name + "PropertyGroup");
	    
	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
	    
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    
	    Collection connectors =  message.GetConnectors();
	    if(connectors.GetCount()==1)
	    {
	        Connector connector = (Connector)connectors.GetAt((short)0);
            int id = connector.GetClientID();
            
            if(id == message.GetElementID())
            {
                id =  connector.GetSupplierID();
            } 
            Element messageMember = EAConnection.getInstance().getRepository().GetElementByID(id);
    
	        element.setAttribute("name","hasMember");
	        element.setAttribute("type",getNamespacePrefixForElement(message) + ":" + messageMember.GetName() + "PropertyType");
	        element.setAttribute("maxOccurs","unbounded");

            // OVA2015 AIXMSCR-5
            //a few connectors also have notes
            String connectorNotes = connector.GetNotes();
            boolean _includeDeprecation = connector.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
            if (connectorNotes!=null && connectorNotes.length()!=0)  {
                if(_includeDocumentation || _includeDeprecation) {
                    element.appendChild(createAnnotation(connector, _includeDeprecation, connectorNotes));
                }
            }

	        sequence.appendChild(element);
	    }
	    else
	    {
	        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": It is currently assumed that a message has only one association to collectionMemberChoice.");
	    }
	    	  
	    sequence.appendChild(element);
	    group.appendChild(sequence);
	    
	    return group;
	}
	
	/**
	 * 
	 * @param collectionMemberChoice
	 * @return
	 */
	protected org.w3c.dom.Element createCollectionMemberChoicePropertyType(Element collectionMemberChoice)
	{
	    String name = collectionMemberChoice.GetName();
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    complexType.setAttribute("name", name + "PropertyType");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    extension.setAttribute("base", "gml" + ":" + "AbstractFeatureMemberType");
	    
	    org.w3c.dom.Element choice = _xmldoc.createElement(XSD_TAG_CHOICE);
	    choice.setAttribute("maxOccurs", "unbounded");
	    
	    for (Iterator iter = collectionMemberChoice.GetConnectors().iterator();iter.hasNext();)
	    {
	        Connector connector = (Connector) iter.next();
            
	        int id = connector.GetClientID();            
            if(id == collectionMemberChoice.GetElementID())
            {
                id =  connector.GetSupplierID();
            }
            
            Element messageMember = EAConnection.getInstance().getRepository().GetElementByID(id);
            
            if(messageMember != null && !messageMember.GetStereotype().equals("Message"))
            {
        	    String elementName = messageMember.GetName();
        	    if(messageMember.GetAbstract().equals("1"))
        	        elementName = "Abstract" + elementName;
        	    
        	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
        	    element.setAttribute("ref",getNamespacePrefixForElement(messageMember) + ":" + elementName);
        	    choice.appendChild(element);
            }
	    }	    
	    
	    extension.appendChild(choice);
	    
	    complexContent.appendChild(extension);
    	    
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
}
