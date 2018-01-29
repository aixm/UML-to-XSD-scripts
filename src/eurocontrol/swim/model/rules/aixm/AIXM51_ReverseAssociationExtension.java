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

package eurocontrol.swim.model.rules.aixm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import org.sparx.Package;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.util.constants.AIXMConstants;
import eurocontrol.swim.model.util.constants.AIXMExtensionsConstants;
import eurocontrol.swim.model.util.constants.XSDConstants;

/**
 * @author hlepori
 */
public class AIXM51_ReverseAssociationExtension extends AbstractRule implements XSDConstants, AIXMConstants, AIXMExtensionsConstants{



    private XMLSerializer _serializer;
    private OutputFormat _of;
    private Document _xmldoc;
    private FileOutputStream _fos;
    private HashMap _featureMap;
    private Element _aixmRoot;
    private HashMap _aixmFeaturePropertyTypeMap;



    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#getName()
     */
    public String getName() {
        return "[AIXM_Features.xsd] CREATION OF EXTENSION - Generate XSD to support Reverse Associations";
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
        initialise();
        generateExtension();
    }

    /**
     *
     */
    private void initialise() {
        try {
            _of = new OutputFormat("XML", "UTF-8", true);
            _xmldoc = new DocumentImpl();
            _fos = new FileOutputStream("./output/" + AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_FILE_NAME);
            _serializer = new XMLSerializer(_fos, _of);
            _serializer.asDOMSerializer();
            _featureMap = new HashMap();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *
     */
    private void generateExtension() {
        try {
            System.out.println("--- Processing file " + "./input/" + AIXM_51_DEFAULT_GENERATE_FILE_NAME_FEATURES + " ---");
            processInputFile();

            System.out.println("--- Generating schema ---");
            generateSchema();

            _fos.close();
            System.out.println("--- Done ---");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     *
     * @throws Exception
     */
    private void processInputFile() throws Exception {
        DOMParser parser = new DOMParser();
        parser.parse("./input/" + AIXM_51_DEFAULT_GENERATE_FILE_NAME_FEATURES);

        _aixmRoot = (Element) parser.getDocument().getElementsByTagName("schema").item(0);

        _aixmFeaturePropertyTypeMap = new HashMap();
        NodeList complexTypeList = _aixmRoot
                .getElementsByTagName(XSD_TAG_COMPLEX_TYPE);
        for (int i = 0; i < complexTypeList.getLength(); i++) {
            Element element = (Element) complexTypeList.item(i);
            String name = element.getAttribute("name");
            if (name.endsWith("PropertyType")
                    && !(name.endsWith("TimeSlicePropertyType")) && !(name.endsWith("PropertiesWithSchedulePropertyType"))) {
                NodeList propertyTypeElementList = element.getChildNodes();
                if (element.getElementsByTagName("attributeGroup").getLength() == 2) {
                    _aixmFeaturePropertyTypeMap.put(name, element);
                }
            }

        }

        // get all the PropertyGroup
        ArrayList propertyGroupList = getPropertyGroupList();

        //Check each property of the propertyGroup. If the property ends with "PropertyType", this is a complex property.
        processPropertyGroupList(propertyGroupList);

    }

    /**
     *
     * @return
     */
    private ArrayList getPropertyGroupList() {
        ArrayList list = new ArrayList();
        // Get all the PropertyGroup
        NodeList rootNodeList = _aixmRoot.getChildNodes();
        for (int i = 0; i < rootNodeList.getLength(); i++) {
            Node item = rootNodeList.item(i);
            if (item.getNodeName().equals("group"))
                list.add(item);
        }
        return list;
    }

    /**
     *
     * @param propertyGroupList
     */
    private void processPropertyGroupList(ArrayList propertyGroupList) {
        for (Iterator i = propertyGroupList.iterator(); i.hasNext();) {
            Element propertyGroup = (Element) i.next();
            String name = propertyGroup.getAttribute("name").replaceFirst(
                    "PropertyGroup", "");

            if (isAFeature(name))
            {
                //A property group contains only one child node: the <sequence>. The <sequence> contains the list of properties.
                Element sequence = (Element) propertyGroup.getElementsByTagName(
                        "sequence").item(0);

                NodeList specificPropertyList = sequence
                        .getElementsByTagName("element");
                NodeList inheritedPropertyList = sequence
                        .getElementsByTagName("group");

                boolean isClassOfAssociation = (inheritedPropertyList.getLength() >0) ;


                if (!isClassOfAssociation)
                {
                    for (int k = 0; k < specificPropertyList.getLength(); k++) {
                        Element property = (Element) specificPropertyList.item(k);
                        if (property.getAttribute("type").endsWith("PropertyType")) {
                            processComplexProperty(name, property.getAttribute("type"));
                        }
                    }
                }

                // in some cases, the group contains a reference to another group - Example:
			/* <group name="RunwayProtectAreaPropertyGroup">
			 		<sequence>
			 			<group ref="aixm:AirportHeliportProtectionAreaPropertyGroup" minOccurs="0"/>
			 			<element name="type" type="aixm:CodeRunwayProtectionAreaType" nillable="true" minOccurs="0"/>
			 			<element name="status" type="aixm:CodeStatusOperationsType" nillable="true" minOccurs="0"/>
			 			<element name="protectedRunwayDirection" type="aixm:RunwayDirectionPropertyType" nillable="true" minOccurs="0"/>
					</sequence>
				</group>*/

                else
                {
                    for (int k = 0; k<inheritedPropertyList.getLength();k++)
                    {
                        Element e = (Element)inheritedPropertyList.item(k);
                        Element refPropertyGroup = getPropertyGroupByName(e.getAttribute("ref").substring(5));
                        if (refPropertyGroup != null)
                        {
                            //A property group contains only one child node: the <sequence>. The <sequence> contains the list of properties.
                            sequence = (Element) propertyGroup.getElementsByTagName(
                                    "sequence").item(0);

                            specificPropertyList = sequence.getElementsByTagName("element");
                            for (int l = 0; l < specificPropertyList.getLength(); l++) {
                                Element property = (Element) specificPropertyList.item(l);
                                if (property.getAttribute("type").endsWith("PropertyType")) {
                                    processComplexProperty(name, property.getAttribute("type"));
                                }
                            }
                        }
                    }
                }


            }
        }
    }

    private boolean isAFeature(String name)
    {
        return _aixmFeaturePropertyTypeMap.keySet().contains(name + "PropertyType");
    }

    public Element getPropertyGroupByName(String name)
    {
        ArrayList propertyGroupList = getPropertyGroupList();
        for (Iterator it = propertyGroupList.iterator();it.hasNext();)
        {
            Element e = (Element)it.next();
            if (e.getAttribute("name").equals(name))
                return e;
        }
        return null;
    }

    /**
     *
     */
    private void processComplexProperty(String name, String propertyType) {
        //remove aixm: namespace
        String shortPropertyType = propertyType.substring(5);

        Element element = (Element) _aixmFeaturePropertyTypeMap
                .get(shortPropertyType);
        if (element != null) {
            String startFeature = name;
            String targetFeature = element.getAttribute("name").replaceAll(
                    "PropertyType", "");
            // Do not recreate self association
            if(!targetFeature.equals(startFeature))
                updateFeatureMap(targetFeature, startFeature);
        }
    }

    private void updateFeatureMap(String key, String value) {
        if (_featureMap.get(key) == null) {
            ArrayList list = new ArrayList();
            list.add(value);
            _featureMap.put(key, list);
        } else {
            ArrayList list = (ArrayList) _featureMap.get(key);
            if (!list.contains(value))
                list.add(value);
        }
    }


    /**
     *
     * @throws IOException
     */
    private void generateSchema() throws IOException {
        Element root = createRoot();

        Element e = null;
        Node n = null;

        // Create import
		// OVA2015 AIXMSCR-1
        root.appendChild(createImport(XSD_HTTP_GML_SCHEMA,
                XSD_HTTP_GML_SCHEMA_LOCATION));
        root.appendChild(createImport(XSD_HTTP_XLINK_SCHEMA,
                XSD_HTTP_XLINK_SCHEMA_LOCATION));

        //org.w3c.dom.Element XSDinclude1 = _xmldoc.createElement(XSD_TAG_INCLUDE);
        String coreAIXMDatatypesFileName = "";
        try
        {
            coreAIXMDatatypesFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_FEATURES).GetElement());
        }
        catch(Exception ex)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the file name for AIXM Features.");
        }

        if(!coreAIXMDatatypesFileName.isEmpty()){
            if(!coreAIXMDatatypesFileName.startsWith(".")){
                coreAIXMDatatypesFileName = "./" + coreAIXMDatatypesFileName;
            }
        }

        root.appendChild(createImport(AIXM_51_DEFAULT_TARGET_NAMESPACE, coreAIXMDatatypesFileName));


        // Create general annotation: provide the list of the reverse associations that are generated
        root.appendChild(createGeneralAnnotation());


        // Create Extensions
        for (Iterator i = _featureMap.keySet().iterator(); i.hasNext();) {
            String featureName = (String) i.next();
            ArrayList listOfFeatures = (ArrayList) _featureMap.get(featureName);

            root.appendChild(createFeatureExtensionElement(featureName));
            root.appendChild(createFeatureExtensionType(featureName));
            root.appendChild(createFeatureExtensionPropertyGroup(featureName));
        }
        _serializer.serialize(root);
    }

    private Comment createGeneralAnnotation()
    {
        String note = "*******************************************\n" + "This Application Schema is autogenerated from the core schema AIXM_Features.xsd. It supports the following reverse associations:\n";
        ArrayList keys = new ArrayList(_featureMap.keySet());
        Collections.sort(keys);
        for (Iterator it = keys.iterator();it.hasNext();)
        {
            String featureName = (String) it.next();
            ArrayList listOfFeatures = (ArrayList) _featureMap.get(featureName);
            note = note + "* [" + featureName + "] ====> ";
            for (Iterator it2 = listOfFeatures.iterator();it2.hasNext();)
            {
                note = note + "[" + it2.next() + "], ";
            }
            note = note + "\n";
        }
        note = note + "*******************************************";
        Comment annotation = _xmldoc.createComment(note);
        return annotation;
    }

    /**
     *
     * @return
     */
    private Element createRoot() {
        Element root = _xmldoc.createElement(XSD_TAG_ROOT);
        root.setAttribute(XSD_XML_NAMESPACE + ":" + AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE,
                AIXM_51_DEFAULT_TARGET_NAMESPACE + "/"
                        + AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE);
        root.setAttribute(XSD_XML_NAMESPACE, XSD_HTTP_XML_SCHEMA);
        root.setAttribute(XSD_XML_NAMESPACE + ":" + "xsd", XSD_HTTP_XML_SCHEMA);
        root.setAttribute(XSD_XML_NAMESPACE + ":" + "aixm", AIXM_51_DEFAULT_TARGET_NAMESPACE);
        root.setAttribute(XSD_XML_NAMESPACE + ":" + "gml", XSD_HTTP_GML_SCHEMA);
        root.setAttribute(XSD_XML_NAMESPACE + ":" + "xlink", XSD_HTTP_XLINK_SCHEMA);
        root.setAttribute("targetNamespace", AIXM_51_DEFAULT_TARGET_NAMESPACE + "/" + AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE);
        root.setAttribute("elementFormDefault", "qualified");
        root.setAttribute("attributeFormDefault", "unqualified");
        root.setAttribute("version", AIXM_TAGGED_VALUE_CORE_VERSION);

        return root;
    }

    /**
     *
     * @param namespace
     * @param schemaLocation
     * @return
     */

    private Element createImport(String namespace, String schemaLocation) {
        Element importElement = _xmldoc.createElement(XSD_TAG_IMPORT);
        importElement.setAttribute("namespace", namespace);
        importElement.setAttribute("schemaLocation", schemaLocation);

        return importElement;
    }

    /**
     *
     * @param featureName
     * @return
     */
    private Element createFeatureExtensionElement(String featureName) {
        Element extensionElement = _xmldoc.createElement(XSD_TAG_ELEMENT);
        extensionElement.setAttribute("name", featureName + "Extension");
        extensionElement.setAttribute("type", AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE + ":"
                + featureName + "ExtensionType");
        extensionElement.setAttribute("substitutionGroup", "aixm:Abstract"
                + featureName + "Extension");
        return extensionElement;
    }

    /**
     *
     * @param featureName
     * @return
     */
    private Element createFeatureExtensionType(String featureName) {
        Element extensionType = _xmldoc.createElement(XSD_TAG_COMPLEX_TYPE);
        extensionType.setAttribute("name", featureName + "ExtensionType");

        Element complexContent = _xmldoc.createElement(XSD_TAG_COMPLEX_CONTENT);

        Element extension = _xmldoc.createElement(XSD_TAG_EXTENSION);
        extension.setAttribute("base", "aixm:AbstractExtensionType");

        Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);

        Element group = _xmldoc.createElement(XSD_TAG_GROUP);
        group.setAttribute("ref", AIXM_51_EXTENSION_REVERSE_ASSOCIATIONS_EXTENSION_NAMESPACE + ":" + featureName
                + "ExtensionPropertyGroup");

        sequence.appendChild(group);
        extension.appendChild(sequence);
        complexContent.appendChild(extension);
        extensionType.appendChild(complexContent);

        return extensionType;
    }

    /**
     *
     * @param featureName
     * @return
     */
    private Element createFeatureExtensionPropertyGroup(String featureName) {
        Element propertyGroup = _xmldoc.createElement(XSD_TAG_GROUP);
        propertyGroup.setAttribute("name", featureName
                + "ExtensionPropertyGroup");

        Element sequence = _xmldoc.createElement(XSD_TAG_SEQUENCE);

        ArrayList listOfFeatures = (ArrayList) _featureMap.get(featureName);
        for (Iterator i = listOfFeatures.iterator(); i.hasNext();) {
            String relatedFeature = (String) i.next();
            Element element = _xmldoc.createElement(XSD_TAG_ELEMENT);
            element.setAttribute("name", "the" + relatedFeature);
            element.setAttribute("type", "aixm:" + relatedFeature
                    + "PropertyType");
            element.setAttribute("nillable", "true");
            element.setAttribute("minOccurs", "0");
            element.setAttribute("maxOccurs", "unbounded");

            sequence.appendChild(element);
        }

        propertyGroup.appendChild(sequence);

        return propertyGroup;
    }
}
