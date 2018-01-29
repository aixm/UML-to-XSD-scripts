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

package eurocontrol.swim.model.gui.common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import eurocontrol.swim.model.rules.common.Rule;

/**
 * @author hlepori
 */
public abstract class AbstractTab extends JPanel {

    protected JButton _checkButton;
    protected JButton _applyButton;
    protected ArrayList _ruleCheckBoxes = new ArrayList();
    
    /**
     * 
     */
    public AbstractTab() {
        super(new BorderLayout());
        initialise();
        setBorder(new TitledBorder(getTabTitle()));
    }

    /**
     * 
     */
    private void initialise() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        _checkButton = new JButton("Check");
        _applyButton = new JButton("Apply");
        buttonPanel.add(_checkButton);
        buttonPanel.add(_applyButton);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        
    	Rule[] rules = getRules();
        
        JPanel rulePanel = new JPanel(new GridBagLayout());
   
        for (int i = 0; i < rules.length ; i++)
        {
         	GridBagConstraints c = new GridBagConstraints();
         	c.fill = GridBagConstraints.HORIZONTAL;
         	c.gridx = 0;
         	c.gridy = i;
            if(rules[i] == null)
            {
                rulePanel.add(new JSeparator(),c);
            }
            else
            {
	         	RuleCheckBox box = new RuleCheckBox(rules[i]);         	
	         	box.setSelected(i==0);
	         	_ruleCheckBoxes.add(box);
	            rulePanel.add(box,c);
            }
        }
        topPanel.add(rulePanel, BorderLayout.WEST);
        
        _checkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                _checkButton.setEnabled(false);
                _applyButton.setEnabled(false);
                
                new Thread(new Runnable() {
                    public void run() {
                        checkRules();
                    }
                }).start();
            }
        });
        
        _applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                _checkButton.setEnabled(false);
                _applyButton.setEnabled(false);
                new Thread(new Runnable() {
                    public void run() {
                applyRules();
                    }
                }).start();
            }
        });
        
        
        add(topPanel,BorderLayout.NORTH);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean arg0) {
        super.setEnabled(arg0);
        _applyButton.setEnabled(arg0);
        _checkButton.setEnabled(arg0);
    }
    
	/**
	 * 
	 *
	 */
	public void checkRules() {
	    Rule[] rules = getSelectedRules();
            for (int i = 0; i < rules.length; i++)
                rules[i].check();
    }
	
	/**
	 * 
	 *
	 */
	public void applyRules() {
	    Rule[] rules = getSelectedRules();
            for (int i = 0; i < rules.length; i++)
                rules[i].apply();
    }
	
	/**
	 * 
	 * @return
	 */
	protected Rule[] getSelectedRules()
	{
	    RuleCheckBox box = null;
	    ArrayList selectedRules = new ArrayList();
	    for(Iterator iter = _ruleCheckBoxes.iterator();iter.hasNext();)
	    {
	        box = (RuleCheckBox) iter.next();
	        if(box.isSelected())
	        {
	            selectedRules.add(box.getRule());
	        }
	    }
	    if(selectedRules.size()>0)
	        return (Rule[])selectedRules.toArray(new Rule[selectedRules.size()]);
	    return null;
	}
    
    
    public abstract Rule[] getRules();
    
    public abstract String getTabTitle();

}
