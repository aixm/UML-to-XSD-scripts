/**
 * ##########################################################################
 * ##########################################################################
 * <p>
 * Copyright (c) 2010, EUROCONTROL
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of EUROCONTROL nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * ##########################################################################
 * ##########################################################################
 */

package eurocontrol.swim.model.rules.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Collection;

import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.constants.AIXMConstants;
import org.sparx.*;
import org.sparx.Package;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.sparx.EAConstants;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.ToolConstants;
import eurocontrol.swim.model.util.constants.XSDConstants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author hlepori
 */
public abstract class AbstractMappingRule implements XSDConstants, EAConstants, ToolConstants, AIXMConstants {
    /**
     * The XML serializer
     */
    protected XMLSerializer _serializer;

    /**
     * The Output Format
     */
    protected OutputFormat _of = new OutputFormat("XML", "UTF-8", true);

    /**
     * The XML Document
     */
    protected Document _xmldoc = createDocument(); // OVA 2015 AIXMSCR-2
    //protected Document _xmldoc = new DocumentImpl();

    /**
     * The FileOutputStream
     */
    protected FileOutputStream _fos;

    /**
     * A boolean indicating whether the documentation (notes description) has to be included in the XSD or not.
     * Set to true by default.
     */
    protected boolean _includeDocumentation = false;

    /**
     * A boolean indicating whether the bugfix that add the missing maxOccurs to choice target associations should be bypassed or not (useful only for the core AIXM XSDs, in order to provide backward compatibility)
     * Set to false by default.
     */
    protected boolean _choiceFixDisabled = false; //OVA2015 AIXMSCR-4

    /**
     * A boolean indicating whether the script automatically corrects some modelling erros in the UML or not
     * Set to true by default.
     */
    protected boolean _autoCorrection = true;

    /**
     * The Sparx EA package to be processed
     */
    protected Package _targetPackage;

    /**
     * The root node
     */
    protected org.w3c.dom.Element _root;

    /**
     * The XSD properties
     */
    protected String _targetNamespace;
    protected String _targetNamespacePrefix;
    protected String _attributeFormDefault;
    protected String _elementFormDefault;

    /**
     * Managing XSD includes
     */
    protected Set<String[]> includesSet = new HashSet<String[]>();
    protected HashMap<String, String[]> importsMap = new HashMap<String, String[]>(); //fileName -> namespace

    /**
     *
     */
    public AbstractMappingRule(Package targetPackage) throws IOException {
        _targetPackage = targetPackage;
    }

