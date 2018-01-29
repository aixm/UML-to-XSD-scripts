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

package eurocontrol.swim.model.util.traceability;

import java.io.Serializable;

import org.sparx.Attribute;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.Element;

import eurocontrol.swim.model.sparx.EAConstants;

/**
 * @author hlepori
 */
public class UML_Construct implements EAConstants, Serializable {    
    
    protected String _guid; 
    
    protected String _name;
    
    protected String _type;
    
    protected String _definition;
    
    
    /**
     * 
     */
    public UML_Construct() {
        super();
    }
    
    /**
     * 
     */
    public UML_Construct(Element element) {
        _guid = element.GetElementGUID();
        _name = element.GetName();
        _type = EA_TYPE_CLASS;
        _definition = element.GetNotes();
    }
    
    /**
     * 
     */
    public UML_Construct(Attribute attribute) {
        _guid = attribute.GetAttributeGUID();
        _name = attribute.GetName();
        _type = EA_TYPE_ATTRIBUTE;
        _definition = attribute.GetNotes();
    }
    
    /**
     * 
     */
    public UML_Construct(ConnectorEnd role, Connector connector, Element element) {
        _guid = connector.GetConnectorGUID() + " " + element.GetElementGUID();
        _name = role.GetRole();
        _type = EA_TYPE_CONNECTOR_END;
        _definition = role.GetRoleNote();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        if(arg0 instanceof UML_Construct)
            return _guid.equals(((UML_Construct)arg0).getGuid());
        return false;
    }
    
    protected void setName(String name)
    {
        _name = name;
    }
    
    public String getDefinition() {
        return _definition;
    }
    public String getGuid() {
        return _guid;
    }
    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {        
        return "["+_type + "] " + _name;
    }
    
}
