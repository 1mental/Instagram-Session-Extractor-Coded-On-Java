package extractor;
import extractor.instagram.SessionType;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;


public final class Form extends  JFrame
{
    private JButton loginButton;

    private JComboBox<String> comboBox;
    private JLabel usernameLabel, passwordLabel, resultLabel, typeLabel;

    private ActionListener onLogin;
    private JTextField userTextField, resultTextField;
    private JPasswordField passwdTextField;
    private boolean isShowed = false;
    public Form()
    {
        super();
        this.InitComponents();
        this.setSize(250,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null); // Our GUI Will be simple so no need for layouts
        this.setResizable(false);
        this.setTitle("#Mental");
    }

    public void showForm() throws Exception
    {
        if (isShowed)
            throw new Exception("Form Already Visible");

        this.setVisible(true);
        isShowed = true;
    }

    // A method to initialize the components
    private void InitComponents()
    {
        this.usernameLabel = new JLabel("Username: ");
        this.passwordLabel = new JLabel("Password: ");
        this.resultLabel = new JLabel("...");
        this.loginButton = new JButton("Login");
        this.userTextField = new JTextField("Username");
        this.resultTextField = new JTextField("SessionID");
        this.passwdTextField = new JPasswordField("Password");
        this.typeLabel = new JLabel("Session: ");
        comboBox = new JComboBox<>();

        usernameLabel.setBounds(10,10, 100,20);

        passwordLabel.setBounds(10,45, 100,20);

        userTextField.setBounds(80,12,150,20);
        userTextField.setHorizontalAlignment(JTextField.CENTER);

        passwdTextField.setBounds(80,48,150,20);
        passwdTextField.setHorizontalAlignment(JTextField.CENTER);

        loginButton.setBounds(10,120,100,25);

        resultLabel.setBounds(120,123,100,20);

        resultTextField.setBounds(10,240,225,20);
        resultTextField.setHorizontalAlignment(JTextField.CENTER);
        resultTextField.setEnabled(false);

        typeLabel.setBounds(10,220,150,20);

        comboBox.addItem("Web");
        comboBox.addItem("Api");
        comboBox.setBounds(10,80,220,25);

        this.add(comboBox);
        this.add(typeLabel);
        this.add(usernameLabel);
        this.add(passwordLabel);
        this.add(resultLabel);
        this.add(userTextField);
        this.add(passwdTextField);
        this.add(loginButton);
        this.add(resultTextField);

    }

    public void registerOnLogin(ActionListener action) throws NullPointerException
    {
        if (action == null)
            throw new NullPointerException("Action cannot be Null.");



        this.onLogin = action;
        this.assignEvent();
    }


    private void assignEvent()
    {
        this.loginButton.addActionListener(this.onLogin);
    }

    public void setResultLabel(String text)
    {
        this.resultLabel.setText(text);
    }

    public void setResultTextField(String text)
    {
        this.resultTextField.setText(text);
    }

    public String getUsername()
    {
        return userTextField.getText();
    }

    public String getPassword()
    {
        return new String(passwdTextField.getPassword());
    }

    public void setResultTextFieldEnable(boolean flag)
    {
        this.resultTextField.setEnabled(flag);
    }

    public void setSessionType(SessionType type)
    {
        typeLabel.setText("Session: ".concat(type.name()));
    }

    public String getSelectedType()
    {
        return (String) comboBox.getSelectedItem();
    }
}
