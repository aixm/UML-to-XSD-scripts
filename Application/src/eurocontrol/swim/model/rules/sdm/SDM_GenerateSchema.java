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

package eurocontrol.swim.model.rules.sdm;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractMappingRule;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.rules.sdm.mapping.SDM_MappingRule;
import eurocontrol.swim.model.util.constants.SDMConstants;
import eurocontrol.swim.model.util.constants.XSDConstants;

/**
 * @author hlepori
 */
public class SDM_GenerateSchema extends AbstractRule implements  XSDConstants, SDMConstants {

    private JCheckBox _includeDocumentationCheckBox;
    private JCheckBox _autoCorrectionCheckBox;
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {       
        return "[SDM / Any physical model] XML SCHEMAS GENERATION - Generate XML Schemas for selected model";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        try
        {
            AbstractMappingRule mappingRule = new SDM_MappingRule(myPackage);
            mappingRule.setAutoCorrection(_autoCorrectionCheckBox.isSelected());
            mappingRule.setIncludeDocumentation(_includeDocumentationCheckBox.isSelected());
            mappingRule.generateXSD();
            
            EAEventManager.getInstance().fireEAEvent(this,
            "Done...");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }
        catch(IOException e)
        {
            EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not generate the XSD ("+e.getMessage()+").");
        }

    }
    

    
    /**
     * 
     */
    protected void initGUI(){
        _includeDocumentationCheckBox = new JCheckBox();
        _includeDocumentationCheckBox.setText("Include documentation");
        _includeDocumentationCheckBox.setSelected(true);//OVA2015 AIXMSCR-5
        
        _autoCorrectionCheckBox = new JCheckBox();
        _autoCorrectionCheckBox.setText("Automatically correct the UML model");
        _autoCorrectionCheckBox.setSelected(false);
        
        super.initGUI();
    }
    
    /**
     * 
     * @return
     */
    protected JPanel buildPanel()
    {
        JPanel generateXSDForExtensionPanel = new JPanel(new BorderLayout());
        
        JPanel checkBoxPanel = new JPanel(new BorderLayout());
        checkBoxPanel.add(_includeDocumentationCheckBox,BorderLayout.NORTH);
        checkBoxPanel.add(_autoCorrectionCheckBox,BorderLayout.SOUTH);
        
        generateXSDForExtensionPanel.add(checkBoxPanel,BorderLayout.NORTH);
        generateXSDForExtensionPanel.add(new JScrollPane(_modelBrowser),BorderLayout.CENTER);
        
        return generateXSDForExtensionPanel;
    }    
}
