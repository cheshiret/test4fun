package com.active.qa.automation.web.testrunner.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tchen on 1/18/2016.
 */
public class Driverlauncher implements ActionListener {
  private static final long serialVersionUID = 1L;
  public boolean exit = false;
  JFrame mainFrame;
  JPanel panel = new JPanel();

  JCheckBox ormsSelection = new JCheckBox("Orms", true);
  JCheckBox webSelection = new JCheckBox("Web", false);
  JCheckBox failedOnly = new JCheckBox("Only failed cases", false);
  JCheckBox qa1Selection = new JCheckBox("QA1", true);
  JCheckBox qa2Selection = new JCheckBox("QA2", false);
  JPanel testcase = new JPanel(new GridLayout(2, 1));
  JPanel list = new JPanel();
  JPanel cases = new JPanel();
  JButton runNow = new JButton("Run Now");
  JButton cancel = new JButton("Cancel");
  JLabel emailLabel = new JLabel("Please enter your email address");
//	JLabel schemaLabel=new JLabel("Please verify schema");

  //JTextField emailAddressField = new JTextField("qaormstest@reserveamerica.com",20);
  JTextField emailAddressField;

  String[] priorities = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
  JLabel priorityLabel = new JLabel("Priority");
  JList priority = new JList(priorities);


  public Driverlauncher() {   // the constructor
    mainFrame = new JFrame("Automation Test");
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    emailAddressField = new JTextField(TestProperty.get("mail.to"), 20);
    priority.setSelectedIndex(6);
    //	setAlwaysOnTop(true);
//		mainFrame.setModal(true);
    mainFrame.setBounds(50, 50, 260, 200);
    Container contentPane = mainFrame.getContentPane();
    contentPane.add(panel);
    SpringLayout layout = new SpringLayout();
    panel.setLayout(layout);

    cases.add(ormsSelection);
    cases.add(webSelection);
    cases.add(qa1Selection);
    cases.add(qa2Selection);
    list.add(failedOnly);
    list.add(priorityLabel);
    list.add(priority);
    testcase.add(cases, BorderLayout.NORTH);
    testcase.add(list, BorderLayout.SOUTH);

    panel.add(emailLabel);
    layout.putConstraint(SpringLayout.WEST, failedOnly,
        20,
        SpringLayout.WEST, testcase);

    layout.putConstraint(SpringLayout.WEST, emailLabel,
        10,
        SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, emailLabel,
        5,
        SpringLayout.NORTH, contentPane);

    panel.add(emailAddressField);
    layout.putConstraint(SpringLayout.WEST, emailAddressField,
        10,
        SpringLayout.WEST, contentPane);
    layout.putConstraint(SpringLayout.NORTH, emailAddressField,
        5,
        SpringLayout.SOUTH, emailLabel);

    panel.add(testcase);
    layout.putConstraint(SpringLayout.WEST, testcase,
        5,
        SpringLayout.WEST, contentPane);

    layout.putConstraint(SpringLayout.NORTH, testcase,
        5,
        SpringLayout.SOUTH, emailAddressField);


    panel.add(runNow);
    layout.putConstraint(SpringLayout.WEST, runNow,
        10,
        SpringLayout.WEST, contentPane);

    layout.putConstraint(SpringLayout.NORTH, runNow,
        5,
        SpringLayout.SOUTH, testcase);

    panel.add(cancel);
    layout.putConstraint(SpringLayout.WEST, cancel,
        15,
        SpringLayout.EAST, runNow);
    layout.putConstraint(SpringLayout.NORTH, cancel,
        5,
        SpringLayout.SOUTH, testcase);

    runNow.addActionListener(this);
    runNow.setMnemonic('R');

    cancel.addActionListener(this);
    cancel.setMnemonic('C');

    ormsSelection.addActionListener(this);
    webSelection.addActionListener(this);
    qa1Selection.addActionListener(this);
    qa2Selection.addActionListener(this);
    failedOnly.addActionListener(this);


    panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    mainFrame.setVisible(true);
  }

  // now the basic event listeners
  public void actionPerformed(ActionEvent ae) {
    Object source = ae.getSource();
    if (source == runNow) {
      TestProperty.put("target_env", getEnv());
      TestProperty.put("mail.to", getEmailAddress());

      exit = true;
      mainFrame.dispose();
    } else if (source == cancel) {
      System.exit(0);
    } else if (source == ormsSelection) {
      ormsSelection.setSelected(true);
      webSelection.setSelected(false);
    } else if (source == webSelection) {
      ormsSelection.setSelected(false);
      webSelection.setSelected(true);
    } else if (source == qa1Selection) {
      qa1Selection.setSelected(true);
      qa2Selection.setSelected(false);
    } else if (source == qa2Selection) {
      qa1Selection.setSelected(false);
      qa2Selection.setSelected(true);
    }
  }

  public String getEmailAddress() {
    return emailAddressField.getText();
  }

  public int getPriority() {
    return priority.getSelectedIndex();
  }

  public String getTestSet() {
    String path = "";

    if (isOrms())
      path = "testCases.sanity.orms";
    else if (isWeb())
      path = "testCases.sanity.web";

    return path;
  }

  public String getEnv() {
    String env = "";

    if (qa1Selection.isSelected())
      env = "qa1";
    else if (qa2Selection.isSelected())
      env = "qa2";

    return env;
  }

  public boolean isOrms() {
    return ormsSelection.isSelected();
  }

  public boolean isWeb() {
    return webSelection.isSelected();
  }

  public boolean failedOnly() {
    return this.failedOnly.isSelected();
  }

  public void waitForExit() {
    while (!this.exit) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
    }
  }

  // and finally the main method
  public static void main(String[] args) {
    //new ScriptsLauncher();
  }
}