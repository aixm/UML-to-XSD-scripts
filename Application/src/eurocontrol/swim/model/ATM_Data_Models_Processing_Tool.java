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

package eurocontrol.swim.model;

import eurocontrol.swim.model.gui.Mainframe;
import eurocontrol.swim.model.rules.aixm.AIXM51_GenerateApplicationSchema;

/**
 * @author hlepori
 */
public class ATM_Data_Models_Processing_Tool {

    public static void main(String[] args) {                
        Mainframe frame = new Mainframe();
        frame.pack();
        frame.setVisible(true);
        if (args.length==7 && args[0].equalsIgnoreCase("autorun"))
        {
            String eapPath = args[1];
            String aixmFeaturesPackageName = args[2];
            String aixmDatatypesPackageName = args[3];
            String extensionFeaturesPackageName = args[4];
            String extensionDatatypesPackageName = args[5];
            String extensionMessagePackageName = args[6];
            AIXM51_GenerateApplicationSchema.autoApplyRules(eapPath,aixmFeaturesPackageName,aixmDatatypesPackageName,extensionFeaturesPackageName,extensionDatatypesPackageName,extensionMessagePackageName);
            System.out.println("finished!");
        }
    }
}
