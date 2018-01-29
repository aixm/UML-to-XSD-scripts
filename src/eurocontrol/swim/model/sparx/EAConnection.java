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


package eurocontrol.swim.model.sparx;
import java.util.ArrayList;

import org.sparx.Element;
import org.sparx.Repository;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.util.SparxUtilities;
import eurocontrol.swim.model.util.constants.ToolConstants;


/**
 * @author hlepori
 */
public class EAConnection implements EAConstants, ToolConstants, Runnable {
	
	/**
	 * Single instance of EAConnector (Singleton)
	 */
	private static final EAConnection _instance = new EAConnection();

	/**
	 * The EA repository
	 */
	private final Repository _repository;
	
	/**
	 * The list of all UML elements
	 */
	private ArrayList _allElements;
	
	
	/**
	 * Private Constructor
	 */
	private EAConnection() {
		// Open EA project
		_repository = new Repository();
		new Thread(this).start();
	}

	/**
	 * Get the single instance of EAConnector.
	 * @return
	 */
	public static final EAConnection getInstance ()
	{	
		return _instance;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        //empty
    }
	
	/**
	 * 
	 * @author hlepori
	 */
	public final void openUMLModel(boolean local, String path, String userName, String password)
	{
	    if(local)
	    { 
	        fireEAEvent("Opening EA project " + path);
	        boolean result = _repository.OpenFile(path);
	        if(result)
	            fireEAEvent("Loading complete... ");
	        else
	            fireEAEvent(MESSAGE_LOCAL_FILE_LOAD_FAILED);
	    }
	    else
	    {
	        fireEAEvent("Connecting to the DB server at [" + path + "]");
	        boolean result = _repository.OpenFile2(path,userName, password);
	        if(result)
	            fireEAEvent("Connected!");
	        else
	            fireEAEvent(MESSAGE_DB_CONNECTION_FAILED);
	    }
	}

	
	/**
	 * Return all the elements of the model
	 *
	 */
	public ArrayList getAllElements()
	{
		if(_allElements == null)
		{
			_allElements = new ArrayList();
			SparxUtilities.addAll(_repository.GetElementSet("",0),_allElements);
		}
		return _allElements;
	}
	
	
	
	/**
	 * @return Returns the repository.
	 */
	public Repository getRepository() {
		return _repository;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public Element getElementByID(int id)
	{
		return _repository.GetElementByID(id);
	}

	
	public void fireEAEvent(String text)
	{
	    EAEventManager.getInstance().fireEAEvent(this,text);
	}

}
