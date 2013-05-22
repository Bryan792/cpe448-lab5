/*
 * Team 6
 * Andrew Nguyen
 * Bryan Ching
 * Matt Crussell
 * CPE 448 Bioinformatics
 * NaiveSuffixTree
 */

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;

import java.io.File;
import java.io.FileWriter;

@SuppressWarnings("serial")
public class InputDialog extends JDialog
{
  /*
   * CONSTANTS
   */
  private final int DIALOG_HEIGHT = 800, DIALOG_WIDTH = 500;

  /*
   * GUI Components
   */
  private Container mPane;
  private JTextField mFile, mStartPos, mEndPos, mWinSize, mShiftIncr, mFile2,
      mRangeStart, mRangeEnd, mType2, mFilter;
  private JTextArea mDisplayArea;
  private JCheckBox mUseSlidingWindow;
  private JComboBox mOptsBox, mTypesBox;

  public InputDialog()
  {
    initialize();
  }

  private void initialize()
  {

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    setResizable(false);
    setLocationRelativeTo(null);

    mPane = this.getContentPane();
    mPane.setLayout(new BoxLayout(mPane, BoxLayout.Y_AXIS));
    mPane.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);

    mFile = new JTextField(20);
    mFile2 = new JTextField(20);
    mStartPos = new JTextField(20);
    mEndPos = new JTextField(20);

    mRangeStart = new JTextField(20);
    mRangeEnd = new JTextField(20);
    mType2 = new JTextField(20);
    mFilter = new JTextField(20);

    mUseSlidingWindow = new JCheckBox("Use Sliding Window", false);

    mDisplayArea = new JTextArea();
    mWinSize = new JTextField(20);
    mShiftIncr = new JTextField(20);

    JPanel fastaFileField = prepareFileField(mFile);
    JPanel gffFileField = prepareFileField(mFile2);

    JPanel posField = prepareParamControls(mStartPos, mEndPos, mWinSize,
        mShiftIncr, mUseSlidingWindow);

    getContentPane().add(fastaFileField);
    mPane.add(gffFileField);
    mPane.add(new JLabel("Filter Size"));
    mPane.add(mFilter);

    String[] searchOpts = { "All", "Specify Range", "To Start Codon"  };
    mOptsBox = new JComboBox(searchOpts);
    mPane.add(mOptsBox);
    mPane.add(new JLabel("Start"));
    mPane.add(mRangeStart);
    mPane.add(new JLabel("End"));
    mPane.add(mRangeEnd);

    String[] searchType = { "Specific String", "Min Repeat Size" };
    mTypesBox = new JComboBox(searchType);
    mPane.add(mTypesBox);
    mPane.add(new JLabel("Search String/Repeat Length"));
    mPane.add(mType2);

    // mPane.add(posField);

    // mPane.add(mDisplayArea);
    JScrollPane scrollDisplay = new JScrollPane(mDisplayArea);
    scrollDisplay.setPreferredSize(new Dimension(200, 300));
    mPane.add(scrollDisplay);

    mPane.add(initControls());

