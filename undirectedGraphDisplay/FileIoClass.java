
package undirectedGraphDisplay;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

/*
 * Used to save graphs to file for later use with the graph viewer tool.
 */
public class FileIoClass extends JPanel
                             implements ActionListener {
    static private final String newline = "\n";
    JButton openButton, saveButton;
    JTextArea log;
    JFileChooser fc;
    public FileIoClass() {
        super(new BorderLayout());
        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);

        //Create a file chooser
        fc = new JFileChooser();
 
    }

    public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(FileIoClass.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                log.append("Opening: " + file.getName() + "." + newline);
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        //Handle save button action.
        } else if (e.getSource() == saveButton) {
            int returnVal = fc.showSaveDialog(FileIoClass.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                log.append("Saving: " + file.getName() + "." + newline);
            } else {
                log.append("Save command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FileIoClass.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    ///////////////////////////////////////////////////// OpenAction
    class OpenAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            //... Open a file dialog.
            int retval = fc.showOpenDialog(FileIoClass.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
                File file = fc.getSelectedFile();

                //This is where a real application would open the file.
                log.append("Opening: " + file.getName() + "." + newline);
            }
        }
    }
    
    class ProcessAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            //... Open a file dialog.
            int retval = fc.showOpenDialog(FileIoClass.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                //... The user selected a file, get it, use it.
                File file = fc.getSelectedFile();

                //This is where a real application would open the file.
                log.append("Opening: " + file.getName() + "." + newline);
            }
        }
    }

    class SaveAction implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            
        	int returnVal = fc.showSaveDialog(FileIoClass.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                log.append("Saving: " + file.getName() + "." + newline);
            } else {
                log.append("Save command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        }
    }    
}
