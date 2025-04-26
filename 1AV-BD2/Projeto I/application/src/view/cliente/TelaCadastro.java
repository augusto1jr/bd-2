package view.cliente;

import view.TelaInicial;

import javax.swing.*;

import database.ConexaoBD;

import java.awt.event.*;
import java.sql.*;
import java.awt.Color;


public class TelaCadastro extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JTextField foneField;
	private JTextField emailField;
	private JPasswordField senhaField;
	private JFrame parentFrame;
	
	
	public TelaCadastro(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		setLayout(null);

		JButton cadastrarButton = new JButton("Cadastrar");
		cadastrarButton.setBackground(new Color(255, 255, 255));
		cadastrarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cadastrarCliente();
			}
		});
		cadastrarButton.setBounds(120, 174, 98, 23);
		add(cadastrarButton);
		
		JButton voltarButton = new JButton("Voltar");
		voltarButton.setBackground(new Color(255, 255, 255));
		voltarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaInicial(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		voltarButton.setBounds(225, 174, 89, 23);
		add(voltarButton);

		JLabel nomeLabel = new JLabel("Nome:");
		nomeLabel.setBounds(149, 71, 39, 14);
		add(nomeLabel);
		
		JLabel foneLabel = new JLabel("Fone:");
		foneLabel.setBounds(149, 96, 39, 14);
		add(foneLabel);

		JLabel emailLabel = new JLabel("Email:");
		emailLabel.setBounds(149, 121, 39, 14);
		add(emailLabel);

		JLabel senhaLabel = new JLabel("Senha:");
		senhaLabel.setBounds(149, 146, 39, 14);
		add(senhaLabel);

		nameField = new JTextField();
		nameField.setBounds(198, 68, 86, 20);
		add(nameField);
		nameField.setColumns(10);
		
		foneField = new JTextField();
		foneField.setBounds(198, 93, 86, 20);
		add(foneField);
		foneField.setColumns(10);

		emailField = new JTextField();
		emailField.setBounds(198, 118, 86, 20);
		add(emailField);
		emailField.setColumns(10);

		senhaField = new JPasswordField();
		senhaField.setBounds(198, 143, 86, 20);
		add(senhaField);
		senhaField.setColumns(10);
	}

	
	private void cadastrarCliente() {
	    String nome = nameField.getText().trim();
	    String fone = foneField.getText().trim();
	    String email = emailField.getText().trim();
	    String senha = new String(senhaField.getPassword()).trim();

	    // Verifica campos vazios
	    if (nome.isEmpty() || fone.isEmpty() || email.isEmpty() || senha.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.",
	                "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Verifica nome com apenas espaços ou dígitos
	    if (!nome.matches("^[\\p{L} .'-]+$")) {
	        JOptionPane.showMessageDialog(this, "Digite um nome válido.",
	                "Nome inválido", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Verifica se o telefone contém apenas números
	    if (!fone.matches("\\d{8,15}")) {
	        JOptionPane.showMessageDialog(this, "Digite um telefone válido (apenas números, entre 8 e 15 dígitos).",
	                "Telefone inválido", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Validação básica de e-mail
	    if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$")) {
	        JOptionPane.showMessageDialog(this, "Digite um e-mail válido.",
	                "E-mail inválido", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Verifica tamanho da senha
	    if (senha.length() < 6) {
	        JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres.",
	                "Senha fraca", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try (Connection conn = ConexaoBD.getConnection()) {
	        String sql = "INSERT INTO clientes (nome, fone, email, senha) VALUES (?, ?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setString(1, nome);
	        stmt.setString(2, fone);
	        stmt.setString(3, email);
	        stmt.setString(4, senha);

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
