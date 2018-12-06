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
import java.util.HashMap;
import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractMappingRule;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;


/**
 * @author hlepori
 */
public class AIXM51_Datatypes_MappingRule extends AbstractMappingRule
{

    /**
     * AIXM properties
     */
    protected String _aixmCoreVersion;
    protected String _generateFileName;

    /**
     *
     */
    protected HashMap _hashMapForDatatypes = new HashMap();

    /**
     * @param targetPackage
     * @throws IOException
     */
    public AIXM51_Datatypes_MappingRule(Package targetPackage)
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
            else
                throw new Exception();
        }
        catch (Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + AIXM_TAGGED_VALUE_CORE_VERSION  + ". Using default value.");
        }
        try
        {
            if (SparxUtilities.isTaggedValuePopulated(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME,_targetPackage.GetElement()))
                _generateFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME,_targetPackage.GetElement());
            else
                throw new Exception();
        }
        catch (Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + AIXM_TAGGED_VALUE_GENERATE_FILE_NAME  + ". Using default value.");
        }
    }


    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.MappingRule#setDefaultValues()
     */
    protected void setDefaultValues() {
        _aixmCoreVersion = AIXM_51_DEFAULT_CORE_VERSION;
        _generateFileName = AIXM_51_DEFAULT_GENERATE_FILE_NAME_DATATYPES;
        _targetNamespace = AIXM_51_DEFAULT_TARGET_NAMESPACE;
        _targetNamespacePrefix = AIXM_51_DEFAULT_TARGET_NAMESPACE_PREFIX;
        _attributeFormDefault = XSD_DEFAULT_ATTRIBUTE_FORM;
        _elementFormDefault = XSD_DEFAULT_ELEMENT_FORM;
    }

    /**
     *
     *
     */
    protected void buildHashMapForDatatypes()
    {
        Package datatypePackage = getTargetPackage();
        for(Iterator iter = datatypePackage.GetElements().iterator();iter.hasNext();)
        {
            Element datatype = (Element)iter.next();
            checkElement(datatype);
            _hashMapForDatatypes.put(datatype.GetName(),datatype);
        }
    }

    /**
     *
     */
    public void generateXSD() throws IOException {

        buildHashMapForDatatypes();

        org.w3c.dom.Element xsdRoot = this.createRoot();
        int i = 0;
        if(xsdRoot != null)
        {
            Package aixmDatatypes = getTargetPackage();
            EAEventManager.getInstance().fireEAEvent(this,"Processing package " + aixmDatatypes.GetName());
            for (Iterator iter = aixmDatatypes.GetElements().iterator();iter.hasNext();)
            {
                Element UMLdatatype = (Element)iter.next();
                String stereotype = UMLdatatype.GetStereotypeEx();

                if(stereotype.contains(AIXM_51_STEREOTYPE_CODELIST))
                    xsdRoot.appendChild(mapsUMLCodelist(UMLdatatype));
                else if (stereotype.contains(AIXM_51_STEREOTYPE_DATATYPE))
                    xsdRoot.appendChild(mapsUMLDatatype(UMLdatatype));
                else
                    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The datatype " + SparxUtilities.toString(UMLdatatype) + " has either no stereotype or a wrong stereotype.");
            }

            _serializer.serialize(xsdRoot);
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


        org.w3c.dom.Element XSDimport = _xmldoc.createElement(XSD_TAG_IMPORT);
        XSDimport.setAttribute("namespace", XSD_HTTP_GML_SCHEMA);
// OVA2015 AIXMSCR-1
        XSDimport.setAttribute("schemaLocation", XSD_HTTP_GML_SCHEMA_LOCATION);

        root.appendChild(XSDimport);

        return root;
    }


    /**
     * Maps a UML <<codelist>>
     * Refer to document "AIXM UML to AIXM XSD Mapping v1.1" - Chapter 4.11.1
     * @param umlCodeList
     * @return
     */
    protected org.w3c.dom.Element mapsUMLCodelist(Element umlCodeList)
    {
        ///////////////////
        // Create the top XSD simple type 
        ///////////////////
        // Create a simple type
        org.w3c.dom.Element xsdCodelist = _xmldoc.createElement(XSD_TAG_SIMPLE_TYPE);
        // set the name of the codelist
        xsdCodelist.setAttribute("name", umlCodeList.GetName());

        boolean _includeDeprecation = umlCodeList.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

        //include annotation
        if(_includeDocumentation || _includeDeprecation ){
            xsdCodelist.appendChild(createAnnotation(umlCodeList, _includeDeprecation, umlCodeList.GetNotes()));
        }

        ///////////////////
        // Create the union
        ///////////////////        
        org.w3c.dom.Element xsdUnion = _xmldoc.createElement(XSD_TAG_UNION);
        // Create simple type 1
        org.w3c.dom.Element simpleType1 = _xmldoc.createElement(XSD_TAG_SIMPLE_TYPE);
        // Create simple type 2
        org.w3c.dom.Element simpleType2 = _xmldoc.createElement(XSD_TAG_SIMPLE_TYPE);

        Element UMLParent = getUMLParent(umlCodeList);

        // Case 1: the codelist inherits from string or has no parent class 
        /////////////////////////////////
        if(UMLParent == null || UMLParent.GetElementGUID().equals(AIXM_51_GUID_CLASS_XSD_SCHEMA_STRING))
        {

            ///////////////////////
            // Create the enumeration => simpleType1
            ///////////////////////

            // Create the restriction base=xsd:string
            org.w3c.dom.Element restriction1 = _xmldoc.createElement(XSD_TAG_RESTRICTION);
            restriction1.setAttribute("base", "xsd:string");

            // Create the enumeration elements
            for (Iterator iter = umlCodeList.GetAttributes().iterator();iter.hasNext();)
            {
                // each UML atribute maps to an XML enumeration element - except the other value, added by default to all codelist
                org.sparx.Attribute attribute = (org.sparx.Attribute)iter.next();

                checkAttribute(umlCodeList, attribute);

                if(!attribute.GetName().equals("OTHER"))
                {
                    org.w3c.dom.Element myEnum = _xmldoc.createElement(XSD_TAG_ENUMERATION);
                    myEnum.setAttribute("value",attribute.GetName());

                    _includeDeprecation = attribute.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
                    //include annotation
                    if(_includeDocumentation || _includeDeprecation ){
                        myEnum.appendChild(createAnnotation(attribute, _includeDeprecation, attribute.GetNotes()));
                    }

                    restriction1.appendChild(myEnum);
                }
            }
            simpleType1.appendChild(restriction1);

            ///////////////////////
            // Create the OTHER value => simpleType2
            ///////////////////////

            // Create the restriction of base xsd:string
            org.w3c.dom.Element restriction2 = _xmldoc.createElement(XSD_TAG_RESTRICTION);
            restriction2.setAttribute("base", "xsd:string");
            // Create pattern
            org.w3c.dom.Element pattern = _xmldoc.createElement(XSD_TAG_PATTERN);
            pattern.setAttribute("value","OTHER(:(\\w|_){1,58})?");
            restriction2.appendChild(pattern);
            simpleType2.appendChild(restriction2);

        }

        // case 2 : the codelist has a parent class which is not [string]. It is then assumed that the parent class is another codelist.  
        /////////////////////////////////
        else
        {
            if(UMLParent.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_CODELIST))
            {
                ///////////////////////
                // Create the restriction => simpleType1
                ///////////////////////

                // Create the restriction
                org.w3c.dom.Element restriction1 = _xmldoc.createElement(XSD_TAG_RESTRICTION);
                restriction1.setAttribute("base", getNamespacePrefixForElement(UMLParent) + ":" + UMLParent.GetName());
                simpleType1.appendChild(restriction1);

                ///////////////////////
                // Create the new enumeration => simpleType2
                ///////////////////////
                org.w3c.dom.Element restriction2 = _xmldoc.createElement(XSD_TAG_RESTRICTION);
                restriction2.setAttribute("base", "xsd:string");

                for (Iterator iter = umlCodeList.GetAttributes().iterator();iter.hasNext();)
                {
                    // each UML atribute maps to an XML enumeration element - except the other value, added by default to all codelist
                    org.sparx.Attribute attribute = (org.sparx.Attribute)iter.next();

                    checkAttribute(umlCodeList, attribute);

                    if(!attribute.GetName().equals("OTHER"))
                    {
                        org.w3c.dom.Element myEnum = _xmldoc.createElement(XSD_TAG_ENUMERATION);
                        myEnum.setAttribute("value",attribute.GetName());

                        //include annotation
                        if(_includeDocumentation || _includeDeprecation ){
                            myEnum.appendChild(createAnnotation(attribute, _includeDeprecation, attribute.GetNotes()));
                        }

                    }
                }
                simpleType2.appendChild(restriction2);
                // Do not add the OTHER value since it is inherited  from the parent 
            }
        }

        xsdUnion.appendChild(simpleType1);
        xsdUnion.appendChild(simpleType2);
        xsdCodelist.appendChild(xsdUnion);

        return xsdCodelist;
    }

    /**
     * Maps a UML <<datatype>>
     *
     * @param umlDatatype
     * @return
     */
    protected org.w3c.dom.Element mapsUMLDatatype(Element umlDatatype)
    {
        // Case 1: the <<datatype>> is a [BaseType]
        // => Refer to document "AIXM UML to AIXM XSD Mapping v1.1" - Chapter 4.11.2 & Chapter 4.11.3
        ////////////////////////////////////////////////
        if(isBaseType(umlDatatype))
            return mapsUMLBaseType(umlDatatype);

        // Case 2: the <<datatype>> has a parent [BaseType] 
        // => Refer to document "AIXM UML to AIXM XSD Mapping v1.1" - Chapter 4.11.2 & Chapter 4.11.3
        ////////////////////////////////////////////////
        if(getUMLParent(umlDatatype)!= null && isBaseType(getUMLParent(umlDatatype)))
            return mapsUMLDatatypeWithBaseType(umlDatatype);

        // Case 3: <<datatype>> with no BaseType
        // => Refer to document "AIXM UML to AIXM XSD Mapping v1.1" - Chapter 4.11.4.1
        ////////////////////////////////////////////////
        if(getUMLParent(umlDatatype)!= null && !isBaseType(getUMLParent(umlDatatype)))
            return mapsUMLDatatypeWithNoBaseType(umlDatatype);

        // Case 4: <<datatype>> XHTMLType
        // => Refer to document "AIXM UML to AIXM XSD Mapping v1.1" - Chapter 4.11.4.2
        ////////////////////////////////////////////////
        if(umlDatatype.GetName().equals("XHTMLType"))
            return mapsXHTMLBaseType(umlDatatype);

        return null;
    }

    /**
     *
     * @param umlDatatype
     * @return
     */
    protected org.w3c.dom.Element mapsUMLBaseType(Element umlDatatype)
    {
        // Create a simple type
        org.w3c.dom.Element simpleType = _xmldoc.createElement(XSD_TAG_SIMPLE_TYPE);
        // Set the name of the simple type
        simpleType.setAttribute("name", umlDatatype.GetName());

        boolean _includeDeprecation = umlDatatype.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

        //include annotation
        if(_includeDocumentation || _includeDeprecation ){
            simpleType.appendChild(createAnnotation(umlDatatype, _includeDeprecation, umlDatatype.GetNotes()));
        }

        // Create restriction
        org.w3c.dom.Element restriction = _xmldoc.createElement(XSD_TAG_RESTRICTION);
        // Get the parent class 
        Element UMLparent = getUMLParent(umlDatatype);

        if(UMLparent == null)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The datatype " + SparxUtilities.toString(umlDatatype) + " has no parent class. Using [xsd:string] as default parent.");
            UMLparent = EAConnection.getInstance().getRepository().GetElementByGuid(AIXM_51_GUID_CLASS_XSD_SCHEMA_STRING);
        }

        String name = UMLparent.GetName();

        name = getNamespacePrefixForElement(UMLparent) + ":" + name;
        restriction.setAttribute("base", name);

        // process the attributes
        for (Iterator iter = umlDatatype.GetAttributes().iterator();iter.hasNext();)
        {
            org.sparx.Attribute attribute = (org.sparx.Attribute)iter.next();
            checkAttribute(umlDatatype, attribute);

            // Process the XSD facet
            if(attribute.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_XSD_FACET))
            {
                org.w3c.dom.Element facet = _xmldoc.createElement(attribute.GetName());
                facet.setAttribute("value", unescapeXML(attribute.GetDefault()));
                restriction.appendChild(facet);
            }
        }

        simpleType.appendChild(restriction);
        return simpleType;
    }

    /**
     *
     * @param umlDatatype
     * @return
     */
    protected org.w3c.dom.Element mapsUMLDatatypeWithBaseType(Element umlDatatype)
    {
        // Create a complex type
        org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
        // Set the name of the complex type
        complexType.setAttribute("name", umlDatatype.GetName());

        boolean _includeDeprecation = umlDatatype.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);
        //include annotation
        if(_includeDocumentation || _includeDeprecation ){
            complexType.appendChild(createAnnotation(umlDatatype, _includeDeprecation, umlDatatype.GetNotes()));
        }

        // Create simpleContent
        org.w3c.dom.Element simpleContent = _xmldoc.createElement(XSD_TAG_SIMPLE_CONTENT);

        // Create extension
        org.w3c.dom.Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
        extension.setAttribute("base", getNamespacePrefixForElement(getUMLParent(umlDatatype)) + ":" + getUMLParent(umlDatatype).GetName());

        // process the UMLattributes
        for (Iterator iter = umlDatatype.GetAttributes().iterator();iter.hasNext();)
        {
            org.sparx.Attribute umlAttribute = (org.sparx.Attribute)iter.next();
            checkAttribute(umlDatatype, umlAttribute);

            org.w3c.dom.Element xsdAttribute = _xmldoc.createElement(XSD_TAG_ATTRIBUTE);
            xsdAttribute.setAttribute("name",umlAttribute.GetName());

            _includeDeprecation = umlAttribute.GetStereotype().equals(AIXM_51_STEREOTYPE_DEPRECATED);

            if(_includeDocumentation || _includeDeprecation ){
                xsdAttribute.appendChild(createAnnotation(umlAttribute, _includeDeprecation, umlAttribute.GetNotes()));
            }

            String typeName = "";
            // get UML attibute type
            Element UMLDatatype = SparxUtilities.getUMLDatatype(umlAttribute);
            if(UMLDatatype != null)
                typeName = UMLDatatype.GetName();
            else
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The UML attribute " + umlDatatype.GetName() + "." + umlAttribute.GetName() + " is not typed correctly");

            if(umlAttribute.GetName().equals("nilReason"))
            {
                xsdAttribute.setAttribute("type","gml:" + typeName);
            }
            else if(umlAttribute.GetName().equals("uom"))
            {
                xsdAttribute.setAttribute("type",getNamespacePrefixForElement(UMLDatatype) + ":" + typeName);
                // xsdAttribute.setAttribute("use","optional");
            }
            else {
                xsdAttribute.setAttribute("type",umlAttribute.GetType());
            }

            extension.appendChild(xsdAttribute);
        }

        simpleContent.appendChild(extension);
        complexType.appendChild(simpleContent);

        return complexType;
    }


    /**
     *
     * @param umlDatatype
     * @return
     */
    protected org.w3c.dom.Element mapsUMLDatatypeWithNoBaseType(Element umlDatatype)
    {
        // the mapping is the same the one for the [BaseType]
        return mapsUMLBaseType(umlDatatype);
    }


    /**
     *
     * @param umlDatatype
     * @return
     */
    protected org.w3c.dom.Element mapsXHTMLBaseType(Element umlDatatype)
    {
        // Create a complex type
        org.w3c.dom.Element complexType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
        // Set the name of the complex type
        complexType.setAttribute("name", umlDatatype.GetName());//"TextXHTMLType");

        boolean _includeDeprecation = umlDatatype.GetStereotypeEx().contains(AIXM_51_STEREOTYPE_DEPRECATED);

        if(_includeDocumentation || _includeDeprecation ){
            complexType.appendChild(createAnnotation(umlDatatype, _includeDeprecation, umlDatatype.GetNotes()));
        }

        //create sequence
        org.w3c.dom.Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);

        org.w3c.dom.Element any = _xmldoc.createElement(XSD_TAG_ANY);
        any.setAttribute("namespace", XSD_HTTP_XHTML_SCHEMA);
        any.setAttribute("processContents", "skip");
        any.setAttribute("maxOccurs", "unbounded");

        sequence.appendChild(any);
        complexType.appendChild(sequence);

        // process the attributes
        for (Iterator iter = umlDatatype.GetAttributes().iterator();iter.hasNext();)
        {
            org.sparx.Attribute attribute = (org.sparx.Attribute)iter.next();
            checkAttribute(umlDatatype, attribute);

            org.w3c.dom.Element xsdAttribute = _xmldoc.createElement(XSD_TAG_ATTRIBUTE);
            xsdAttribute.setAttribute("name",attribute.GetName());

            _includeDeprecation = attribute.GetStereotype().equals(AIXM_51_STEREOTYPE_DEPRECATED);

            if(_includeDocumentation || _includeDeprecation ){
                xsdAttribute.appendChild(createAnnotation(attribute, _includeDeprecation, attribute.GetNotes()));
            }

            String typeName = "";
            // get UML attibute type
            Element UMLDatatype = SparxUtilities.getUMLDatatype(attribute);
            if(UMLDatatype != null)
                typeName = UMLDatatype.GetName();
            else
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The UML attribute " + umlDatatype.GetName() + "." + attribute.GetName() + " is not typed correctly");

            if(attribute.GetName().equals("nilReason"))
            {
                xsdAttribute.setAttribute("type","gml:" + typeName);
            }
            complexType.appendChild(xsdAttribute);
        }




        return complexType;
    }


    ///////////

    /**
     *
     */
    protected boolean isBaseType(Element umlDatatype)
    {
        return umlDatatype.GetName().endsWith("BaseType");
    }


    /**
     *
     * @param element
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

        if(!element.GetStereotype().toLowerCase().equals(element.GetStereotype()))
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
     * @param aClass, attribute
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
                EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": No valid datatype could be found for [" + aClass.GetName() + "]." + attribute.GetName());
            }
        }


        // force the update in the SparxEA UML model
        if(_autoCorrection && requiresUpdate)
            attribute.Update();
    }

    /**
     *
     * @param datatypeName
     * @return
     */
    private Element getDatatypeByName(String datatypeName)
    {
        return (Element)_hashMapForDatatypes.get(datatypeName);
    }

    /**
     *
     */
    protected Package getTargetPackage() {
        return _targetPackage;
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
            if(SparxUtilities.belongsToPackage(element,AIXM_51_GUID_PACKAGE_AIXM_DATATYPES))
                return SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES).GetElement());

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

}