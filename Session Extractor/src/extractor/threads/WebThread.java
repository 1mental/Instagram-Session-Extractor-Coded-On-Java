package extractor.threads;
import extractor.Form;
import extractor.Main;
import extractor.instagram.InstagramAPI;
import extractor.instagram.WebSession;
import extractor.instagram.InvalidLoginException;
import extractor.instagram.SecuredException;
import extractor.instagram.CheckpointException;
import javax.swing.JOptionPane;

public class WebThread extends  Thread{

    private final Form mainform;

    public WebThread(Form mainform)
    {
        super();
        this.mainform = mainform;
    }

    @Override
    public void run()
    {
        try {
            WebSession session;
            if ((session = InstagramAPI.LoginWeb(mainform.getUsername(),mainform.getPassword())) == null)
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

        }catch (SecuredException ex)
        {
            mainform.setResultLabel("2FA");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Warning", JOptionPane.WARNING_MESSAGE);
        }
        catch (CheckpointException ex)
        {
            mainform.setResultLabel("Checkpoint!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Warning", JOptionPane.WARNING_MESSAGE);
        }catch (NullPointerException ex)
        {
            mainform.setResultLabel("Runtime Error!");
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.WARNING_MESSAGE);
        }

        finally {
            Main.IsLogging = false;
        }
    }



}
