package extractor.threads;

import extractor.Form;
import extractor.Main;
import extractor.instagram.*;

import javax.swing.JOptionPane;

public class ApiThread extends Thread
{
    private final Form mainform;

    public ApiThread(Form mainform)
    {
        super();
        this.mainform = mainform;
    }


    @Override
    public void run()
    {
        try {
            APISession session;
            if ((session = InstagramAPI.LoginAPI(mainform.getUsername(),mainform.getPassword())) == null)
            {
                Main.IsLogging = false;
                mainform.setResultLabel("Error");
                JOptionPane.showMessageDialog(null, String.format("Unhandled Error [%s]", InstagramAPI.getLastError()),"Error", JOptionPane.ERROR_MESSAGE);

                return;
            }
            mainform.setResultLabel("Extracted!");
            mainform.setResultTextFieldEnable(true);
            mainform.setResultTextField(session.getSession());
            mainform.setSessionType(session.getSessionType());

        }catch (InvalidLoginException ex)
        {
            mainform.setResultLabel("Error!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);

        }
        catch (CheckpointException ex)
        {
            mainform.setResultLabel("Checkpoint!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Warning", JOptionPane.WARNING_MESSAGE);
        }catch (BannedAccountException ex)
        {
            mainform.setResultLabel("Disabled!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.WARNING_MESSAGE);
        }
        catch (NullPointerException ex)
        {
            mainform.setResultLabel("Runtime Error!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.WARNING_MESSAGE);
        }
        finally {
            Main.IsLogging = false;
        }
    }
}