    /**
     *
     *
     */
    protected void initXSDFileProperties() {
        try {
            if (SparxUtilities.isTaggedValuePopulated(XSD_TAGGED_VALUE_TARGET_NAMESPACE, _targetPackage.GetElement()))
                _targetNamespace = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE, _targetPackage.GetElement());
            else
                throw new Exception();
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + XSD_TAGGED_VALUE_TARGET_NAMESPACE + ". Using default value.");
        }
        try {
            if (SparxUtilities.isTaggedValuePopulated(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, _targetPackage.GetElement()))
                _targetNamespacePrefix = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, _targetPackage.GetElement());
            else
                throw new Exception();
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX + ". Using default value.");
        }
        try {
            if (SparxUtilities.isTaggedValuePopulated(XSD_TAGGED_VALUE_ATTRIBUTE_FORM_DEFAULT, _targetPackage.GetElement()))
                _attributeFormDefault = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_ATTRIBUTE_FORM_DEFAULT, _targetPackage.GetElement());
            else
                throw new Exception();
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + XSD_TAGGED_VALUE_ATTRIBUTE_FORM_DEFAULT + ". Using default value.");
        }
        try {
            if (SparxUtilities.isTaggedValuePopulated(XSD_TAGGED_VALUE_ELEMENT_FORM_DEFAULT, _targetPackage.GetElement()))
                _elementFormDefault = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_ELEMENT_FORM_DEFAULT, _targetPackage.GetElement());
            else
                throw new Exception();
        } catch (Exception e) {
            EAEventManager.getInstance().fireEAEvent(this, MESSAGE_WARNING + ": The package " + _targetPackage.GetName() + " has no tagged value " + XSD_TAGGED_VALUE_ELEMENT_FORM_DEFAULT + ". Using default value.");
        }
    }

    /**
     *
     *
     */
    protected void setDefaultValues() {
        _targetNamespacePrefix = XSD_DEFAULT_TARGET_NAMESPACE_PREFIX;
        _targetNamespace = XSD_DEFAULT_TARGET_NAMESPACE;
        _elementFormDefault = XSD_DEFAULT_ELEMENT_FORM;
        _attributeFormDefault = XSD_DEFAULT_ATTRIBUTE_FORM;
    }

    /**
     *
     *
     */
    protected void initXSDFile(String file) throws IOException {
        _fos = new FileOutputStream(file);
        _serializer = new XMLSerializer(_fos, _of);
        _serializer.asDOMSerializer();
    }

    /**
     *
     *
     */
    protected void initXSDFile() throws IOException {
        initXSDFile(getOutputFile());
    }

    /**
     *
     *
     */
    public abstract void generateXSD() throws IOException;

    public abstract String getOutputFile();

    // OVA 2015 AICMSCR-2 (required for xpath search)
    static protected Document createDocument() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (Exception e) {
            //should never happens, plain and simple construction, no special parameter in use.
        }
        Document doc = docBuilder.newDocument();
        return doc;
    }


    /**
     * Create the root tag of the XSD 
     * @return
     */
    protected org.w3c.dom.Element createRoot() {
        org.w3c.dom.Element root = _xmldoc.createElement(XSD_TAG_ROOT);
        _xmldoc.appendChild(root); // OVA 2015 AICMSCR-2 (required for xpath search)
        root.setAttribute(XSD_XML_NAMESPACE, XSD_DEFAULT_TARGET_NAMESPACE);

        return root;
    }

    /**
     *
     * @param documentation
     * @return
     */

    protected org.w3c.dom.Node createXSDAnnotation(String documentation) {
        //OVA2015 AIXMSCR-5
        if (documentation != null && documentation.trim().length() != 0) {
            org.w3c.dom.Element xsdAnnotation = _xmldoc.createElement(XSD_TAG_ANNOTATION);
            org.w3c.dom.Element xsdDocumentation = _xmldoc.createElement(XSD_TAG_DOCUMENTATION);
            xsdDocumentation.setTextContent(documentation);
            xsdAnnotation.appendChild(xsdDocumentation);
            return xsdAnnotation;
        } else {
            return _xmldoc.createComment("No annotation found for this element.");
        }
    }


    /**
     * Create a generic function that can create deprecated annotations for attributes,
     * features/objects and associations
     * @param obj
     * @param <T>
     * @return
     */

    protected <T> org.w3c.dom.Node createAnnotation(T obj, boolean _includeDeprecation, String documentation) {
        org.w3c.dom.Element xsdAnnotation = _xmldoc.createElement(XSD_TAG_ANNOTATION);

        if(_includeDeprecation){
            ObjectType objType = null;
            org.sparx.Collection tags = null;

            if (obj instanceof Attribute) {
                objType = ObjectType.otAttribute;
                tags = ((Attribute) obj).GetTaggedValues();
            } else if (obj instanceof Element) {
                objType = ObjectType.otElement;
                tags = ((Element) obj).GetTaggedValuesEx();
            } else if (obj instanceof Connector) {
                objType = ObjectType.otConnector;
                tags = ((Connector) obj).GetTaggedValues();
            }

            String rationaleText = "", replacementText = "", deprecationVersionText = "", deletionVersionText = "";
            org.w3c.dom.Element xsdAppInfo = _xmldoc.createElement(XSD_TAG_APPINFO);
            xsdAppInfo.setTextContent(AIXM_51_STEREOTYPE_DEPRECATED);

            if (objType != null){
            if (objType.equals(ObjectType.otElement)) {
                for (TaggedValue tag : (org.sparx.Collection<TaggedValue>) tags) {
                    if (tag.GetName().equals(AIXM_TAGGED_VALUE_RATIONALE)) {
                        rationaleText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_REPLACEMENT)) {
                        replacementText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DEPRECATION_VERSION)) {
                        deprecationVersionText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DELETION_VERSION)) {
                        deletionVersionText = tag.GetValue();
                    }

                }
            } else if (objType.equals(ObjectType.otAttribute)) {
                for (AttributeTag tag : (org.sparx.Collection<AttributeTag>) tags) {
                    if (tag.GetName().equals(AIXM_TAGGED_VALUE_RATIONALE)) {
                        rationaleText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_REPLACEMENT)) {
                        replacementText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DEPRECATION_VERSION)) {
                        deprecationVersionText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DELETION_VERSION)) {
                        deletionVersionText = tag.GetValue();
                    }
                }
            } else if (objType.equals(ObjectType.otConnector)) {
                for (ConnectorTag tag : (org.sparx.Collection<ConnectorTag>) tags) {
                    if (tag.GetName().equals(AIXM_TAGGED_VALUE_RATIONALE)) {
                        rationaleText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_REPLACEMENT)) {
                        replacementText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DEPRECATION_VERSION)) {
                        deprecationVersionText = tag.GetValue();
                    } else if (tag.GetName().equals(AIXM_TAGGED_VALUE_DELETION_VERSION)) {
                        deletionVersionText = tag.GetValue();
                    }
                }
            }
                org.w3c.dom.Element xsdDocumentation = _xmldoc.createElement(XSD_TAG_DOCUMENTATION);
                org.w3c.dom.Element xsdDeprecated = _xmldoc.createElement(XSD_TAG_DEPRECATED);
                org.w3c.dom.Element xsdRationale = _xmldoc.createElement(XSD_TAG_RATIONALE);
                org.w3c.dom.Element xsdReplacement = _xmldoc.createElement(XSD_TAG_REPLACEMENT);
                org.w3c.dom.Element xsdDeprecatedVersion = _xmldoc.createElement(XSD_TAG_DEPRECATION_VERSION);
                org.w3c.dom.Element xsdDeletionVersion = _xmldoc.createElement(XSD_TAG_DELETION_VERSION);

                xsdRationale.setTextContent(rationaleText);
                xsdReplacement.setTextContent(replacementText);
                xsdDeprecatedVersion.setTextContent(deprecationVersionText);
                xsdDeletionVersion.setTextContent(deletionVersionText);

                xsdDeprecated.appendChild(xsdRationale);
                xsdDeprecated.appendChild(xsdReplacement);
                xsdDeprecated.appendChild(xsdDeprecatedVersion);
                xsdDeprecated.appendChild(xsdDeletionVersion);

                xsdDocumentation.appendChild(xsdDeprecated);

                xsdAnnotation.appendChild(xsdAppInfo);
                xsdAnnotation.appendChild(xsdDocumentation);
            }
        }
        if(documentation != null && documentation.trim().length() != 0) {
            org.w3c.dom.Element xsdDocumentation = _xmldoc.createElement(XSD_TAG_DOCUMENTATION);
            xsdDocumentation.setTextContent(documentation);
            xsdAnnotation.appendChild(xsdDocumentation);
            }
        else if(!_includeDeprecation)
            return _xmldoc.createComment("No annotation found for this element.");

        return xsdAnnotation;
    }

    /**
     * Get the parent class of a UML class
     *
     * @param UMLClass
     * @return
     */
    protected Element getUMLParent(Element UMLClass) {
        if (UMLClass.GetBaseClasses().GetCount() > 0) {
            return (Element) UMLClass.GetBaseClasses().GetAt((short) 0);
        }
        return null;

    }

    public void setAutoCorrection(boolean autoCorrection) {
        _autoCorrection = autoCorrection;
    }

    public void setIncludeDocumentation(boolean includeDocumentation) {
        _includeDocumentation = includeDocumentation;
    }

    //OVA2015 AIXMSCR-4
    public void setChoiceFixDisabled(boolean choiceFixDisabled) {
        _choiceFixDisabled = choiceFixDisabled;
    }


    protected void inspectPackageForLinks(Package p, String rootNameSpace) {
        inspectPackageForLinks(p, p, rootNameSpace);
    }

    protected void inspectPackageForLinks(Package selectedPackage, Package p, String rootNameSpace) {

        p.GetConnectors().Refresh();
        Connector connector;

        try {

            //OVA2015 AIXMSCR-3
            Element selectedPackageElement = selectedPackage.GetElement();
            String selectedPackageFileLocation = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION, selectedPackageElement);
            boolean isSelectedPackageWithSpecialPath = selectedPackageFileLocation != null && selectedPackageFileLocation.length() != 0;

            if (p.GetConnectors().GetCount() > 0) {
                String prefix, nameSpace;
                for (Iterator<Connector> connectorsIterator = p.GetConnectors().iterator(); connectorsIterator.hasNext(); ) {
                    connector = connectorsIterator.next();
                    int targetID = connector.GetSupplierID();
                    if (targetID != p.GetElement().GetElementID()) {
                        Element target = EAConnection.getInstance().getRepository().GetElementByID(targetID);
                        prefix = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, target);
                        nameSpace = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE, target);

                        //OVA2015 AIXMSCR-3 - knowing if the target is an extension xsd (same path) or another xsd (root path)
                        String connectedPackageFileLocation = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION, target);
                        boolean isConnectedPackageWithSpecialPath = connectedPackageFileLocation != null && connectedPackageFileLocation.length() != 0;
                        String pathPrefix = null;
                        if (isSelectedPackageWithSpecialPath && !isConnectedPackageWithSpecialPath)
                            pathPrefix = this.getExtensionRelativePathPrefix();

                        if (rootNameSpace.equals(nameSpace)) {
                            //OVA2015 AIXMSCR-3
                            includesSet.add(new String[]{SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, target), pathPrefix});
                        } else if (connector.GetStereotype().equals(XSD_TAG_IMPORT)) {
                            //OVA2015 AIXMSCR-3
                            importsMap.put(
                                    SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, target),
                                    new String[]{prefix, nameSpace, pathPrefix});
                        }
                    }
                }
            }

            p.GetPackages().Refresh();
            if (p.GetPackages().GetCount() > 0) {
                for (Iterator<Package> packagesIterator = p.GetPackages().iterator(); packagesIterator.hasNext(); ) {
                    inspectPackageForLinks(selectedPackage, packagesIterator.next(), rootNameSpace);
                }
            }
        } catch (Exception e) {
            //goto next connector
            e.printStackTrace();
        }

    }

    // OVA2015 AIXMSCR-3
    protected String extensionFileLocation = null;
    protected String extensionRelativePathPrefix = null;

    public String getExtensionRelativePathPrefix() {
        if (extensionRelativePathPrefix == null) {
            extensionFileLocation = extensionFileLocation.replace('\\', '/');
            if (extensionFileLocation.startsWith("./")) extensionFileLocation = extensionFileLocation.substring(2);
            if (extensionFileLocation.startsWith("/")) extensionFileLocation = extensionFileLocation.substring(1);
            if (extensionFileLocation.endsWith("/"))
                extensionFileLocation = extensionFileLocation.substring(0, extensionFileLocation.length() - 1);
            int numberOfFolders = countOccurences("/", extensionFileLocation) + 1;
            StringBuffer extensionRelativePathPrefixPreparator = new StringBuffer();
            if (extensionFileLocation.length() == 0 || extensionFileLocation.equalsIgnoreCase(".")) {
                numberOfFolders = 0;
                extensionRelativePathPrefixPreparator.append("./");
            } else for (int x = 0; x < numberOfFolders; x++) {
                extensionRelativePathPrefixPreparator.append("../");
            }
            extensionRelativePathPrefix = extensionRelativePathPrefixPreparator.toString();
        }
        return extensionRelativePathPrefix;
    }

    static int countOccurences(String ofThisString, String insideThisString) {
        // ./extension/EUR/eASM/v3.5
        return (insideThisString.length() - insideThisString.replace(ofThisString, "").length()) / ofThisString.length();
    }

    /**
     * Auxiliar function to unescape XML strings
     * @param xml A string formatted with escaped XML characters returns
     * @return a copy with the corresponding unescaped characters
     */

    protected String unescapeXML(String xml) {

        return xml.replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&lt;", "<")
                .replaceAll("$gt;", ">")
                .replaceAll("&apos;", "\'");
    }

    protected void addLinksToXSDRoot(org.w3c.dom.Element root, boolean isExtension) {


        String key;
        if (!importsMap.isEmpty()) {
            for (Iterator<String> importKeysIterator = importsMap.keySet().iterator(); importKeysIterator.hasNext(); ) {
                key = importKeysIterator.next();
                if (key != null && !key.isEmpty()) {
                    String importNamespace = importsMap.get(key)[1];
                    org.w3c.dom.Element impor = _xmldoc.createElement(XSD_TAG_IMPORT);
                    // OVA2015 AIXMSCR-3
                    //this method is only used by extensions, thus by default use the backward prefix (e.g. ../../../)
                    String prefix = importsMap.get(key)[2];
                    //use current path by default
                    if (prefix == null) prefix = "./";
                    //use no prefix is file is http://
                    if (key.contains("://")) prefix = "";
                    impor.setAttribute("namespace", importNamespace);
                    impor.setAttribute("schemaLocation", prefix + key);
                    root.appendChild(impor);

                    //TODO JCH : change "key" into prefix value
                    root.setAttribute(XSD_XML_NAMESPACE + ":" + importsMap.get(key)[0], importsMap.get(key)[1]);
                }
            }
        }

        if (!includesSet.isEmpty()) {
            for (Iterator<String[]> includesIterator = includesSet.iterator(); includesIterator.hasNext(); ) {
                String[] include = includesIterator.next();
                key = include[0];
                if (key != null && !key.isEmpty()) {
                    // OVA2015 AIXMSCR-3
                    String prefix = key.contains("://") ? "" : include[1];
                    //use current path if the xsd file is another extension!
                    if (prefix == null)
                        prefix = "./";
                    org.w3c.dom.Element includ = _xmldoc.createElement(XSD_TAG_INCLUDE);
                    includ.setAttribute("schemaLocation", prefix + key);
                    root.appendChild(includ);
                }
            }
        }
    }
}
