/**
 Copyright (c) 2010, SESAR Joint Undertaking
 ============================================
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the names of the SESAR Joint Undertaking nor the names of its contributors may be used to endorse or promote products derived from this specification without specific prior written permission.


 DISCLAIMER

 THIS SPECIFICATION IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 ===========================================
 Editorial note: this license is an instance of the BSD license template as provided by the Open Source Initiative:
 http://www.opensource.org/licenses/bsd-license.php

 Details on the SJU and its members: http://www.sesarju.eu/players/members
 **/

package eurocontrol.swim.model.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import eurocontrol.swim.model.gui.common.AbstractTab;
import eurocontrol.swim.model.gui.common.EAEvent;
import eurocontrol.swim.model.gui.common.EAEventManager;
import eurocontrol.swim.model.gui.common.EAListener;
import eurocontrol.swim.model.rules.aixm.AIXM51_GenerateApplicationSchema;
import eurocontrol.swim.model.sparx.EAConnection;
import eurocontrol.swim.model.sparx.EAConstants;
import eurocontrol.swim.model.util.SpringUtilities;
import eurocontrol.swim.model.util.constants.AIRMConstants;
import eurocontrol.swim.model.util.constants.ToolConstants;

/**
 * @author hlepori
 */
public class Mainframe extends JFrame implements EAListener, EAConstants, ToolConstants, AIRMConstants{

    protected JTextField _localFileTextField = new JTextField(30);
    protected JTextField _DBMSRepositoryTextField = new JTextField(30);
    protected JTextField _userNameTextField = new JTextField(10);
    protected JPasswordField _passwordField = new JPasswordField(10);   
    protected JFileChooser _fileChooser; 
    protected JButton _openUMLFileButton = new JButton("Load UML model");
    protected JTabbedPane _mainTabbedPane = new JTabbedPane();
    private JTextArea _logTextArea = new JTextArea();
    private JTextArea _errorWarningTextArea = new JTextArea();
       
    protected String _EApath;
    
    protected AbstractTab[] _tabs = new AbstractTab[] { 
            //new AIRMTab(),
            //new NSV11b_To_AIRM_TraceabilityTab(),
            new PhysicalModelTab(),
            //new MiscellaneousTab()
            };
    
    
    /**
     * 
     */
    public Mainframe() {
        super();
        setTitle("ATM Data Models Processing Tool - @ 2013 Eurocontrol");
        setPreferredSize(new Dimension(1024, 728));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialise();
        buildGUI();
        EAEventManager.getInstance().registerListener(this);
    }
    
    
    /**
     * 
     */
    private void initialise() {
                
        // Add window listener to eap properly close the EA connection when closing the application
        addWindowListener(new WindowAdapter() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            public void windowClosing(WindowEvent arg0) {
                EAEventManager.getInstance().fireEAEvent(this,"Closing EA connection ..." );
                EAConnection.getInstance().getRepository().CloseFile();
                EAConnection.getInstance().getRepository().Exit();
            }
        });
        
        _openUMLFileButton.setEnabled(false);
        
        _localFileTextField.setEditable(false);
        
        _DBMSRepositoryTextField.addKeyListener(new KeyAdapter() {
            
            /* (non-Javadoc)
             * @see java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
             */
            public void keyReleased(KeyEvent arg0) {
                _openUMLFileButton.setEnabled((_localFileTextField.getText().length()>0)||(_DBMSRepositoryTextField.getText().length()>0));
            }
        });
        
