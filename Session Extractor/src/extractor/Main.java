package extractor;

import extractor.threads.ApiThread;
import extractor.threads.WebThread;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;

public class Main {

    public static boolean IsLogging = false;
    private static Form mainform;
    public static void main(String[] args) {
        mainform = new Form();
        try {
            mainform.showForm();

            ActionListener buttonClick = e -> {
                try {
                    if (IsLogging)
                    {
                        JOptionPane.showMessageDialog(null,"Wait till the requested login completes","Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (mainform.getUsername().equals("Username") && mainform.getPassword().equals("Password"))
                    {
                        JOptionPane.showMessageDialog(null,"Please enter your username and password!","Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }else if (mainform.getPassword().isEmpty() || mainform.getUsername().isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"Please enter your username and password!","Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainform.setResultTextFieldEnable(false);
                    mainform.setResultLabel("Logging..");
                    IsLogging = true;
                    if (mainform.getSelectedType().compareTo("Web") == 0)
                    {
                        WebThread thread = new WebThread(mainform);
                        thread.start();
                    }else {
                        ApiThread thread = new ApiThread(mainform);
                        thread.start();
                    }
                }catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
                }


            };

            mainform.registerOnLogin(buttonClick);
        }catch (Exception ex)
        {
            if (ex instanceof NullPointerException)
            {
                NullPointerException tempEx = (NullPointerException)ex;
                JOptionPane.showMessageDialog(null, "Error [".concat(tempEx.getMessage().concat("]")),"Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}