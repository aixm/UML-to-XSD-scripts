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

package eurocontrol.swim.model.util.constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author hlepori
 */
public class FIXMProperties {

	/**
	 * Single instance of FIXMProperties (Singleton)
	 */
	private static final FIXMProperties _instance = new FIXMProperties();
	
	private Properties _FIXMProperties; 
    
	/**
	 * Private Constructor
	 */
	private FIXMProperties() {
	    _FIXMProperties = new Properties();
        try
        {
	        FileInputStream in = new FileInputStream("resources/FIXMProperties");
	        _FIXMProperties.load(in);
	        in.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            // DO nothing.
        }		
	}

	/**
	 * Get the single instance of FIXMProperties.
	 * @return
	 */
	public static final FIXMProperties getInstance ()
	{	
		return _instance;
	}
	
	public String getProperty(String key)
	{
	    return _FIXMProperties.getProperty(key);
	}
}
