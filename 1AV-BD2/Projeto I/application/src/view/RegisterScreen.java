package view;

import javax.swing.*;

import database.ConexaoDB;

import java.awt.event.*;
import java.sql.*;

public class RegisterScreen extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JTextField emailField;
	private JPasswordField passwordField;
	private JFrame parentFrame;

	public RegisterScreen(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		setLayout(null);

		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registerUser();
			}
		});
		registerButton.setBounds(99, 153, 89, 23);
		add(registerButton);

		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(136, 71, 39, 14);
		add(nameLabel);

		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setBounds(136, 97, 39, 14);
		add(emailLabel);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(110, 125, 65, 14);
		add(passwordLabel);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new LoginScreen(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		cancelButton.setBounds(197, 153, 89, 23);
		add(cancelButton);

		nameField = new JTextField();
		nameField.setBounds(177, 68, 86, 20);
		add(nameField);
		nameField.setColumns(10);

		emailField = new JTextField();
		emailField.setBounds(177, 94, 86, 20);
		add(emailField);
		emailField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(177, 122, 86, 20);
		add(passwordField);
		passwordField.setColumns(10);
	}

	private void registerUser() {
		String name = nameField.getText();
		String email = emailField.getText();
		String password = new String(passwordField.getPassword()).trim();
		
	    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
	        JOptionPane.showMessageDialog
	        (this, "Por favor, preencha todos os campos.", "Campos obrigatórios",
	        		JOptionPane.WARNING_MESSAGE);
	        return; 
	    }

		try (Connection conn = ConexaoDB.getConnection()) {
			String sql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, password);

			int resultado = stmt.executeUpdate();
			if (resultado > 0) {
				JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
			} else {
				JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao salvar no banco de dados!");
		}
	}
}
