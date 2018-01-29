/**
 *
##########################################################################
##########################################################################

Copyright (c) 2013, EUROCONTROL
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

package eurocontrol.swim.model.gui;

import eurocontrol.swim.model.gui.common.AbstractTab;
import eurocontrol.swim.model.rules.aixm.*;
import eurocontrol.swim.model.rules.common.Rule;
import eurocontrol.swim.model.rules.fixm.FIXM_ConceptualModel_CleanUp;
import eurocontrol.swim.model.rules.fixm.FIXM_GenerateSchema;
import eurocontrol.swim.model.rules.sdm.SDM_GenerateSchema;
import eurocontrol.swim.model.rules.wxxm.AIRM_to_WXXM_Rule;
import eurocontrol.swim.model.rules.wxxm.ApplyAIRMRuleBookToMetPackage;
import eurocontrol.swim.model.rules.wxxm.ExtractWXXMSpecificInformationtoExcel;

/**
 * @author hlepori
 */
public class PhysicalModelTab extends AbstractTab {

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.gui.common.AbstractTab#getRules()
     */
    public Rule[] getRules() {
        return new Rule[] {
                new AIXM51_GenerateDatatypesSchema(), 
                new AIXM51_GenerateFeaturesSchema(),
                null,
                new AIXM51_GenerateExtensionDataTypes(),
                new AIXM51_GenerateExtensionFeatures(),
                new AIXM51_GenerateExtensionMessages(),

                //new AIXM51_GenerateApplicationSchema(),
                null,
                new AIXM51_ReverseAssociationExtension(),

                null,
//                new ApplyAIRMRuleBookToMetPackage(),
//                new AIRM_to_WXXM_Rule(),
//                new ExtractWXXMSpecificInformationtoExcel(),
//                null,
//                new FIXM_GenerateSchema(),
//                new FIXM_ConceptualModel_CleanUp(),
//                null,
//                new SDM_GenerateSchema()
        };
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.gui.common.AbstractTab#getTabTitle()
     */
    public String getTabTitle() {
        return "Physical Models";
    }
    
}
