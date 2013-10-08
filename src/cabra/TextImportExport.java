/*package cabra;

import javax.swing.*;
import java.awt.event.*;

public class TextImportExport {
 * 
    class TextImportListener implements ActionListener{
        
        private static final int WIDTH = 300;
        private static final int HEIGHT = 300;
        
        @Override
                public void actionPerformed(ActionEvent e){
                    JDialog dialog = new JDialog(gui.getFrame(), "Text Import");
                    dialog.setContentPane(new DialogPanel());
                    dialog.setModal(true);
                    dialog.setSize(WIDTH,HEIGHT);
                    Utils.centerComponent(dialog,gui.getFrame());
                    dialog.setVisible(true);
                }
        */
        /**
         * JPanel for the file choosing dialog
         */
        /*class DialogPanel extends JPanel{
             public DialogPanel(){
                setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.insets = new Insets(10,10,10,10);
                c.fill = GridBagConstraints.BOTH;
                
                //instructions label
                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = 2;
                JLabel label = new JLabel("Please select a file. BLAH BLAH");
                add(label,c);
                
                //file choosing button
                c.gridx = 0;
                c.gridy = 1;
                c.gridwidth = 1;
                JButton button = new JButton("Choose a file");  
                button.addActionListener(null);
                add(button,c);
                
                //file name label
                c.gridx = 1;
                c.gridy = 1;
                c.gridwidth = 1;
                label = new JLabel("C:/file/dfdfdfdfdfdfdfdf");
                add(label,c);
                
                //project name label
                c.gridx = 0;
                c.gridy = 2;
                c.gridwidth = 1;
                label = new JLabel("Name of project");
                add(label,c);
                
                //project name field
                c.gridx = 1;
                c.gridy = 2;
                c.gridwidth = 1;
                JTextField textField = new JTextField("hi",5); 
                add(textField,c);
                
                //OK button
                c.gridx = 0;
                c.gridy = 3;
                c.gridwidth = 1;
                button = new JButton("OK");
                button.addActionListener(null);
                add(button,c);
            }
        }
    }
   
    class TextExportListener implements ActionListener{
        @Override
                public void actionPerformed(ActionEvent e){
            
                }
    }    
}*/
