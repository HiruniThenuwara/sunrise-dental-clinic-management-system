package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.sunrise.controller.ClinicException;
import com.sunrise.controller.ErrorHandler;
import com.sunrise.controller.LoginController;

/**
 * Function 1 of the brief - user authentication.
 *
 * Only the screen is written here. The checking of the username and the
 * password is done by LoginController (MVC).
 */
public class LoginView extends JFrame {

	private static final long serialVersionUID = 1L;

	private final LoginController controller = new LoginController();

	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JCheckBox chkShowPassword;
	private JLabel lblError;
	private JButton btnLogin;

	public LoginView() {
		setTitle("Sunrise Dental Clinic - Staff Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(470, 560);
		setResizable(false);

		buildScreen();
		UITheme.center(this);
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Sunrise Dental Clinic", "Appointment and Patient Management System"),
				BorderLayout.NORTH);
		main.add(buildForm(), BorderLayout.CENTER);

		JLabel footer = UITheme.hint("  CIS6003 Advanced Programming - version 1.0");
		footer.setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));
		main.add(footer, BorderLayout.SOUTH);

		setContentPane(main);
	}

	private JPanel buildForm() {
		JPanel card = UITheme.card();
		card.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;

		card.add(UITheme.heading("Staff Login"), c);

		c.gridy++;
		card.add(UITheme.hint("Please enter your username and password"), c);

		c.gridwidth = 1;
		c.gridy++;
		card.add(UITheme.label("Username"), c);

		c.gridy++;
		c.gridwidth = 2;
		txtUsername = UITheme.textField(18);
		card.add(txtUsername, c);

		c.gridy++;
		c.gridwidth = 1;
		card.add(UITheme.label("Password"), c);

		c.gridy++;
		c.gridwidth = 2;
		txtPassword = new JPasswordField(18);
		txtPassword.setFont(UITheme.NORMAL_FONT);
		txtPassword.setBorder(UITheme.fieldBorder());
		card.add(txtPassword, c);

		c.gridy++;
		chkShowPassword = new JCheckBox("Show password");
		chkShowPassword.setFont(UITheme.SMALL_FONT);
		chkShowPassword.setBackground(UITheme.CARD);
		chkShowPassword.setFocusPainted(false);
		chkShowPassword.addActionListener(e -> showOrHidePassword());
		card.add(chkShowPassword, c);

		c.gridy++;
		lblError = new JLabel(" ");
		lblError.setFont(UITheme.SMALL_FONT);
		lblError.setForeground(UITheme.DANGER);
		lblError.setPreferredSize(new Dimension(320, 34));
		card.add(lblError, c);

		c.gridy++;
		btnLogin = UITheme.primaryButton("Login");
		btnLogin.addActionListener(e -> doLogin());
		card.add(btnLogin, c);

		// the Enter key also logs in
		KeyAdapter enterKey = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					doLogin();
				}
			}
		};
		txtUsername.addKeyListener(enterKey);
		txtPassword.addKeyListener(enterKey);

		JPanel holder = new JPanel(new GridBagLayout());
		holder.setBackground(UITheme.BACKGROUND);
		holder.add(card);
		return holder;
	}

	private void showOrHidePassword() {
		if (chkShowPassword.isSelected()) {
			txtPassword.setEchoChar((char) 0);
		} else {
			txtPassword.setEchoChar('•');
		}
	}

	/**
	 * The login runs in a background thread with SwingWorker, so the window
	 * does not freeze while the server is answering.
	 */
	private void doLogin() {
		lblError.setText(" ");
		btnLogin.setEnabled(false);
		btnLogin.setText("Please wait...");

		final String username = txtUsername.getText();
		final char[] password = txtPassword.getPassword();

		new SwingWorker<String, Void>() {

			@Override
			protected String doInBackground() throws Exception {
				return controller.login(username, password);
			}

			@Override
			protected void done() {
				btnLogin.setEnabled(true);
				btnLogin.setText("Login");

				try {
					String fullName = get();
					openMainMenu(fullName);

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					showError(cause.getMessage());

					if (cause instanceof ClinicException) {
						ErrorHandler.log((ClinicException) cause);
					}
				}
			}
		}.execute();
	}

	private void openMainMenu(String fullName) {
		MainMenuView menu = new MainMenuView(fullName);
		menu.setVisible(true);
		dispose();
	}

	public void showError(String message) {
		// the message can be long, so it is shown in two lines
		lblError.setText("<html><div style='width:300px'>" + message.replace("\n", "<br>") + "</div></html>");
		lblError.setForeground(UITheme.DANGER);
		txtPassword.setText("");
		txtPassword.requestFocus();
	}

	public void clear() {
		txtUsername.setText("");
		txtPassword.setText("");
		lblError.setText(" ");
		lblError.setForeground(new Color(0, 0, 0, 0));
	}
}
