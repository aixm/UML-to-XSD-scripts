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

package eurocontrol.swim.model.rules.sdm.mapping;

import java.io.IOException;

import org.sparx.Package;
import org.w3c.dom.Element;

import eurocontrol.swim.model.rules.common.AbstractMappingRule;

/**
 * @author hlepori
 */
public class SDM_MappingRule extends AbstractMappingRule {
   
    /**
     * 
     */
    public SDM_MappingRule(Package targetPackage) throws IOException {
        super(targetPackage);
        setDefaultValues();
        initXSDFileProperties();
        initXSDFile();
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.AbstractMappingRule#getOutputFile()
     */
    public String getOutputFile() {
        return "./output/" + "SDM.xsd";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.AbstractMappingRule#initXSDFileProperties()
     */
    protected void initXSDFileProperties() {
        super.initXSDFileProperties();
        
        // TODO add SDM-specific code here       
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.AbstractMappingRule#generateXSD()
     */
    public void generateXSD() throws IOException {
        org.w3c.dom.Element xsdRoot = this.createRoot();
        
        if(xsdRoot != null)
        {  
            // TODO add SDM-specific code here
            
            _serializer.serialize(xsdRoot);
        }
    }
    
    

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.AbstractMappingRule#createRoot()
     */
    protected Element createRoot() {
        org.w3c.dom.Element root = super.createRoot();
        
        // TODO add SDM-specific code here
        
        return root;
    }


}
