package view;

import javax.swing.*;

import database.ConexaoDB;

import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField emailField;
	private JPasswordField passwordField; 

	public LoginScreen(JFrame parentFrame) {
		setLayout(null);

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginUser();
			}
		});
		loginButton.setBounds(123, 147, 70, 23);
		add(loginButton);

		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new RegisterScreen(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		registerButton.setBounds(200, 147, 90, 23);
		add(registerButton);

		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setBounds(122, 84, 50, 14);
		add(emailLabel);

		emailField = new JTextField();
		emailField.setBounds(180, 81, 100, 20);
		add(emailField);
		emailField.setColumns(10);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(100, 112, 70, 14);
		add(passwordLabel);

		passwordField = new JPasswordField();
		passwordField.setBounds(180, 109, 100, 20);
		add(passwordField);
		passwordField.setColumns(10);
		
		
	}
	
	private void loginUser() {
	    String email = emailField.getText().trim();
	    String password = new String(passwordField.getPassword()).trim();

	    if (email.isEmpty() || password.isEmpty()) {
	        JOptionPane.showMessageDialog
	        (this, "Preencha o email e a senha.", "Campos obrigatórios",
	        		JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try (Connection conn = ConexaoDB.getConnection()) {
	        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setString(1, email);
	        stmt.setString(2, password);

	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            String nome = rs.getString("nome");
	            JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + nome + "!");
	        } else {
	            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Erro de login", JOptionPane.ERROR_MESSAGE);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados.");
	    }
	}

}