        _fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
        _fileChooser.setName("Please select an EA project");
        _fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File arg0) {
                return arg0.isDirectory() ||arg0.getName().endsWith(".eap");
            }

            public String getDescription() {
                return "EA Project (*.eap)";
            }
        });    
        
        _mainTabbedPane.setEnabled(false);
        
        _logTextArea.setEditable(false);
        _logTextArea.setRows(10);
        _logTextArea.setText("Console \n" + SEPARATOR);
        
        _errorWarningTextArea.setEditable(false);
        _errorWarningTextArea.setRows(10);
        _errorWarningTextArea.setText("Errors and Warnings \n" + SEPARATOR);
        
        _openUMLFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (
                    _localFileTextField.getText().length() > 0) {
                    _EApath = _localFileTextField.getText();
                    _openUMLFileButton.setEnabled(false);
                    EAConnection.getInstance().openUMLModel(true, _EApath,
                            null, null);
                    _mainTabbedPane.setEnabled(true);
                    setTabsEnabled(true);
                }
                else
                {
                    JPanel loginPanel = new JPanel(new SpringLayout());
                    loginPanel.add(new JLabel("User name"));
                    loginPanel.add(_userNameTextField);
                    loginPanel.add(new JLabel("Password"));
                    loginPanel.add(_passwordField);
                    SpringUtilities.makeCompactGrid(loginPanel,2,2,0,0,5,5);
                                        
                	

                    Object[] options = {"Connect","Cancel"};
                    
                    int n = JOptionPane.showOptionDialog(Mainframe.this,
                            loginPanel,
                            "Connecting to DB server",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,     //do not use a custom Icon
                            options,  //the titles of buttons
                            options[0]); //default button title
                   
                    if (n==0)
                    {
                                _EApath = _DBMSRepositoryTextField.getText();
                                _openUMLFileButton.setEnabled(false);
                                EAConnection.getInstance().openUMLModel(false,_EApath,_userNameTextField.getText(),new String(_passwordField.getPassword()));
                                _mainTabbedPane.setEnabled(true);
                                setTabsEnabled(true);
                    }
                    
                }
            }
        });
     
        setTabsEnabled(false);
    }




    /**
     * 
     */
    private void buildGUI() {   
        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);
                
        ///////////////////////////////
        // build the panel used for the selection of the UML model
        ///////////////////////////////
        JPanel northPanel = new JPanel(new BorderLayout());        
        JPanel selectProjectPanel = new JPanel(new SpringLayout());
        
        JLabel localFileLabel = new JLabel("Local *.eap file");
        JLabel DBMSRepositoryLabel = new JLabel("DBMS repository");
        
        JButton openFileChooser = new JButton("...");
        openFileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int result = _fileChooser.showOpenDialog(null);
                
                if (result == JFileChooser.OPEN_DIALOG) {
            	    //_fileChooser.getSelectedFile();
                    _localFileTextField.setText(_fileChooser.getSelectedFile().getPath());
                    _openUMLFileButton.setEnabled(true);
                }
            }
        });
        
        
        JButton defaultAIRMPathButton = new JButton("AIRM");
        defaultAIRMPathButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                _DBMSRepositoryTextField.setText("DBType=1;Connect=Provider=SQLOLEDB.1;Integrated Security=SSPI;Persist Security Info=False;User ID=admin;Initial Catalog=SPARX;Data Source=hhbruc001\\hhintlive");
                _openUMLFileButton.setEnabled(true);
            }
        });
        
        JPanel openUMLFileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openUMLFileButtonPanel.add(_openUMLFileButton);
        
        selectProjectPanel.add(localFileLabel);
        selectProjectPanel.add(Box.createHorizontalGlue());
        
        selectProjectPanel.add(_localFileTextField);
        selectProjectPanel.add(openFileChooser);

        selectProjectPanel.add(Box.createHorizontalGlue());
        selectProjectPanel.add(Box.createHorizontalGlue());
        
        selectProjectPanel.add(DBMSRepositoryLabel);
        selectProjectPanel.add(Box.createHorizontalGlue());
        
        selectProjectPanel.add(_DBMSRepositoryTextField);
        selectProjectPanel.add(defaultAIRMPathButton);
        
        selectProjectPanel.add(Box.createHorizontalGlue());
        selectProjectPanel.add(Box.createHorizontalGlue());
        
        selectProjectPanel.add(openUMLFileButtonPanel);
        selectProjectPanel.add(Box.createHorizontalGlue());
        
        SpringUtilities.makeCompactGrid(selectProjectPanel,7,2,3,3,20,5);
        northPanel.add(selectProjectPanel, BorderLayout.WEST);
        northPanel.setBorder(new TitledBorder("Select EA File"));
        
        mainPanel.add(northPanel, BorderLayout.NORTH);
        
        
        /////////////////
        // The tabbed pane
        /////////////////
        addTabsToGUI();
        
        ////////////////////
        // Log area  & WARNING/ERROR area
        ////////////////////

        JSplitPane logSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(_logTextArea), new JScrollPane(_errorWarningTextArea));        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _mainTabbedPane, logSplitPane);
        splitPane.setDividerLocation(400);
        mainPanel.add(splitPane,BorderLayout.CENTER);
    }
    
    
    /* (non-Javadoc)
     * @see eurocontrol.swim.model.gui.EAListener#processEAEvent(eurocontrol.swim.model.gui.EAEvent)
     */
    public void processEAEvent(EAEvent event) {
        String text = _logTextArea.getText() + "\n" + event.getText();
        System.out.println(event.getText());
        _logTextArea.setText(text);
        
        if(event.getText().startsWith(MESSAGE_ERROR) ||event.getText().startsWith(MESSAGE_WARNING))
        {
            processWarningErrorEvent(event);
        }
      
        
        // update GUI elements
        if(event.getSource() instanceof EAConnection)
        {
            if(event.getText().equals(MESSAGE_DB_CONNECTION_FAILED))
            {
                setTabsEnabled(false);
                _openUMLFileButton.setEnabled(false);             
            }
        }
    }
    
    /**
     * 
     * @param event
     */
    private void processWarningErrorEvent(EAEvent event) {
        String text = _errorWarningTextArea.getText() + "\n" + event.getText();
        _errorWarningTextArea.setText(text);
    }

    
    /**
     * 
     *
     */
    protected void addTabsToGUI()
    {  
        for (int i = 0; i< _tabs.length;i++)
        {
            _mainTabbedPane.addTab(_tabs[i].getTabTitle(),_tabs[i]);   
        }
    }
    

    
    /**
     * 
     * @param enabled
     */
    public void setTabsEnabled(boolean enabled) 
    {
        for (int i = 0; i< _tabs.length;i++)
        {
            _tabs[i].setEnabled(enabled);
        }
    }
    

}
