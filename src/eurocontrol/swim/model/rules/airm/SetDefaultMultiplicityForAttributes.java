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

package eurocontrol.swim.model.rules.airm;

import java.util.Iterator;

import org.sparx.Attribute;
import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.rules.common.AbstractRule;
import eurocontrol.swim.model.sparx.EAConstants;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;

/**
 * @author hlepori
 */
public class SetDefaultMultiplicityForAttributes extends AbstractRule implements
        EAConstants, AIRMConstants {

    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.Rule#getName()
     */
    public String getName() {
        return "[AIRM] QUALITY CHECK - Set default multiplicty for attributes.";
    }
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.AbstractRule#applyRuleToPackage(org.sparx.Package)
     */
    public void applyRuleToPackage(Package myPackage) {
        processPackageContentRecursively(myPackage.GetPackages());
    }
    
    private void processPackageContentRecursively(Collection packageCollection) {

        if (packageCollection!= null && packageCollection.GetCount() > 0) {
            // local variable to store a subpackage
            Package subPackage = null;
            // local variable to store the element (= a class)
            Element element = null;
            // local variable to store an attribute
            Attribute attribute = null;

            
            ////////////////////////////////////////////////////
            // check the sub packages
            ////////////////////////////////////////////////////
            for (Iterator packageIter = packageCollection.iterator(); packageIter
                    .hasNext();) {
                subPackage = (Package) packageIter.next();
                 
                EAEventManager.getInstance().fireEAEvent(this,"Updating package " + subPackage.GetName());

                /////////////////////////////////////////////////
                // Process the classes defined in this subpackage
                /////////////////////////////////////////////////
                for (Iterator elementsIter = subPackage.GetElements()
                        .iterator(); elementsIter.hasNext();) {
                    
                    element = (Element) elementsIter.next();
                    
                    // The class element is used generically
                    // WARNING: in some cases, an element of type enumeration
                    // ...
                    if (element.GetType().equals(EA_TYPE_CLASS) || element.GetType().equals(EA_TYPE_ENUMERATION)) {
                        
                         ///////////////////////////////////////////////////
                         // Process the class attributes                        
                         ///////////////////////////////////////////////////
                         
                         for (Iterator attributeIter = element.GetAttributes().iterator();attributeIter.hasNext();)
                         {
                             attribute = (Attribute)attributeIter.next();
                          try
                          {
                              if(attribute.GetLowerBound().equals("1") && attribute.GetUpperBound().equals("1"))
                              {
                                  attribute.SetLowerBound("0");
                                  boolean isUpdateSuccessful = attribute.Update();
                                  if(!isUpdateSuccessful)
                                  {
                                      throw new Exception();
                                  }
                              }                                                            
                          }
	                         catch(Exception e)
	                         {
	                             EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": Could not update attribute " + SparxUtilities.toString(attribute, element));
	                         }
                         }                        
                    } 
                }  
                processPackageContentRecursively(subPackage.GetPackages());
            }
        } 
    }
}
