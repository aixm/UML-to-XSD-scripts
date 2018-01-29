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

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.aixm.mapping.AIXM51_Extension_Datatypes_MappingRules;
import eurocontrol.swim.model.rules.aixm.mapping.AIXM51_Extension_Features_MappingRule;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIXMConstants;
import eurocontrol.swim.model.util.constants.XSDConstants;
import org.sparx.*;
import org.sparx.Package;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;

public class AIXM51_GenerateExtensionDataTypes extends AbstractRule implements XSDConstants, AIXMConstants {

    private JCheckBox _includeDocumentationCheckBox;
    private JCheckBox _disableChoiceBugfixCheckBox;  //OVA2015 AIXMSCR-4
    private JCheckBox _autoCorrectionCheckBox;

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.Rule#getName()
     */
    public String getName() {
        return "[AIXM 5.1 Extension] XML SCHEMAS GENERATION - Generate Extension Data Types Schema.";
    }

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(org.sparx.Package myPackage) {
        org.sparx.Package datatypesPackage = myPackage;

        try{

            AIXM51_Extension_Datatypes_MappingRules mappingRuleDatatypes = new AIXM51_Extension_Datatypes_MappingRules(datatypesPackage);
            mappingRuleDatatypes.setAutoCorrection(_autoCorrectionCheckBox.isSelected());
            mappingRuleDatatypes.setIncludeDocumentation(_includeDocumentationCheckBox.isSelected());
            mappingRuleDatatypes.setChoiceFixDisabled(_disableChoiceBugfixCheckBox.isSelected());      //OVA2015 AIXMSCR-4
            mappingRuleDatatypes.generateXSD();


            EAEventManager.getInstance().fireEAEvent(this, "Done...");
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
        //OVA2015 AIXMSCR-4
        _disableChoiceBugfixCheckBox = new JCheckBox();
        _disableChoiceBugfixCheckBox.setText("Disable choice bugfix");
        _disableChoiceBugfixCheckBox.setSelected(false);

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
        checkBoxPanel.add(_disableChoiceBugfixCheckBox,BorderLayout.CENTER);     //OVA2015 AIXMSCR-4
        checkBoxPanel.add(_autoCorrectionCheckBox,BorderLayout.SOUTH);

        generateXSDForExtensionPanel.add(checkBoxPanel,BorderLayout.NORTH);
        generateXSDForExtensionPanel.add(new JScrollPane(_modelBrowser),BorderLayout.CENTER);

        return generateXSDForExtensionPanel;
    }

}
