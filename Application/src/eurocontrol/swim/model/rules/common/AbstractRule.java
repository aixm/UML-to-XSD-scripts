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

package eurocontrol.swim.model.rules.common;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.sparx.Collection;
import org.sparx.Package;

import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.gui.common.PackageTreeObject;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.sparx.EAConstants;
import eurocontrol.swim.model.util.constants.ToolConstants;

/**
 * @author hlepori
 */
public abstract class AbstractRule implements Rule, ToolConstants, EAConstants {

    protected JPanel _dialogPanel;
    protected JTree _modelBrowser;
       
        
    
    /**
     * Initialise the GUI that captures the selection tree
     */
    protected void initGUI(){  
        _modelBrowser = buildTree();
        _dialogPanel = buildPanel();
    }
    
    /**
     * Build the selection tree reflecting the package structure of the selected model
     * @return
     */
    protected JTree buildTree()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Please select the package to be processed.");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        buildTreeRecursively(root, EAConnection.getInstance().getRepository().GetModels());

        JTree tree = new JTree(treeModel);
        return tree;
    }
    
    /**
     * Build the JPanel containing the model browser
     * @return
     */
    protected JPanel buildPanel()
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(_modelBrowser),BorderLayout.CENTER);   
        return panel;
    }
    
    /**
     * Build the selection tree recursively, based on the package structure of the selected model
     * @param parentNode
     * @param subPackages
     */
    protected void buildTreeRecursively (DefaultMutableTreeNode parentNode, Collection subPackages)
    {
        for(Iterator iter = subPackages.iterator();iter.hasNext();)
        {
            Package subPackage = (Package) iter.next();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new PackageTreeObject(subPackage));
            parentNode.add(node);
            buildTreeRecursively(node,subPackage.GetPackages());
        }
    }
    
    /**
     * Apply the rule to the model. The rule can be either applied to a list of packages or to the entire model. 
     */
    public void apply() {        
        EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        EAEventManager.getInstance().fireEAEvent(this,
                "=== Applying rule [" + getName() + "] to the model ===");
        
        
        Package[] packages = getPackagesToBeProcessed();
        
        if(packages !=null)
        {
	        for (int i = 0; i<packages.length;i++)
	        {
	            applyRuleToPackage(packages[i]);
	        }
            EAEventManager.getInstance().fireEAEvent(this,
            "=== Done ===");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }
        else
        {
            applyRuletoModel();
        }
    }
    

    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.rules.common.Rule#check()
     */
    public void check() {
        EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        EAEventManager.getInstance().fireEAEvent(this,
                "=== Checking that the model complies with the rule [" + getName() + "] ===");
        
        
        Package[] packages = getPackagesToBeProcessed();
        
        if(packages !=null)
        {
	        for (int i = 0; i<packages.length;i++)
	        {
	            checkPackageAgainstRule(packages[i]);
	        }
            EAEventManager.getInstance().fireEAEvent(this,
            "=== Done ===");
            EAEventManager.getInstance().fireEAEvent(this, SEPARATOR);
        }  

    }
    
    /**
     * 
     * @return
     */
    public Package[] getPackagesToBeProcessed()
    {
        // by default, the slection tree is build to enable the user to selct the package to be processed
        
        initGUI();
        
        Object[] options = {"Process","Cancel"};
        
        int n = JOptionPane.showOptionDialog(null,
                _dialogPanel,
                "Please select the package to be processed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title
        
        if (n==0)
        {
            Object selectedObject = _modelBrowser.getSelectionPath().getLastPathComponent();
            if(selectedObject != null && ((DefaultMutableTreeNode)selectedObject).getUserObject() instanceof PackageTreeObject)
            {
                Package myPackage = ((PackageTreeObject)((DefaultMutableTreeNode)selectedObject).getUserObject()).getPackage();              
          
                return new Package[]{myPackage};
            }           
        }
	    else
	    {
	        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_ERROR + ": The selected item is not a valid AIRM package.");
	    }
        return null;
    }
    
    /**
     * Apply the rule to the model as a whole. The method is called when no specific package(s) is (are) selected.  
     *
     */
    public void applyRuletoModel()
    {
        // NOT IMPLEMENTED
        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": NOT IMPLEMENTED!");
    }
    
    /**
     * 
     * @param myPackage
     */
    public void applyRuleToPackage(Package myPackage)
    {
        // NOT IMPLEMENTED
        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": NOT IMPLEMENTED!");
    }
    
    /**
     * 
     * @param myPackage
     */
    public void checkPackageAgainstRule(Package myPackage)
    {
        // NOT IMPLEMENTED
        EAEventManager.getInstance().fireEAEvent(this,MESSAGE_WARNING + ": NOT IMPLEMENTED!");

    }
    
    /**
     * 
     */
    public abstract String getName();
    
}