    mPane.validate();
  }

  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          InputDialog dialog = new InputDialog();
          dialog.setVisible(true);

        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

  /*
   * Creates and returns a JPanel containing sub components that make up the
   * input file selection section
   */
  private JPanel prepareFileField(JTextField fileField)
  {
    JPanel fastaFileField = new JPanel();

    fastaFileField.setLayout(new FlowLayout(FlowLayout.LEADING));

    fastaFileField.add(new JLabel("Select Input File:"));
    fastaFileField.add(fileField);
    fastaFileField.add(prepareBrowseButton(fileField));

    return fastaFileField;
  }

  /*
   * Creates and returns a JButton that can be used to browse for any given
   * file. The input JTextField is associated with the returned button such that
   * when the browse button is used to select a file, the full file name is
   * written to the input JTextField
   */
  private JButton prepareBrowseButton(final JTextField fileField)
  {
    JButton fileBrowse = new JButton("Browse");

    fileBrowse.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(chooser);

        if (returnVal == JFileChooser.CANCEL_OPTION)
        {
          System.out.println("cancelled");
        }

        else if (returnVal == JFileChooser.APPROVE_OPTION)
        {
          File fastaFile = chooser.getSelectedFile();
          fileField.setText(fastaFile.getAbsolutePath());
        }

        else
        {
          System.out.println("Encountered Unknown Error");
          System.exit(0);
        }
      }
    });

    return fileBrowse;
  }

  /*
   * Creates and returns a JPanel containing all of the parameter JTextFields.
   * Additionally adds an ItemListener to the JCheckBox to hide/show the
   * JTextFields for the sliding window parameters.
   */
  private JPanel prepareParamControls(JTextField posStart, JTextField posEnd,
      final JTextField winSize, final JTextField shiftIncr, JCheckBox useSlide)
  {
    JPanel controlField = new JPanel(), startField = new JPanel(), endField = new JPanel(), checkBoxField = new JPanel(), windowField = new JPanel(), shiftField = new JPanel();
    final JPanel slideFields = new JPanel();

    /*
     * Position based parameters
     */
    startField.setLayout(new FlowLayout(FlowLayout.LEADING));
    startField.add(new JLabel("Start Position:"));
    startField.add(posStart);

    endField.setLayout(new FlowLayout(FlowLayout.LEADING));
    endField.add(new JLabel("End Position:"));
    endField.add(posEnd);

    /*
     * Fancy check box for making window parameters visible/invisible
     */
    checkBoxField.setLayout(new FlowLayout(FlowLayout.LEADING));
    checkBoxField.add(useSlide);
    useSlide.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
          slideFields.setVisible(true);
        }
        else if (e.getStateChange() == ItemEvent.DESELECTED)
        {
          slideFields.setVisible(false);
          winSize.setText("");
          shiftIncr.setText("");
        }

        mPane.setVisible(true);
      }
    });

    /*
     * Window Parameters
     */
    windowField.setLayout(new FlowLayout(FlowLayout.LEADING));
    windowField.add(new JLabel("Window Size"));
    windowField.add(winSize);

    shiftField.setLayout(new FlowLayout(FlowLayout.LEADING));
    shiftField.add(new JLabel("Window Shift"));
    shiftField.add(shiftIncr);

    slideFields.setLayout(new BoxLayout(slideFields, BoxLayout.Y_AXIS));
    slideFields.add(windowField);
    slideFields.add(shiftField);
    slideFields.setVisible(false);

    /*
     * Putting all parameter inputs together
     */
    controlField.setLayout(new BoxLayout(controlField, BoxLayout.Y_AXIS));
    controlField.add(startField);
    controlField.add(endField);
    controlField.add(checkBoxField);
    controlField.add(slideFields);

    return controlField;
  }

  /*
   * Creates and returns a JPanel containing all of the controls available on
   * this dialog window. This includes the "Run," "Save," and "Quit" buttons.
   */
  public JPanel initControls()
  {
    JPanel dialogControls = new JPanel();

    dialogControls.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

    dialogControls.add(createRunButton());
    dialogControls.add(createSaveButton());
    dialogControls.add(createQuitButton());

    dialogControls.setAlignmentX(Component.CENTER_ALIGNMENT);

    return dialogControls;
  }

  /*
   * Creates and returns a JButton that executes the appropriate code. The code
   * right now is extremely basic (it doesn't run anything) and output is
   * written to mDisplayArea, a JTextArea in the middle (ish) of this dialog
   * window
   */
  private JButton createRunButton()
  {
    JButton runButton = new JButton("Run");

    runButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (mFile.getText().equals(""))
        {
          JOptionPane.showMessageDialog(null, "No FASTA file was selected",
              "Invalid File", JOptionPane.ERROR_MESSAGE);
        }
        else if (mFile2.getText().equals(""))
        {
          JOptionPane.showMessageDialog(null, "No gff file was selected",
              "Invalid File", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
          String sequence = FileReader.readFastaFile(mFile.getText());

          switch (mOptsBox.getSelectedIndex())
          {
          case 2:
            sequence = sequence.substring(
                Integer.valueOf(mRangeStart.getText()) - 1,
                Integer.valueOf(mRangeEnd.getText()));
            break;
          case 1:
            int startHere = MRNAFinder.findSmallestMRNA(mFile2.getText());
            //int startHere = 1;
            sequence = sequence.substring(
                Math.max(0,startHere - Integer.valueOf(mRangeStart.getText()) - 1),
                startHere - 1);
            break;
          case 0:
            break;
          }

          switch (mTypesBox.getSelectedIndex())
          {
          case 0:
            mDisplayArea.setText(NaiveSuffixTree.find(sequence,
                mType2.getText()));
            break;
          case 1:
            mDisplayArea.setText(NaiveSuffixTree.run(sequence,
                Integer.valueOf(mType2.getText()),
                Integer.valueOf(mFilter.getText())));
            break;
          }

        }
      }
    });

    return runButton;
  }

  /*
   * Creates and returns a JButton that allows the user to select a file for the
   * contents of the JTextArea to be written to.
   */
  private JButton createSaveButton()
  {
    JButton saveButton = new JButton("Save");

    saveButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (mDisplayArea.getText().equals(""))
        {
          JOptionPane.showMessageDialog(null, "No output to save",
              "Empty output", JOptionPane.ERROR_MESSAGE);
        }

        else
        {
          JFileChooser chooser = new JFileChooser();
          int returnVal = chooser.showSaveDialog(mPane);

          if (returnVal == JFileChooser.APPROVE_OPTION)
          {
            try
            {
              FileWriter writer = new FileWriter(chooser.getSelectedFile());
              writer.write(mDisplayArea.getText());
              writer.close();
            } catch (java.io.IOException ioErr)
            {
              JOptionPane.showMessageDialog(null,
                  "Encountered unknown error when saving output",
                  "Unable to save output", JOptionPane.ERROR_MESSAGE);
            }
          }

          else if (returnVal == JFileChooser.ERROR_OPTION)
          {
            JOptionPane.showMessageDialog(null,
                "Encountered unknown error when saving output",
                "Unable to save output", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    return saveButton;
  }

  /*
   * Self explanatory
   */
  private JButton createQuitButton()
  {
    JButton quitButton = new JButton("Quit");

    quitButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        dispose(); // closes the dialog window
        return;
      }
    });

    return quitButton;
  }
}
