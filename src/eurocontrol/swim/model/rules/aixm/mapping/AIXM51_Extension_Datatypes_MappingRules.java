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

import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIXMExtensionsConstants;

/**
 * @author hlepori
 */
public class AIXM51_Extension_Datatypes_MappingRules extends
        AIXM51_Datatypes_MappingRule implements AIXMExtensionsConstants {
    
    /**
     * AIXM extension properties
     */
    protected String _extensionVersion;

    /**
     * @param targetPackage
     * @throws IOException
     */
    public AIXM51_Extension_Datatypes_MappingRules(Package targetPackage) throws IOException {
        super(targetPackage);
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
    	_generateFileName = AIXM_51_EXTENSION_DEFAULT_GENERATE_FILE_NAME_DATATYPES;
        _targetNamespace = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE;
        _targetNamespacePrefix = AIXM_51_EXTENSION_DEFAULT_TARGET_NAMESPACE_PREFIX;
    	_attributeFormDefault = AIXM_51_EXTENSION_DEFAULT_ATTRIBUTE_FORM;
    	_elementFormDefault = AIXM_51_EXTENSION_DEFAULT_ELEMENT_FORM;
    }


    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.mapping.AIXM51_Datatypes_MappingRule#createRoot()
     */
    protected org.w3c.dom.Element createRoot() {
        
        org.w3c.dom.Element root = super.createRoot();
      
        String coreTargetNamespacePrefix = AIXM_51_DEFAULT_TARGET_NAMESPACE_PREFIX;
        String coreTargetNamespace = AIXM_51_DEFAULT_TARGET_NAMESPACE;
        String coreAIXMDatatypesFileName = AIXM_51_DEFAULT_GENERATE_FILE_NAME_DATATYPES;
		try
		{
		    coreTargetNamespacePrefix = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE_PREFIX, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace prefix for the core AIXM datatypes. Using a default value.");
		}

		// OVA2015 AIXMSCR-3
        try
        {
            extensionRelativePathPrefix = null;
            extensionFileLocation = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION, this.getTargetPackage().GetElement());
            if (extensionFileLocation==null || extensionFileLocation.length()==0) throw new Exception("tagged valuie not found");
        }
        catch(Exception e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the extension file location ("+ AIXM_TAGGED_VALUE_EXTENSION_FILE_LOCATION +") from the AIXM Datatypes package. Using the default value.");
            extensionFileLocation = AIXM_51_DEFAULT_EXTENSION_FILE_LOCATION;
        }

		try
		{
		    coreTargetNamespace = SparxUtilities.getContentOfTaggedValue(XSD_TAGGED_VALUE_TARGET_NAMESPACE, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the target namespace for the core AIXM datatypes. Using a default value.");
		}
		
		try
		{
		    coreAIXMDatatypesFileName = SparxUtilities.getContentOfTaggedValue(AIXM_TAGGED_VALUE_GENERATE_FILE_NAME, EAConnection.getInstance().getRepository().GetPackageByGuid(AIXM_51_GUID_PACKAGE_AIXM_DATATYPES).GetElement());
		}
		catch(Exception e)
		{
		    EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": Could not retrieve the file name for AIXM datatypes. Using a default file name.");
		}

        
	    //root.setAttribute(XSD_XML_NAMESPACE + ":" + coreTargetNamespacePrefix, coreTargetNamespace);
	    
	    // include the version of the extension 
	    root.setAttribute("version", _extensionVersion);

        //include dependencies between files

       // importsMap.put(coreAIXMDatatypesFileName, new String[]{coreTargetNamespacePrefix, coreTargetNamespace});
        inspectPackageForLinks(_targetPackage, _targetNamespace);
        addLinksToXSDRoot(root,true);

        return root;
    }

}
