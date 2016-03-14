package com.active.qa.automation.web.test4fun.util.project;

import com.active.qa.automation.web.testapi.util.AutomationLogger;
import com.active.qa.automation.web.testapi.util.TestProperty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tchen on 1/18/2016.
 */
public class ScriptsLauncher implements ActionListener {

  //Get a RALogger Instance
  private static AutomationLogger logger = AutomationLogger.getInstance();

  private static final long serialVersionUID = 1L;

  public boolean exit = false;    //Used to quit program

  private String testCasePath = "";

  JFrame mainFrame;      //A Frame object

  JPanel panel = new JPanel();

  JCheckBox ormsSelection = new JCheckBox("Orms", true);

  JCheckBox webSelection = new JCheckBox("Web", false);

  JCheckBox listedInFile = new JCheckBox("Only cases listed in file", true);

  JCheckBox reportOnly = new JCheckBox("Report Only", false);

  JCheckBox qa1Selection = new JCheckBox("QA1", false);

  JCheckBox qa2Selection = new JCheckBox("QA2", false);

  JCheckBox qa3Selection = new JCheckBox("QA3", false);

  JCheckBox qa4Selection = new JCheckBox("QA4", false);

  JCheckBox qa5Selection = new JCheckBox("QA5", true);

  JCheckBox liveSelection = new JCheckBox("Live", false);

  JCheckBox sanitySelection = new JCheckBox("Sanity", true);

  JCheckBox regressionSelection = new JCheckBox("Regression", false);

  JCheckBox advancedSelection = new JCheckBox("Advanced", false);

  JCheckBox productionSelection = new JCheckBox("Production", false);

  JCheckBox supportScript = new JCheckBox("Support scripts", false);//added by pzhu

  JPanel testcase = new JPanel(new GridLayout(3, 1));   //Get a Panel use GridLayout

  JPanel testSuite = new JPanel();

  JPanel list = new JPanel();

  JPanel cases = new JPanel();

  JButton runNow = new JButton("Run Now");       //Run Button

  JButton cancel = new JButton("Cancel");        //Cancel Button

  JLabel emailLabel = new JLabel("Please enter your email address");  //A Label

  //	JLabel schemaLabel=new JLabel("Please verify schema");

  //	JTextField emailAddressField = new JTextField("qaormstest@reserveamerica.com",20);
  JTextField emailAddressField;   //Input text Field

  JTextField testSuitePath; //display and override test suite path

  /**
   * Construct method used to construct a GUI programme
   * this programme is a interface to run test cases
   */
  public ScriptsLauncher() { // the constructor
    mainFrame = new JFrame("Automation Test");
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    emailAddressField = new JTextField(TestProperty.getProperty("notification.to"), 35);
    testSuitePath = new JTextField(syncTestCasesPath(), 35);
    //		setAlwaysOnTop(true);
    //		mainFrame.setModal(true);
    mainFrame.setBounds(500, 200, 500, 320);
    Container contentPane = mainFrame.getContentPane();   //get a container
    contentPane.add(panel);
    SpringLayout layout = new SpringLayout();
    panel.setLayout(layout);      //set SpringLayout as Layout

    cases.add(ormsSelection);
    cases.add(webSelection);
    cases.add(qa1Selection);
    cases.add(qa2Selection);
    cases.add(qa3Selection);
    cases.add(qa4Selection);
    cases.add(qa5Selection);
    cases.add(liveSelection);

    testSuite.add(sanitySelection);
    testSuite.add(regressionSelection);
    testSuite.add(advancedSelection);
    testSuite.add(productionSelection);
    testSuite.add(supportScript);//added by pzhu

    list.add(listedInFile);
    list.add(reportOnly);

    testcase.add(cases, BorderLayout.NORTH);
    testcase.add(testSuite, BorderLayout.NORTH);
    testcase.add(list, BorderLayout.SOUTH);

    panel.add(emailLabel);
    layout.putConstraint(SpringLayout.WEST, listedInFile, 20, SpringLayout.WEST, testcase);

    layout.putConstraint(SpringLayout.WEST, emailLabel, 10, SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, emailLabel, 5, SpringLayout.NORTH, contentPane);

    panel.add(emailAddressField);
    layout.putConstraint(SpringLayout.WEST, emailAddressField, 10, SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, emailAddressField, 5, SpringLayout.SOUTH, emailLabel);

    panel.add(testcase);
    layout.putConstraint(SpringLayout.WEST, testcase, 5, SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, testcase, 5, SpringLayout.SOUTH, emailAddressField);

    panel.add(testSuitePath);
    layout.putConstraint(SpringLayout.WEST, testSuitePath, 5, SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, testSuitePath, 5, SpringLayout.SOUTH, testcase);

    panel.add(runNow);
    layout.putConstraint(SpringLayout.WEST, runNow, 10, SpringLayout.WEST, contentPane);

    layout.putConstraint(SpringLayout.NORTH, runNow, 5, SpringLayout.SOUTH, testSuitePath);

    panel.add(cancel);
    layout.putConstraint(SpringLayout.WEST, cancel, 15, SpringLayout.EAST, runNow);
    layout.putConstraint(SpringLayout.NORTH, cancel, 5, SpringLayout.SOUTH, testSuitePath);

    runNow.addActionListener(this);
    runNow.setMnemonic('R');

    cancel.addActionListener(this);
    cancel.setMnemonic('C');

    //Add action listener for some components
    ormsSelection.addActionListener(this);
    webSelection.addActionListener(this);
    qa1Selection.addActionListener(this);
    qa2Selection.addActionListener(this);
    qa3Selection.addActionListener(this);
    qa4Selection.addActionListener(this);
    qa5Selection.addActionListener(this);
    liveSelection.addActionListener(this);
    sanitySelection.addActionListener(this);
    regressionSelection.addActionListener(this);
    advancedSelection.addActionListener(this);
    productionSelection.addActionListener(this);
    listedInFile.addActionListener(this);
    reportOnly.addActionListener(this);
    supportScript.addActionListener(this);//added by pzhu

    panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    mainFrame.setVisible(true);
    testCasePath = testSuitePath.getText();
  }

