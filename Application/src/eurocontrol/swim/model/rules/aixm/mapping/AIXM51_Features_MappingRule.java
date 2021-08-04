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

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractMappingRule;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIXMConstants;
import org.sparx.*;
import org.sparx.Collection;
import org.sparx.Package;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.*;

/**
 * @author hlepori
 */
public class AIXM51_Features_MappingRule  extends AbstractMappingRule implements AIXMConstants{

    static private HashMap<String,String> typesMarkedForNilreason = new HashMap<String,String>();

    /**
     * AIXM properties
     */
    protected String _aixmCoreVersion;
    protected String _generateFileName;
    
    /**
     * An arraylist used to store the GUID of the processed SparxEA elements 
     */
    protected ArrayList _processedGUIDs = new ArrayList();
    
    /**
     * 
     */
    protected HashMap _hashMapForAssociationClasses = new HashMap();
    
    /**
     * 
     */
    protected HashMap _hashMapForDatatypes = new HashMap();
    
    /**
     * @param
     * @throws IOException
     */
    public AIXM51_Features_MappingRule(Package targetPackage)
            throws IOException {
        super(targetPackage);
        setDefaultValues();
        initXSDFileProperties();
        initXSDFile();
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.MappingRule#getOutputFile()
     */
    public String getOutputFile() {
        return "./output/" + _generateFileName;
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.MappingRule#initXSDFileProperties()
     */
    protected void initXSDFileProperties() {
        
        super.initXSDFileProperties();     
        
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(AIXM_TAGGED_VALUE_CORE_VERSION,_targetPackage.GetElement()))
                _aixmCoreVersion = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_CORE_VERSION,_targetPackage.GetElement());           
        }
        catch (Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The package " + _targetPackage.GetName() + " has no tagged value " + AIXM_TAGGED_VALUE_CORE_VERSION  + ". Using default value.");
        }
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME,_targetPackage.GetElement()))
                _generateFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME,_targetPackage.GetElement());
        }
        catch (Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The package " + _targetPackage.GetName() + " has no tagged value " + AIXM_TAGGED_VALUE_GENERATE_FILE_NAME + ". Using default value.");
        }
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.MappingRule#setDefaultValues()
     */
    protected void setDefaultValues() {
        _aixmCoreVersion = AIXM_51_DEFAULT_CORE_VERSION;
    	_generateFileName = AIXM_51_DEFAULT_GENERATE_FILE_NAME_FEATURES;
        _targetNamespace = AIXM_51_DEFAULT_TARGET_NAMESPACE;
    	_targetNamespacePrefix = AIXM_51_DEFAULT_TARGET_NAMESPACE_PREFIX;
    	_attributeFormDefault = XSD_DEFAULT_ATTRIBUTE_FORM;
    	_elementFormDefault = XSD_DEFAULT_ELEMENT_FORM;
    }

    protected boolean skipSubstitutionGroup = false;
    protected boolean autoInheritance = false;//AIXMSCR-13
    protected boolean timeslicesAutoInheritance = false;//AIXMSCR-13
    protected boolean objectsAutoInheritance = false;//AIXMSCR-13
    protected boolean isGeneratingExtensionFeatures = false;
    protected boolean ignoreExtensionStereotypes = false;

    public void generateXSD() throws IOException {

        _root = this.createRoot();

        if(_root != null)
        {
            //AIXMSCR-13
            //detect if render only the overridden AIXMTimeslice properties and connectors
            Package _targetPackageBkp = null;
            if (isGeneratingExtensionFeatures)
                autoInheritance = prepareElementsForAutoInheritance();
            else
                autoInheritance = false;
            if (autoInheritance){
                _targetPackageBkp = _targetPackage;
                //switch input to the AIXM features package (non-feature elements, properties and associations will be ignored because allFeaturesDetected is true)
                Package aixmFeaturesPackage = EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_FEATURES);
                _targetPackage = aixmFeaturesPackage;
            }

            processMainPackage();
            //addNilreasonAttributes();// OVA2015 AIXMSCR-2 - commented for AIXMSCR-11

            //AIXMSCR-13
            if (autoInheritance){
                //add the overridden AIXMTimeslice properties and connectors on all features
                //addElementsForAllFeatures();// OVA2015 AIXMSCR-8   AIXMSCR-13:obsolete
                //restore normal package
                _targetPackage = _targetPackageBkp;
                autoInheritance = false;
                //process all the non-extension content from the extension features package (AIXMTimeslice/AIXMOBject might be associated to new elements)
                ignoreExtensionStereotypes = true;
                processMainPackage();
                ignoreExtensionStereotypes = false;
            }
            _serializer.serialize(_root);
        }  
    }

    private void processMainPackage()
    {
        buildHashMapForAssociationClasses(_targetPackage);
        buildHashMapForDatatypes();
        Collection packagesToBeProcessed = _targetPackage.GetPackages();
        if(packagesToBeProcessed.GetCount()>0)
        {
            processRecursively(_targetPackage.GetPackages());
        }
        else
        {
            processPackage(_targetPackage);
        }
    }
    
    protected void buildHashMapForAssociationClasses(Package pack)
    {
        //modified by pulsar on 20190612 following issue from porosnie/20190304 "Incorrect generation of extension for association class towards feature"

        for (Iterator elementIter = pack.GetElements().iterator();elementIter.hasNext();)
        {
            Element element = (Element)elementIter.next();
            // Check the validity of the class
            checkElement(element);

            /////////////////////////////////
            // A numeric subtype qualifies the Type of the main element. For Class: 1 = Parameterised, 2 = Instantiated, 3 = Both, 0 = Neither, 17 = Association Class
            // If 17, because an Association Class has been created through the user interface, MiscData(3) will contain the ID of the related Association.
            // http://www.sparxsystems.com/uml_tool_guide/sdk_for_enterprise_architect/element2.htm
            /////////////////////////////////
            if(element.GetSubtype() == 17)
            {
                _hashMapForAssociationClasses.put(EAConnection.getInstance().getRepository().GetConnectorByID(Integer.parseInt(element.MiscData(3))).GetConnectorGUID(), element.GetElementGUID());
            }
        }
        for (Iterator packageIter = pack.GetPackages().iterator();packageIter.hasNext();)
        {
            Package subPackage = (Package)packageIter.next();
            buildHashMapForAssociationClasses(subPackage);
        }
    }
    
    /**
     * 
     *
     */
    protected void buildHashMapForDatatypes()
    {
	    Package datatypePackage = EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES);
        if(datatypePackage != null)
        {
            for(Iterator iter = datatypePackage.GetElements().iterator();iter.hasNext();)
            {
                Element datatype = (Element)iter.next();
                _hashMapForDatatypes.put(datatype.GetName(),datatype);
            }
        }
    }
    
    /**
     * 
     * @param subPackages
     */
    protected void processRecursively(Collection subPackages)
    {
        for (Iterator packageIter = subPackages.iterator();packageIter.hasNext();)
        {
            Package aPackage = (Package)packageIter.next();
            processPackage(aPackage);            
            // process recursively the subpackages
            processRecursively(aPackage.GetPackages());
        }
    }

    static private Vector<org.w3c.dom.Node> elementsForAllTimeslices = null;
    static private Vector<org.w3c.dom.Node> elementsForAllObjects = null;

    //AIXMSCR-13
    public boolean prepareElementsForAutoInheritance()
    {
        elementsForAllTimeslices = getInheritedElements(AIXM_51_GUID_CLASS_AIXMTIMESLICE,"AIXMTimeSlice");
        timeslicesAutoInheritance = elementsForAllTimeslices.size()!=0;
        elementsForAllObjects = getInheritedElements(AIXM_51_GUID_CLASS_AIXMOBJECT,"AIXMObject");
        objectsAutoInheritance = elementsForAllObjects.size()!=0;
        autoInheritance = timeslicesAutoInheritance || objectsAutoInheritance;
        if (autoInheritance)
        {
            EAEventManager.getInstance().fireEAEvent(this,"Switching into ALL_FEATURES mode: all the AIXM features/objects will be extended with the properties and associations of the overridden AIXMTimeslice/AIXMObjects");
            EAEventManager.getInstance().fireEAEvent(this,"Warning! ALL THE OTHER ELEMENTS FROM THE EXTENSION FEATURES PACKAGE WILL BE IGNORED (ALL_FEATURES mode is not compatible with manual extensions)");
            return true;
        }
        else
        {
            return false;
        }
    }

    public Vector<org.w3c.dom.Node> getInheritedElements(String elementGUID,String elementName)
    {
        skipSubstitutionGroup = true;
        Vector<org.w3c.dom.Node> inheritedElements = null;
        if (inheritedElements==null)
        {
            inheritedElements = new Vector<org.w3c.dom.Node>();// OVA2015 AIXMSCR-8
            //store elements for the "all feature" functionality - OVA2015 AIXMSCR-8
            Element aixmTimeslice = EAConnection.getInstance().getRepository().GetElementByGuid(elementGUID);
            if (aixmTimeslice!=null)
            {
                //for each inheriting "extension" connectors
                Iterator connectorIter = aixmTimeslice.GetConnectors().iterator();
                while (connectorIter.hasNext())
                {
                    Connector connector = (Connector)connectorIter.next();
                    String connectorType = connector.GetType();

                    if(connectorType.equalsIgnoreCase(EA_CONNECTOR_TYPE_INHERITANCE))
                    {
                        // The target class is process only if the association is navigable to it.
                        boolean isNavigableToTarget = connector.GetClientEnd().GetNavigable().equalsIgnoreCase(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
                        int id = connector.GetClientID();
                        if (id == aixmTimeslice.GetElementID()) id = connector.GetSupplierID();
                        Element associatedElement = EAConnection.getInstance().getRepository().GetElementByID(id);

                        EAEventManager.getInstance().fireEAEvent(this,"Detected an inheritance of "+elementName+"...");
                        if (associatedElement.GetName().equalsIgnoreCase(elementName) && associatedElement.GetStereotype().equalsIgnoreCase(AIXM_51_STEREOTYPE_EXTENSION))
                        {
                            EAEventManager.getInstance().fireEAEvent(this,"Checking properties and associations of "+associatedElement.GetName()+"/"+associatedElement.GetStereotype()+" for ALL_FEATURE mode.");
                            org.w3c.dom.Element propertyGroupWithElementsForAllFeatures = createFeaturePropertyGroup(associatedElement);
                            NodeList childNodes = propertyGroupWithElementsForAllFeatures.getChildNodes();
                            for (int x=0;x<childNodes.getLength();x++)
                            {
                                NodeList childSequenceElement = childNodes.item(x).getChildNodes();
                                for (int e=0;e<childSequenceElement.getLength();e++)
                                {
                                    org.w3c.dom.Node childElement = childSequenceElement.item(e);
                                    inheritedElements.add(childElement);
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": "+elementName+" was not found (using GUID), as a result the ALL_FEATURES mode may be ignored or generate partial results");
            }
        }
        skipSubstitutionGroup = false;
        return inheritedElements;
    }
    /**
     * 
     * @param aPackage
     */
    protected void processPackage(Package aPackage)
    {
        EAEventManager.getInstance().fireEAEvent(this,"Processing package " + aPackage.GetName());
        for (Iterator elementIter = aPackage.GetElements().iterator();elementIter.hasNext();)
        {
            Element element = (Element)elementIter.next();

            if(element.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_FEATURE))
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
            else if (element.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_OBJECT))
            {
                    _root.appendChild(createObject(element));
                    _root.appendChild(createObjectType(element));
                    _root.appendChild(createObjectPropertyGroup(element));
                    _root.appendChild(createObjectPropertyType(element));
                    _root.appendChild(createAbstractObjectExtension(element));
            }
            else if (element.GetStereotypeEx().equals(AIXM_51_STEREOTYPE_CHOICE))
            {
                // Do nothing - the choices are processed directly when creating the "property groups"  
            }
            else
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Unknown stereotype for " + SparxUtilities.toString(element));
            }
        }
    }
    
	/**
	 * 
	 * @return
	 */
	protected org.w3c.dom.Element createRoot() {
	    org.w3c.dom.Element root = super.createRoot();

	    root.setAttribute(XSD_XML_NAMESPACE + ":" + _targetNamespacePrefix, _targetNamespace); 	    
	    root.setAttribute(XSD_XML_NAMESPACE, XSD_DEFAULT_TARGET_NAMESPACE);	       
		root.setAttribute(XSD_XML_NAMESPACE + ":" + XSD_DEFAULT_TARGET_NAMESPACE_PREFIX, XSD_DEFAULT_TARGET_NAMESPACE);
		
		root.setAttribute(XSD_XML_NAMESPACE + ":" + "gml", XSD_HTTP_GML_SCHEMA);
		
		root.setAttribute(XSD_XML_NAMESPACE + ":" + "xlink", XSD_HTTP_XLINK_SCHEMA);
		
		root.setAttribute("targetNamespace",_targetNamespace);
		
		root.setAttribute("elementFormDefault", _elementFormDefault);
		root.setAttribute("attributeFormDefault", _attributeFormDefault);
		root.setAttribute("version", _aixmCoreVersion);

		
		org.w3c.dom.Element XSDimport1 = _xmldoc.createElement(XSD_TAG_IMPORT);
		XSDimport1.setAttribute("namespace", XSD_HTTP_GML_SCHEMA);
		// OVA2015 AIXMSCR-1
		XSDimport1.setAttribute("schemaLocation", XSD_HTTP_GML_SCHEMA_LOCATION);
		
		org.w3c.dom.Element XSDimport2 = _xmldoc.createElement(XSD_TAG_IMPORT);
		XSDimport2.setAttribute("namespace", XSD_HTTP_XLINK_SCHEMA);
		// OVA2015 AIXMSCR-1
		XSDimport2.setAttribute("schemaLocation", XSD_HTTP_XLINK_SCHEMA_LOCATION);
		
		org.w3c.dom.Element XSDinclude1 = _xmldoc.createElement(XSD_TAG_INCLUDE);
		String coreAIXMDatatypesFileName = AIXM_51_DEFAULT_GENERATE_FILE_NAME_DATATYPES;
		try
		{
		    coreAIXMDatatypesFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the file name for AIXM Features. Using a default file name.");
		}
		XSDinclude1.setAttribute("schemaLocation", "./" + coreAIXMDatatypesFileName);
		
		org.w3c.dom.Element XSDinclude2 = _xmldoc.createElement(XSD_TAG_INCLUDE);
		XSDinclude2.setAttribute("schemaLocation","./AIXM_AbstractGML_ObjectTypes.xsd");
		
		root.appendChild(XSDimport1);
		root.appendChild(XSDimport2);
		root.appendChild(XSDinclude1);
		root.appendChild(XSDinclude2);
		
		return root;
	}
    
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeature(Element feature)
	{
	    String name = feature.GetName();	  
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);

        boolean _includeDeprecation = feature.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
	    
	    if(feature.GetAbstract().equals("1"))
	    {
	        name = "Abstract" + name;
	        element.setAttribute("abstract","true");
	    }
	    element.setAttribute("name",name);
	    element.setAttribute("type", getNamespacePrefixForElement(feature) + ":" + name + "Type");
	    
	    Element UMLParent = getUMLParent(feature);
	    if(UMLParent != null)
	    {
	        String parentName = UMLParent.GetName();
		    if(UMLParent.GetAbstract().equals("1"))
		    {
		        parentName = "Abstract" + parentName;
		    }
	        element.setAttribute("substitutionGroup",getNamespacePrefixForElement(UMLParent) + ":" + parentName);
	    }
	    else
	        element.setAttribute("substitutionGroup","aixm" + ":" + "AbstractAIXMFeature");

        //include annotation
        if(_includeDocumentation || _includeDeprecation ){
            element.appendChild(createAnnotation(feature, _includeDeprecation, feature.GetNotes()));
        }

	    return element;
	}

    /**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeatureType(Element feature)
	{
	    String name = feature.GetName();
	    
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    if(feature.GetAbstract().equals("1"))
	    {
	        name = "Abstract" + name;
	        complexType.setAttribute("abstract","true");
	    }
	    
	    complexType.setAttribute("name", name + "Type");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    
	    // by default, the complexType extends AbstractAIXMFeatureType
	    String extensionBaseName = "AbstractAIXMFeatureType";
	    
	    Element umlParent = getUMLParent(feature);
	    if(umlParent != null)
	    {
	        if(umlParent.GetAbstract().equals("1"))
	        {
	            extensionBaseName = "Abstract" + umlParent.GetName() + "Type";
	        }
	        else
	        {
	            extensionBaseName = umlParent.GetName() + "Type";
	        }
	        extension.setAttribute("base", getNamespacePrefixForElement(umlParent) + ":" + extensionBaseName);
	    }
	    else
	    {
	        extension.setAttribute("base", "aixm" + ":" + extensionBaseName);
	    }
	    
	    
	    // For non abstract classes
	    if(!feature.GetAbstract().equals("1"))
	    {
		    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
		    
		    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
		    element.setAttribute("name","timeSlice");
		    element.setAttribute("type",getNamespacePrefixForElement(feature) + ":" + feature.GetName() + "TimeSlicePropertyType");
		    element.setAttribute("maxOccurs", "unbounded");
		    
		    sequence.appendChild(element);
		    extension.appendChild(sequence);
	    }
	    
	    complexContent.appendChild(extension);
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeatureTimeSlicePropertyType(Element feature)
	{
	    String name = feature.GetName();
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    complexType.setAttribute("name", name + "TimeSlicePropertyType");
	    
	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
	    
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    element.setAttribute("ref",getNamespacePrefixForElement(feature) + ":" + name + "TimeSlice");
	    
	    sequence.appendChild(element);
	    complexType.appendChild(sequence);
	    
	    org.w3c.dom.Element attributeGroup = _xmldoc.createElement(XSD_TAG_ATTRIBUTE_GROUP);
	    attributeGroup.setAttribute("ref","gml:OwnershipAttributeGroup");
	    
	    complexType.appendChild(attributeGroup);
	    
	    return complexType;
	}
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeatureTimeSlice(Element feature)
	{
	    String name = feature.GetName();
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    element.setAttribute("name", name + "TimeSlice");
	    element.setAttribute("type", getNamespacePrefixForElement(feature) + ":" + name + "TimeSliceType");
	    element.setAttribute("substitutionGroup", "gml:AbstractTimeSlice");
	    return element;
	}
	
	
	/**
	 *
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeatureTimeSliceType(Element feature)
	{
	    String name = feature.GetName();
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    complexType.setAttribute("name", name + "TimeSliceType");

	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);

	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    extension.setAttribute("base", "aixm" + ":" + "AbstractAIXMTimeSliceType");

	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);

	    org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
	    group.setAttribute("ref", getNamespacePrefixForElement(feature) + ":" + name + "PropertyGroup");

	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    element.setAttribute("name","extension");
	    element.setAttribute("minOccurs","0");
	    element.setAttribute("maxOccurs","unbounded");

	    org.w3c.dom.Element complexType2 = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);

	    Element UMLParent = getUMLParent(feature);

	    if(UMLParent == null)
	    {
	        org.w3c.dom.Element sequence2 = _xmldoc.createElement(XSD_TAG_SEQUENCE);
	        org.w3c.dom.Element element2 = _xmldoc.createElement(XSD_TAG_ELEMENT);
	        element2.setAttribute("ref",getNamespacePrefixForElement(feature) + ":" + "Abstract" + name + "Extension");
	        sequence2.appendChild(element2);
	        complexType2.appendChild(sequence2);

	    }
	    else
	    {
	        org.w3c.dom.Element choice = _xmldoc.createElement(XSD_TAG_CHOICE);


            Element choiceElement = feature;

            while(choiceElement != null) {
                org.w3c.dom.Element element2 = _xmldoc.createElement(XSD_TAG_ELEMENT);
                element2.setAttribute("ref", getNamespacePrefixForElement(choiceElement) + ":" + "Abstract" + choiceElement.GetName() + "Extension");
                choice.appendChild(element2);
                choiceElement = getUMLParent(choiceElement);
            }
	        complexType2.appendChild(choice);
	    }

        org.w3c.dom.Element attributeGroup = _xmldoc.createElement(XSD_TAG_ATTRIBUTE_GROUP);
        attributeGroup.setAttribute("ref","gml:OwnershipAttributeGroup");
        complexType2.appendChild(attributeGroup);

	    element.appendChild(complexType2);

	    sequence.appendChild(group);
	    sequence.appendChild(element);

	    extension.appendChild(sequence);

	    complexContent.appendChild(extension);

	    complexType.appendChild(complexContent);

	    return complexType;
	}
	
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeaturePropertyType(Element feature)
	{
	    String name = feature.GetName();
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    complexType.setAttribute("name", name + "PropertyType");
	    	    
	    org.w3c.dom.Element attributeGroup1 = _xmldoc.createElement(XSD_TAG_ATTRIBUTE_GROUP);
	    attributeGroup1.setAttribute("ref","gml:OwnershipAttributeGroup");
	    	    
	    org.w3c.dom.Element attributeGroup2 = _xmldoc.createElement(XSD_TAG_ATTRIBUTE_GROUP);
	    attributeGroup2.setAttribute("ref","gml:AssociationAttributeGroup");
	    
	    complexType.appendChild(attributeGroup1);
	    complexType.appendChild(attributeGroup2);
	    
	    return complexType;
	}
	
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createFeaturePropertyGroup(Element feature)
	{
	    String name = feature.GetName();
	    Element UMLParent = getUMLParent(feature);
	    
	    org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
	    group.setAttribute("name", name + "PropertyGroup");

	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);

	    // include reference to the parent property group of the parent, if any
        if (!autoInheritance && UMLParent != null && !inheritsFromISO19107(feature) && feature.GetStereotype().equals("feature"))
	    {
	        org.w3c.dom.Element parentGroup = _xmldoc.createElement(XSD_TAG_GROUP);
	        parentGroup.setAttribute("ref", getNamespacePrefixForElement(UMLParent) + ":" + UMLParent.GetName() + "PropertyGroup");
	        parentGroup.setAttribute("minOccurs", "0");
	        sequence.appendChild(parentGroup);
	    }

        //AIXMSCR-13: add inherited properties and associations if autoInheritance enabled
        Vector<org.w3c.dom.Node> autoInheritanceElements = null;
        if (feature.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_OBJECT)) autoInheritanceElements = elementsForAllObjects;
        if (feature.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_FEATURE)) autoInheritanceElements = elementsForAllTimeslices;
        if (autoInheritance && autoInheritanceElements!=null && autoInheritanceElements.size()!=0)for (int e=0;e<autoInheritanceElements.size();e++)
        {
            Node autoInheritanceElement = autoInheritanceElements.get(e);
            sequence.appendChild(autoInheritanceElement.cloneNode(true));
        }

        // include the UML properties
        //AIXMSCR-13: ignore properties if autoInheritance enabled
	    if (!autoInheritance) for (Iterator attributeIter = feature.GetAttributes().iterator();attributeIter.hasNext();)
	    {
	        Attribute umlAttribute = (Attribute) attributeIter.next();
	        	        
	        checkAttribute(feature, umlAttribute);
	        
	        org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	        element.setAttribute("name",umlAttribute.GetName());
	        element.setAttribute("nillable","true");
	        element.setAttribute("minOccurs","0");

            boolean  _includeDeprecation = umlAttribute.GetStereotype().equals(AIXM_51_STEREOTYPE_DEPRECATED);

            //include annotation
            if(_includeDocumentation || _includeDeprecation ){
                element.appendChild(createAnnotation(umlAttribute, _includeDeprecation, umlAttribute.GetNotes()));
            }

	        //Add type
            Element umlDatatype = null;
            try {
                umlDatatype = EAConnection.getInstance().getElementByID(umlAttribute.GetClassifierID());
            } catch (Exception e) {
                // Do nothing
            }
            if (umlDatatype != null) 
            {
                element.setAttribute("type",getNamespacePrefixForElement(umlDatatype) + ":" + umlDatatype.GetName());  
    	        sequence.appendChild(element);
            }
            else 
            {
                // Do nothing - the error message is already raised by method checkAttribute                 
            }            	        
	    }

        // Process associations
        //AIXMSCR-13 : ignore associations if autoInheritance enabled
        if (!autoInheritance) for (Iterator connectorIter = feature.GetConnectors().iterator();connectorIter.hasNext();)
	    {
	        Connector connector = (Connector) connectorIter.next();
	        String connectorType = connector.GetType();

	        if(connectorType.equals(EA_CONNECTOR_TYPE_ASSOCIATION) || connectorType.equals(EA_CONNECTOR_TYPE_AGGREGATION))
	        {	            
	            // The target class is process only if the association is navigable to it.
	            boolean isNavigableToTarget = connector.GetClientEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	            int id = connector.GetClientID();
	            String roleName = connector.GetClientEnd().GetRole();           
	            String roleNotes = connector.GetClientEnd().GetRoleNote();
	            String sparxCardinality = connector.GetClientEnd().GetCardinality();
	            
	            // Note: depending on the way the UML association was modelled, what is called the target class in the comment above can be either the "client" or "supplier" of the connector.
	            // The purpose of this piece of code is to make sure the right target association is correctly processed.                                
	            if (id == feature.GetElementID())
	            {
	                id = connector.GetSupplierID();
	                isNavigableToTarget = connector.GetSupplierEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	                roleName = connector.GetSupplierEnd().GetRole();
	                roleNotes = connector.GetSupplierEnd().GetRoleNote();
	                sparxCardinality = connector.GetSupplierEnd().GetCardinality();

	            }
	            Element associatedElement = EAConnection.getInstance().getRepository().GetElementByID(id);
	            
	            // Remark: the association to class +annotation [Note] is not processed in this loop. The property annotation is always included in the XSD as the last item of the property group.
	            if(isNavigableToTarget && !associatedElement.GetName().equals("Note"))
	            {
        	        org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
        	        element.setAttribute("nillable","true");        	        

        	        ////////////////////////////////////
        	        // Check cardinality
        	        // minOccurs is always set to 0 because everything has to be optional
        	        element.setAttribute("minOccurs","0");
        	        String upperCardinality = null;

                    try
        	        {
        	            upperCardinality = getUpperMultiplictyInXML(connector, associatedElement);
        	        }
        	        catch (Exception e)
        	        {
        	            // Use the default multiplicity 0..1                            
                    	EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The multiplicity of the following association is invalid or not specified: " + SparxUtilities.toString(connector) + ". The multiplicity 0..1 will be used by default.");
        	            upperCardinality = "1";
        	        }
        	        element.setAttribute("maxOccurs",upperCardinality);

                    ////////////////////////////////////

                    boolean _includeDeprecation = connector.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

                    //include annotation
        	        if(_includeDocumentation || _includeDeprecation)
                    {
                        org.w3c.dom.Node xsdAnnotation = null;
                        // OVA2015 AIXMSCR-5
                        //a few connectors also have notes

                        String connectorNotes = connector.GetNotes();
                        if (connectorNotes!=null && connectorNotes.length()!=0)xsdAnnotation = element.appendChild(createAnnotation(connector, _includeDeprecation, connectorNotes));
                        else if (roleNotes!=null && roleNotes.length()!=0)  xsdAnnotation = element.appendChild(createAnnotation(connector, _includeDeprecation, roleNotes));

                        if (upperCardinality != "unbounded"){
                            if(Integer.parseInt(upperCardinality) > 1) {
                                if(xsdAnnotation == null){
                                    xsdAnnotation = _xmldoc.createElement(XSD_TAG_ANNOTATION);
                                }
                                org.w3c.dom.Element xsdDocumentation = _xmldoc.createElement(XSD_TAG_DOCUMENTATION);
                                xsdDocumentation.setTextContent("WARNING: possible wrong multiplicity - see documentation");
                                xsdAnnotation.appendChild(xsdDocumentation);
                                element.appendChild(xsdAnnotation);
                            }

                        }
                    }


                    //modified by pulsar on 20190612 following issue from porosnie/20190304 "Incorrect generation of extension for association class towards feature"
                    String associationClassGUID = (String)_hashMapForAssociationClasses.get(connector.GetConnectorGUID());
	                // Case 1: association class
	                if(associationClassGUID != null)
	                {
	                    Element associationClass = EAConnection.getInstance().getRepository().GetElementByGuid(associationClassGUID);
	                    // chapter 4.9.1 Mapping Associations with Association Classes
	        	        element.setAttribute("name",roleName);
                        element.setAttribute("type",getNamespacePrefixForElement(associationClass) + ":" +  associationClass.GetName() + "PropertyType");
	        	        sequence.appendChild(element);
	                }
	                // case 2: no association class - the target class is a feature
	                else if(associatedElement.GetStereotypeEx().contains("feature"))
	                {
	                    // chapter 4.10 Mapping Relationships to Features
	        	        element.setAttribute("name",roleName);
	        	        element.setAttribute("type",getNamespacePrefixForElement(associatedElement) + ":" + associatedElement.GetName() + "PropertyType");
	        	        sequence.appendChild(element);
	                }
	                // case 3: no association class - the target class is an object
	                else if (associatedElement.GetStereotypeEx().contains("object"))
	                {
	                    // chapter 4.9 Mapping Relationships to Objects
	        	        element.setAttribute("name",roleName);
	        	        element.setAttribute("type",getNamespacePrefixForElement(associatedElement) + ":" + associatedElement.GetName() + "PropertyType");
	        	        sequence.appendChild(element);
	                }
	                // case 4: no association class - the target class is a choice

	                else if (associatedElement.GetStereotypeEx().contains("choice"))
	                {
	                    sequence.appendChild(createChoice(associatedElement,connector,roleName,roleNotes, upperCardinality));
                    
	                }        	        
	            }	            	            
	        }           
	    }
	    
	    /////////////////////////////////
	    // particular case: the element is an association class
        /////////////////////////////////
        // A numeric subtype qualifies the Type of the main element. For Class: 1 = Parameterised, 2 = Instantiated, 3 = Both, 0 = Neither, 17 = Association Class
        // If 17, because an Association Class has been created through the user interface, MiscData(3) will contain the ID of the related Association.
        // http://www.sparxsystems.com/uml_tool_guide/sdk_for_enterprise_architect/element2.htm
        /////////////////////////////////
        //AIXMSCR-13 : ignore linked classes if autoInheritance enabled
        if (!autoInheritance) if(feature.GetSubtype() == 17)
        {
            Connector connector = EAConnection.getInstance().getRepository().GetConnectorByID(Integer.parseInt(feature.MiscData(3)));
            if(connector != null)
            {
	            // The target class is process only if the association is navigable to it.
	            boolean isNavigableToTarget = connector.GetClientEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	            int id = connector.GetClientID();
	            String roleName = connector.GetClientEnd().GetRole();           
	            String roleNotes = connector.GetClientEnd().GetRoleNote();

	            // Note: depending on the way the UML association was modelled, what is called the target class in the comment above can be either the "client" or "supplier" of the connector.
	            // The purpose of this piece of code is to make sure the right target association is correctly processed.                                
	            if (!isNavigableToTarget)
	            {
	                id = connector.GetSupplierID();
	                isNavigableToTarget = connector.GetSupplierEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	                roleName = connector.GetSupplierEnd().GetRole();
	                roleNotes = connector.GetSupplierEnd().GetRoleNote();

	            }
	            Element associatedElement = EAConnection.getInstance().getRepository().GetElementByID(id);
	            
	            if(isNavigableToTarget)
	            {
	                org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	                element.setAttribute("name", "the" + associatedElement.GetName());
                    String noPrefixTypeName = associatedElement.GetName() + "PropertyType";
                    String typeName = getNamespacePrefixForElement(associatedElement) + ":" + noPrefixTypeName;
	                element.setAttribute("type", typeName);
	                element.setAttribute("minOccurs","0");
					// OVA2015 AIXMSCR-2
                    String lowerMultiplicity = "1";
                    try
                    {
                        lowerMultiplicity = SparxUtilities.getLowerMultiplicity(connector,associatedElement);
                    }
                    catch(Exception e)
                    {
                        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Cardinality cannot be retrieved for association " + associatedElement.GetName() + ", using default");
                    }
                    if (lowerMultiplicity.equalsIgnoreCase("0"))  element.setAttribute("nillable","true");
            		sequence.appendChild(element);

                    // OVA2015 AIXMSCR-2
                    //the related propertyType must also permit a nilreason attribute (will be done at the end, but remember it now)
                    typesMarkedForNilreason.put(noPrefixTypeName,noPrefixTypeName);
	            }
            }
        }

        //////////////////////////////
        // Add the annotation property at the end of the sequence.
        // This property is added only if the processed element 
        // - has no parent
        // - has a parent class belonging to the [ISO 19107] package
        // - is not part of the Notes package
        // In all other cases, the annotation property is inherited from the parent class.       
        //////////////////////////////
//        Element UMLParent = getUMLParent(feature);
         //AIXMSCR-13 ignore notes if autoInheritance enabled
        if (!autoInheritance && !SparxUtilities.belongsToPackage(feature,AIXM_51_GUID_PACKAGE_NOTES) && (UMLParent == null || SparxUtilities.belongsToPackage(UMLParent,AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY)))
        {
            org.w3c.dom.Element annotation = _xmldoc.createElement(XSD_TAG_ELEMENT);
	        annotation.setAttribute("name","annotation");
	        annotation.setAttribute("type","aixm" + ":" + "NotePropertyType");
	        annotation.setAttribute("nillable","true");
	        annotation.setAttribute("minOccurs","0");
	        annotation.setAttribute("maxOccurs","unbounded");
	        sequence.appendChild(annotation);
        }
	    group.appendChild(sequence);
	    
	    return group;
	}

    private static class MyNamespaceContext implements NamespaceContext {

        static private Map<String,String> namespaces = null;
        public String getNamespaceURI(String prefix) {
            if (namespaces==null)
            {
                namespaces = Collections.synchronizedMap(new HashMap<String,String>());
                namespaces.put("adrmsg", "http://www.eurocontrol.int/cfmu/b2b/ADRMessage");
                namespaces.put("adrext", "http://www.aixm.aero/schema/5.1/extensions/EUR/ADR");
                namespaces.put("gml", "http://www.opengis.net/gml/3.2");
                namespaces.put("aixm", "http://www.aixm.aero/schema/5.1");
                namespaces.put("xlink", "http://www.w3.org/1999/xlink");
            }
            String uri = namespaces.get(prefix);
            return uri;
        }

        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }

    }

    // OVA2015 AIXMSCR-2
    /*
    private void addElementsForAllFeatures()
    {
        try
        {
           if (elementsForAllFeatures!=null && elementsForAllFeatures.size()!=0)
           {
               EAEventManager.getInstance().fireEAEvent(this,"Adding elements for all features...");

               XPath xPath = XPathFactory.newInstance().newXPath();
               xPath.setNamespaceContext(new MyNamespaceContext());

               org.w3c.dom.Element rootElement = _root.getOwnerDocument().getDocumentElement();
               if (rootElement==null) rootElement = _root;

               //find all property groups
               NodeList propertyGroups = (NodeList)xPath.evaluate("//group[contains(@name,'PropertyGroup')]",rootElement, XPathConstants.NODESET);
               for (int p=0;p<propertyGroups.getLength();p++)
               {
                   Node propertyGroup =  propertyGroups.item(p);
                   String propertyGroupName = propertyGroup.getAttributes().getNamedItem("name").getNodeValue();
                   String timesliceName = propertyGroupName.replace("PropertyGroup","TimeSliceType");
                   //check a feature timeslice exists for this property group
                   NodeList complexTypes = (NodeList)xPath.evaluate("//complexType[@name='"+timesliceName+"']",rootElement, XPathConstants.NODESET);
                   if (complexTypes.getLength()>=1)
                   {
                       //check a sequence exists inside this property group
                       NodeList sequences = (NodeList)xPath.evaluate("descendant::sequence",propertyGroup, XPathConstants.NODESET);
                       if (complexTypes.getLength()>=1)
                       {
                           Node sequence = sequences.item(0);
                           for (int e=0;e<elementsForAllFeatures.size();e++)
                           {
                               Node elementForAllFeatures = elementsForAllFeatures.get(e);
                               sequence.appendChild(elementForAllFeatures.cloneNode(true));
                           }
                       }
                       else
                       {
                           EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Sequence not found inside property group ["+propertyGroupName+"] - skipping the adding of elements for all features for this one");
                       }
                   }
                   else
                   {
                       String objectTypeName = propertyGroupName.replace("PropertyGroup","Type");
                       NodeList objectComplexTypes = (NodeList)xPath.evaluate("//complexType[@name='"+objectTypeName+"']",rootElement, XPathConstants.NODESET);
                       if (objectComplexTypes.getLength()>=1)
                       {
                           //ok it is an object, no timeslice here, and no inheritance needed
                       }
                       else
                       {
                           EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": (ComplexType ["+timesliceName+"] not found - assuming that the PropertyGroup ["+propertyGroupName+"] is an object, an exotic element or a feature without timeslice");
                       }
                   }
               }
               EAEventManager.getInstance().fireEAEvent(this,"Elements for all features have been added");
           }
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Unexpected problem while adding element for all features: " + e.getMessage() + ", the adding of 'all features' elements has been aborted and will be incomplete.");
        }
    }
    */

    // OVA2015 AIXMSCR-2
    private void addNilreasonAttributes()
    {
        EAEventManager.getInstance().fireEAEvent(this,"Adding nilreason attributes....");
        for (String typeMarkedForNilreason : typesMarkedForNilreason.keySet())
        {
            NodeList nodes = null;
            try
            {
                XPath xPath = XPathFactory.newInstance().newXPath();
                xPath.setNamespaceContext(new MyNamespaceContext());

                org.w3c.dom.Element rootElement = _root.getOwnerDocument().getDocumentElement();
                if (rootElement==null) rootElement = _root;
                nodes = (NodeList)xPath.evaluate("//complexType[@name='" + typeMarkedForNilreason + "']",rootElement, XPathConstants.NODESET);
            }
            catch(Exception e)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Unexpected problem while adding nilreason attribute for ["+typeMarkedForNilreason+"] (xpath issue, " + e.getMessage() + ").");
            }
            if (nodes==null || nodes.getLength()==0)
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Unexpected problem while adding nilreason attribute for ["+typeMarkedForNilreason+"] (node not present in results, no need to add nilreason).");
            }
            else
            {
                if (nodes.getLength()>1)
                {
                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Unexpected problem while adding nilreason attribute for ["+typeMarkedForNilreason+"] (multiple nodes found, adding nilreason to all of them).");
                }
                for (int i=0;i<nodes.getLength();i++)
                {
                    org.w3c.dom.Element propertyType = (org.w3c.dom.Element) nodes.item(i);
                    org.w3c.dom.Element nilreasonAttribute = _xmldoc.createElement(XSD_TAG_ATTRIBUTE);
                    nilreasonAttribute.setAttribute("name", "nilReason");
                    nilreasonAttribute.setAttribute("type", "gml:NilReasonEnumeration");
                    propertyType.appendChild(nilreasonAttribute);
                }
            }
        }
        EAEventManager.getInstance().fireEAEvent(this,"nilreason attributes have been added");
    }


        /**
         *
         * @param associatedElement
         * @param connector
         * @param roleName
         * @param roleNotes
         * @return
         */
	protected org.w3c.dom.Element createChoice(Element associatedElement, Connector connector, String roleName, String roleNotes, String upperCardinality)
	{
        // chapter 4.8 Mapping Choices
        org.w3c.dom.Element choice = _xmldoc.createElement(XSD_TAG_CHOICE);

       // boolean _includeDeprecation =  connector.GetSupplierEnd().GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);



        boolean _includeDeprecation =  associatedElement.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
        boolean _includeConnectorsDeprecation =  connector.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

        if(_includeDocumentation || _includeDeprecation ){

            choice.appendChild(createAnnotation(associatedElement, _includeDeprecation, associatedElement.GetNotes()));
            //choice.appendChild(createAnnotation(connector, _includeConnectorsDeprecation, roleNotes));
        }

        for(Iterator choiceConnectorIter = associatedElement.GetConnectors().iterator();choiceConnectorIter.hasNext();)
        {

            Connector choiceConnector = (Connector)choiceConnectorIter.next();
            if (choiceConnector.GetConnectorID() != connector.GetConnectorID())
            {
	            int idChoiceElement = choiceConnector.GetClientID();     
	            String roleChoiceElement = choiceConnector.GetClientEnd().GetRole();
	            String roleNotesChoiceElement = choiceConnector.GetClientEnd().GetRoleNote();
	            boolean isNavigableToChoiceTarget = choiceConnector.GetClientEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	            // Note: depending on the way the UML association was modelled, what is called the target class in the comment above can be either the "client" or "supplier" of the connector.
	            // The purpose of this piece of code is to make sure the right target association is correctly processed.                                
	            if (idChoiceElement == associatedElement.GetElementID())
	            {
	                idChoiceElement = choiceConnector.GetSupplierID();
	                roleChoiceElement = choiceConnector.GetSupplierEnd().GetRole();
	                roleNotesChoiceElement = choiceConnector.GetSupplierEnd().GetRoleNote();
	                isNavigableToChoiceTarget = choiceConnector.GetSupplierEnd().GetNavigable().equals(EA_CONNECTOR_NAVIGABILITY_NAVIGABLE);
	            }
	            
	            if(isNavigableToChoiceTarget)
	            {	               
	                Element choiceElement = EAConnection.getInstance().getRepository().GetElementByID(idChoiceElement);
            		
	                // A choice can point to another choice. For instance <<choice>>[FlightRoutingElementChoice] ---> <<choice>>[SignificantPoint] 
	                if(choiceElement.GetStereotypeEx().contains("choice"))
	                {
	                    choice.appendChild(createChoice(choiceElement,choiceConnector, roleChoiceElement,roleNotesChoiceElement, upperCardinality));
	                }
	                else
	                {
	                    org.w3c.dom.Element XSDelementForChoice = _xmldoc.createElement(XSD_TAG_ELEMENT);
	                    XSDelementForChoice.setAttribute("name", roleName + "_" + roleChoiceElement);
	                    XSDelementForChoice.setAttribute("type", getNamespacePrefixForElement(choiceElement) + ":" + choiceElement.GetName() + "PropertyType");
                        XSDelementForChoice.setAttribute("nillable","true");
                        XSDelementForChoice.setAttribute("minOccurs","0");

                        //OVA2015 AIXMSCR-4
                        if (!_choiceFixDisabled)
                        {
                            String upperMultiplicity = null;
                            try {
                                upperMultiplicity = SparxUtilities.getUpperMultiplicity(choiceConnector,choiceElement);
                            }
                            catch(Exception e)
                            {
                                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the upper multiplicity from choice association [" + roleChoiceElement + ">"+choiceElement.GetName()+"]. Using 'unbounded' by default.");
                            }
                            if (upperMultiplicity!=null)
                                XSDelementForChoice.setAttribute("maxOccurs",upperCardinality);
                            else
                                XSDelementForChoice.setAttribute("maxOccurs","unbounded");
                        }

                        _includeDeprecation =  choiceConnector.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

                        if(_includeDocumentation || _includeDeprecation || _includeConnectorsDeprecation ){

                            if(_includeDeprecation)
                                XSDelementForChoice.appendChild(createAnnotation(choiceConnector, _includeDeprecation, choiceConnector.GetNotes()));
                            else
                            XSDelementForChoice.appendChild(createAnnotation(connector, _includeConnectorsDeprecation, connector.GetNotes()));
                        }


	                    choice.appendChild(XSDelementForChoice);
	                }
	            }
            }
        }
        
        return choice;
	}
	
	/**
	 * 
	 * @param feature
	 * @return
	 */
	protected org.w3c.dom.Element createAbstractFeatureExtension(Element feature)
	{
	    String name = feature.GetName();
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);    
	    element.setAttribute("name", "Abstract" + name + "Extension");	    
	    element.setAttribute("type", "aixm" + ":" + "AbstractExtensionType");
	    element.setAttribute("abstract", "true");
	    element.setAttribute("substitutionGroup", "aixm" + ":" + "AbstractExtension");
	    
	    return element;
	}
	
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected org.w3c.dom.Element createObject(Element object)
	{	    
	    // check 4.7 Mapping Objects
	    // "AIXM objects are encoded as GML objects. For the most part, the XML schema entities are created in the same way as for Features, following the object-property model."
	    String name = object.GetName();	  
	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    
	    if(object.GetAbstract().equals("1"))
	    {
	        name = "Abstract" + name;
	        element.setAttribute("abstract","true");
	    }
	    element.setAttribute("name",name);
	    element.setAttribute("type", getNamespacePrefixForElement(object) + ":" + name + "Type");	    
	    
	    Element UMLParent = getUMLParent(object);
	    if(UMLParent != null)
	    {
	        String parentName = UMLParent.GetName();
	        
	        //Removes prefix [GM_] for ISO 19107 classes such as GM_Point
	        if(SparxUtilities.belongsToPackage(UMLParent, AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY) && parentName.startsWith("GM_"))
	            parentName = parentName.substring(3);
	        
		    if(UMLParent.GetAbstract().equals("1"))
		    {
		        parentName = "Abstract" + parentName;
		    }
	        element.setAttribute("substitutionGroup",getNamespacePrefixForElement(UMLParent) + ":" + parentName);
	    }

        boolean _includeDeprecation =  object.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

        //include annotation
        if(_includeDocumentation || _includeDeprecation ){
            element.appendChild(createAnnotation(object, _includeDeprecation, object.GetNotes()));
        }

	    return element;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected org.w3c.dom.Element createObjectType(Element object)
	{
	    String name = object.GetName();
	    String objectTypeName = new String(name);
	    if(object.GetAbstract().equals("1"))
	        objectTypeName = "Abstract" + objectTypeName;
	        
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);	    
	    complexType.setAttribute("name", objectTypeName + "Type");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    
	    Element UMLParent = getUMLParent(object);
	    if(UMLParent != null)
	    {
	        String parentName = UMLParent.GetName();
		    if(UMLParent.GetAbstract().equals("1"))
		    {
		        parentName = "Abstract" + parentName;
		    }
	        
	        //Removes prefix [GM_] for ISO 19107 classes such as GM_Point
	        if(SparxUtilities.belongsToPackage(UMLParent, AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY) && parentName.startsWith("GM_"))
	            parentName = parentName.substring(3);
		    
	        extension.setAttribute("base", getNamespacePrefixForElement(UMLParent) + ":" + parentName + "Type");
	    }
	    else
	    {
	        extension.setAttribute("base", "aixm" + ":" + "AbstractAIXMObjectType");
	    }
	    
	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);


	    if(UMLParent != null)
	    {
	        String parentName = UMLParent.GetName();
	        /////////////////
	        if(!inheritsFromISO19107(object))//!EAUtil.belongsToPackage(UMLParent, AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY))
	        {
			    org.w3c.dom.Element groupA = _xmldoc.createElement(XSD_TAG_GROUP);
			    groupA.setAttribute("ref",getNamespacePrefixForElement(UMLParent) + ":" + parentName + "PropertyGroup");
			    sequence.appendChild(groupA);
	        }
	    }
	        /////////////////

	    if(!object.GetAbstract().equals("1"))
	    {
            org.w3c.dom.Element group = _xmldoc.createElement(XSD_TAG_GROUP);
		    group.setAttribute("ref",getNamespacePrefixForElement(object) + ":" + name + "PropertyGroup");
		    sequence.appendChild(group);

            org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
			/*
			KEEP XML 5.1.1 INVALID AND MANUALLY CORRECTED - 5.2 will be correct (email Eddy 20181218)
            String extensionName = (UMLParent != null? UMLParent.GetName() : name) + "Extension";
            element.setAttribute("name", extensionName);
			*/
			element.setAttribute("name","extension");
            element.setAttribute("minOccurs","0");
            element.setAttribute("maxOccurs","unbounded");

            org.w3c.dom.Element complexType2 = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);

            org.w3c.dom.Element choice = _xmldoc.createElement(XSD_TAG_CHOICE);
            complexType2.appendChild(choice);

            if(UMLParent != null && !inheritsFromISO19107(object))
            {
                String parentName = UMLParent.GetName();
                org.w3c.dom.Element elementA = _xmldoc.createElement(XSD_TAG_ELEMENT);
                elementA.setAttribute("ref",getNamespacePrefixForElement(UMLParent) + ":" + "Abstract" + parentName + "Extension");
                choice.appendChild(elementA);
            }

            org.w3c.dom.Element elementB = _xmldoc.createElement(XSD_TAG_ELEMENT);
            elementB.setAttribute("ref",getNamespacePrefixForElement(object) + ":" + "Abstract" + name + "Extension");
            choice.appendChild(elementB);

            org.w3c.dom.Element attributeGroup = _xmldoc.createElement(XSD_TAG_ATTRIBUTE_GROUP);
            attributeGroup.setAttribute("ref", "gml:OwnershipAttributeGroup");
            complexType2.appendChild(attributeGroup);


            element.appendChild(complexType2);

            sequence.appendChild(element);
        }

	    extension.appendChild(sequence);
	    
	    complexContent.appendChild(extension);
	    
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected org.w3c.dom.Element createObjectPropertyType(Element object)
	{
	    String name = object.GetName();
	    org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
	    complexType.setAttribute("name", name + "PropertyType");
	    
	    org.w3c.dom.Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);
	    
	    org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
	    extension.setAttribute("base", "aixm" + ":" + "AbstractAIXMPropertyType");
	    
	    org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);
	    
	    String elementName = name;
	    if(object.GetAbstract().equals("1"))
	        elementName = "Abstract" + elementName;

	    org.w3c.dom.Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
	    element.setAttribute("ref",getNamespacePrefixForElement(object) + ":" + elementName);
	    
	    sequence.appendChild(element);
	    extension.appendChild(sequence);
	    
	    complexContent.appendChild(extension);
    	    
	    complexType.appendChild(complexContent);
	    
	    return complexType;
	}
	
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected org.w3c.dom.Element createObjectPropertyGroup(Element object)
	{	    
	    // check 4.7 Mapping Objects
	    // "AIXM objects are encoded as GML objects. For the most part, the XML schema entities are created in the same way as for Features, following the object-property model."  
	    return createFeaturePropertyGroup(object);
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected org.w3c.dom.Element createAbstractObjectExtension(Element object)
	{
	    // check 4.7 Mapping Objects
	    // "AIXM objects are encoded as GML objects. For the most part, the XML schema entities are created in the same way as for Features, following the object-property model."  
	    return createAbstractFeatureExtension(object);
	}
	
	
	/**
	 * 
	 * @param
	 */
	protected void checkElement(Element element)
	{
	    boolean requiresUpdate = false; 
	    
	    if (element.GetName().contains(" "))
	    {
	        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The name of " + SparxUtilities.toString(element) + " contains a space character.");
	        element.SetName(element.GetName().replaceAll(" ",""));
	        requiresUpdate = true; 
	    }
	    
	    if(!element.GetStereotype().equals(AIXM_51_STEREOTYPE_COLLECTION_MEMBER_CHOICE) && !element.GetStereotype().toLowerCase().equals(element.GetStereotype()))
	    {
	        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The stereotype of " + SparxUtilities.toString(element) + " contains letters in Upper case.");
	        element.SetStereotype(element.GetStereotype().toLowerCase());
	        requiresUpdate = true; 
	    }
	    
	    // force the update in the SparxEA UML model
        if(_autoCorrection && requiresUpdate)
            element.Update();
	}
	
	/**
	 * 
	 * @param
	 */
	protected void checkAttribute(Element aClass, Attribute attribute)
	{
	    boolean requiresUpdate = false; 
	    
	    // Check name
	    if (attribute.GetName().contains(" "))
	    {
	        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The name of " + SparxUtilities.toString(attribute) + " contains a space character.");
	        attribute.SetName(attribute.GetName().replaceAll(" ",""));
	        requiresUpdate = true;
	    }
	    
        //Check datatype
        Element umlDatatype = null;
        try {
            umlDatatype = EAConnection.getInstance().getElementByID(attribute.GetClassifierID());
        } catch (Exception e) {
            // Do nothing
        }
        if (umlDatatype == null) 
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The property [" + aClass.GetName() + "]." + attribute.GetName() + " is not typed properly in UML.");
            umlDatatype = getDatatypeByName(attribute.GetType());
            if(umlDatatype != null)
            {
                attribute.SetClassifierID(umlDatatype.GetElementID());
                requiresUpdate = true;
            }
            else
            {
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": No valid datatype could be found for [" + aClass.GetName() + "]." + attribute.GetName());
            }
        }

	    
	    // force the update in the SparxEA UML model
        if(_autoCorrection && requiresUpdate)
            attribute.Update();
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	protected Element getDatatypeByName(String datatypeName)
	{	    
	    return (Element)_hashMapForDatatypes.get(datatypeName);
	}
	
    
    /**
     * 
     * @param connector
     * @param targetElement
     * @return
     */
    public String getUpperMultiplictyInXML(Connector connector, Element targetElement) throws Exception
    {
        String upperMultiplicty = SparxUtilities.getUpperMultiplicity(connector, targetElement);
        if(upperMultiplicty.equals("*") || upperMultiplicty.equals("n"))
        {
            return "unbounded";
        }
        return upperMultiplicty;
    }
	
	/**
     * 
     */
    protected String getNamespacePrefixForElement(Element element) {
        try
        {
            if(SparxUtilities.belongsToPackage(element,_targetPackage.GetPackageGUID()))
                return _targetNamespacePrefix;
            if(SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_XSD_SCHEMA_DATATYPES))
                return XSD_DEFAULT_TARGET_NAMESPACE_PREFIX;
            if(SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY))
                return "gml";
            if(SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_AIXM_FEATURES) || SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_AIXM_ABSTRACT_FEATURES))
                return SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_FEATURES).GetElement());

            Package aPackage = EAConnection.getInstance().getRepository().GetPackageByID(element.GetPackageID());
            if(aPackage != null)
            {
                if (SparxUtilities.isTaggedValuePopulated(XSD_TAGGED_VALUE_TARGET_NAMESPACE,aPackage.GetElement()))
                    return SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX,aPackage.GetElement());
            }
            return _targetNamespacePrefix;

        }
        catch(Exception e)
        {
            return _targetNamespacePrefix;
        }
    }
    
    /**
     * 
     * @param element
     * @return
     */
    private boolean inheritsFromISO19107(Element element)
    {
        if(element == null)
            return false;
        boolean belongsToISO19107Package = SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_ISO_19107_GEOMETRY);
        return belongsToISO19107Package || inheritsFromISO19107(getUMLParent(element));
    }
}
