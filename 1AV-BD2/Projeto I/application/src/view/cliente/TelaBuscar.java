package view.cliente;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import database.ConexaoBD;
import view.TelaInicial;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class TelaBuscar extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField emailField;

	public TelaBuscar(JFrame parentFrame) {
		setLayout(null);
		
		JLabel emailLabel = new JLabel("Insira o email do usuário que deseja encontrar:");
		emailLabel.setBounds(80, 100, 331, 14);
		add(emailLabel);
		
		emailField = new JTextField();
		emailField.setBounds(174, 125, 89, 20);
		add(emailField);
		emailField.setColumns(10);
		
		JButton buscarButton = new JButton("Buscar");
		buscarButton.setBackground(new Color(255, 255, 255));
		buscarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscarCliente(parentFrame);
			}
		});
		buscarButton.setBounds(174, 147, 89, 23);
		add(buscarButton);
		
		JButton voltarButton = new JButton("< Voltar");
		voltarButton.setBackground(new Color(255, 255, 255));
		voltarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaInicial(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		voltarButton.setBounds(10, 11, 89, 23);
		add(voltarButton);

	}
	
	private void buscarCliente(JFrame parentFrame) {
	    String email = emailField.getText().trim();

	    if (email == null || email.trim().isEmpty()) {
	        JOptionPane.showMessageDialog(this, "E-mail não pode estar vazio.", "Entrada inválida", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try (Connection conn = ConexaoBD.getConnection()) {
	        String sql = "SELECT * FROM clientes WHERE email = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setString(1, email.trim());
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            int id_cliente = rs.getInt("id_cliente");
	            String nome = rs.getString("nome");
	            String fone = rs.getString("fone");
	            String senha = rs.getString("senha");
	            
				parentFrame.setContentPane(new TelaPerfil(id_cliente, nome, fone, email, senha, parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();

	        } else {
	            JOptionPane.showMessageDialog(this, "Cliente não encontrado.",
	                    "Busca", JOptionPane.INFORMATION_MESSAGE);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Erro ao buscar no banco de dados.");
	    }
	}

}