  /**
   * Basic Event Listener to listen which GUI component is triggered
   */
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == runNow) {    //click runNow Button
      TestProperty.putProperty("target_env", getEnv());
      TestProperty.putProperty("notification.to", getEmailAddress());
      String logName = TestConstants.TIMESTAMP;
      testCasePath = testSuitePath.getText();

      if (isProduction()) {
        Util.loadLiveInformation();
      }

      if (productionSelection.isSelected()) {
        logName = "Production_Sanity_" + logName;
      }

      if (isOrms())
        logName = "Orms_" + logName;
      else
        logName = "Web_" + logName;

      logger.resetLogfileName(logName);

      exit = true;
      mainFrame.dispose();
    } else if (source == cancel) {   //click cancel Button
      System.exit(0);
    } else if (source == ormsSelection) {   //Orms checkBox is selected
      ormsSelection.setSelected(true);
      webSelection.setSelected(false);
    } else if (source == webSelection) {   //Web checkBox is selected
      ormsSelection.setSelected(false);
      webSelection.setSelected(true);
      reportOnly.setSelected(false);
    } else if (source == qa1Selection) {   //qa1 checkBox is selected
      qa1Selection.setSelected(true);
      qa2Selection.setSelected(false);
      qa3Selection.setSelected(false);
      qa4Selection.setSelected(false);
      qa5Selection.setSelected(false);
      liveSelection.setSelected(false);
    } else if (source == qa2Selection) {   //qa2 checkBox is selected
      qa2Selection.setSelected(true);
      qa1Selection.setSelected(false);
      qa3Selection.setSelected(false);
      qa4Selection.setSelected(false);
      qa5Selection.setSelected(false);
      liveSelection.setSelected(false);
    } else if (source == qa3Selection) {   //qa3 checkBox is selected
      qa3Selection.setSelected(true);
      qa1Selection.setSelected(false);
      qa2Selection.setSelected(false);
      qa4Selection.setSelected(false);
      qa5Selection.setSelected(false);
      liveSelection.setSelected(false);
    } else if (source == qa4Selection) {   //qa2 checkBox is selected
      qa4Selection.setSelected(true);
      qa1Selection.setSelected(false);
      qa2Selection.setSelected(false);
      qa3Selection.setSelected(false);
      qa5Selection.setSelected(false);
      liveSelection.setSelected(false);
    } else if (source == qa5Selection) {   //qa2 checkBox is selected
      qa5Selection.setSelected(true);
      qa4Selection.setSelected(false);
      qa1Selection.setSelected(false);
      qa2Selection.setSelected(false);
      qa3Selection.setSelected(false);
      liveSelection.setSelected(false);
    } else if (source == liveSelection) {   //live checkBox is selected
      qa1Selection.setSelected(false);
      qa2Selection.setSelected(false);
      qa3Selection.setSelected(false);
      qa4Selection.setSelected(false);
      qa5Selection.setSelected(false);
      liveSelection.setSelected(true);

      productionSelection.setSelected(true);
      sanitySelection.setSelected(false);
      regressionSelection.setSelected(false);

      reportOnly.setSelected(false);
    } else if (source == sanitySelection) {   //sanity checkBox is selected
      productionSelection.setSelected(false);
      sanitySelection.setSelected(true);
      regressionSelection.setSelected(false);
      advancedSelection.setSelected(false);

      reportOnly.setSelected(false);
      liveSelection.setSelected(false);
//			if(!qa1Selection.isSelected() && !qa2Selection.isSelected()) {
//			  	qa2Selection.setSelected(true);
//			}

    } else if (source == regressionSelection) {   //regression checkBox is selected
      productionSelection.setSelected(false);
      sanitySelection.setSelected(false);
      regressionSelection.setSelected(true);
      advancedSelection.setSelected(false);

      liveSelection.setSelected(false);
//			if(!qa1Selection.isSelected() && !qa2Selection.isSelected()) {
//			  	qa2Selection.setSelected(true);
//			}
    } else if (source == productionSelection) {   //production checkBox is selected
      productionSelection.setSelected(true);
      advancedSelection.setSelected(false);
      sanitySelection.setSelected(false);
      regressionSelection.setSelected(false);
      reportOnly.setSelected(false);
    } else if (source == advancedSelection) {   //production checkBox is selected
      advancedSelection.setSelected(true);
      productionSelection.setSelected(false);
      sanitySelection.setSelected(false);
      regressionSelection.setSelected(false);
      reportOnly.setSelected(false);
    } else if (source == reportOnly) {
      ormsSelection.setSelected(true);
      webSelection.setSelected(false);

      advancedSelection.setSelected(false);
      productionSelection.setSelected(false);
      regressionSelection.setSelected(true);
      sanitySelection.setSelected(false);

      liveSelection.setSelected(false);
//			if(!qa1Selection.isSelected() && !qa2Selection.isSelected()) {
//			  	qa2Selection.setSelected(true);
//			}
    }
    if (source == this.supportScript) {   //support script checkBox is selected, added by pzhu
      supportScript.setSelected(true);
      productionSelection.setSelected(false);
      advancedSelection.setSelected(false);
      sanitySelection.setSelected(false);
      regressionSelection.setSelected(false);
      reportOnly.setSelected(false);
    }

    testSuitePath.setText(syncTestCasesPath());
  }

  /**
   * Get Email address from the email text field
   *
   * @return
   */
  public String getEmailAddress() {
    return emailAddressField.getText();
  }

  /**
   * Initialize the test case path by judgeing the selected check box from GUI
   *
   * @return
   */
  private String syncTestCasesPath() {
    String path = "";

    String product = isOrms() ? "orms" : "web";
    if (productionSelection.isSelected())
      path = "testCases/production/" + product + "/";
    else if (reportOnly.isSelected()) {
      path = "testCases/regression/advanced/orms/order/ordercart/adjustfee/permitorder/";
    } else if (regressionSelection.isSelected()) {
      path = "testCases/regression/basic/" + product + "/";
    } else if (sanitySelection.isSelected()) {
      path = "testCases/sanity/" + product + "/";
    } else if (advancedSelection.isSelected()) {
      path = "testCases/regression/advanced/" + product + "/";
    } else if (supportScript.isSelected()) {  //added by pzhu for support scripts
      path = "supportscripts/";
    }

    return path;
  }

  /**
   * Get env information by judgeing the selected env checkBox
   *
   * @return
   */
  public String getEnv() {
    String env = "";

    if (qa1Selection.isSelected())
      env = "qa1";
    else if (qa2Selection.isSelected())
      env = "qa2";
    else if (qa3Selection.isSelected())
      env = "qa3";
    else if (qa4Selection.isSelected())
      env = "qa4";
    else if (qa5Selection.isSelected())
      env = "qa5";
    else if (liveSelection.isSelected())
      env = "live";

    return env;
  }

  /**
   * Check is Orms checkBox is selected
   *
   * @return
   */
  public boolean isOrms() {
    return ormsSelection.isSelected();
  }

  /**
   * Check is Web checkBox is selected
   *
   * @return
   */
  public boolean isWeb() {
    return webSelection.isSelected();
  }

  /**
   * Check if need to run production test cases in live environment
   *
   * @return - true only if the live check box is selected
   */
  public boolean isProduction() {
    return liveSelection.isSelected();
  }

  /**
   * Check is RunOnlyListed checkBox is selected
   *
   * @return
   */
  public boolean onlyListed() {
    return this.listedInFile.isSelected();
  }

  public String getTestCasesPath() {
    return testCasePath;
  }

  // and finally the main method
  public static void main(String[] args) {
    new ScriptsLauncher();
  }
}

