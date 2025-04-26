package view.cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.ConexaoBD;

public class TelaPedido extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField dataField;
	private JTextField valorField;
	private int id_cliente;
	private JFrame parentFrame;

	public TelaPedido(int id_cliente, JFrame parentFrame) {
        this.id_cliente = id_cliente;
        this.parentFrame = parentFrame;
		setLayout(null);
		
		JLabel dataLabel = new JLabel("Data do pedido:");
		dataLabel.setBounds(110, 82, 95, 14);
		add(dataLabel);
		
		JLabel valorLabel = new JLabel("Valor do pedido:");
		valorLabel.setBounds(110, 110, 95, 14);
		add(valorLabel);
		
		dataField = new JTextField();
		dataField.setBounds(208, 79, 86, 20);
		add(dataField);
		dataField.setColumns(10);
		
		valorField = new JTextField();
		valorField.setBounds(208, 107, 86, 20);
		add(valorField);
		valorField.setColumns(10);
		
		JButton cadastrarPedidoButton = new JButton("Cadastrar Pedido");
		cadastrarPedidoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cadastrarPedido(id_cliente);
			}
		});
		cadastrarPedidoButton.setBackground(new Color(255, 255, 255));
		cadastrarPedidoButton.setBounds(133, 151, 153, 23);
		add(cadastrarPedidoButton);
		}
		
	private void cadastrarPedido(int id_cliente) {
	    String dataPedido = dataField.getText().trim();
	    String valorText = valorField.getText().trim();

	    // Verifica campos vazios
	    if (dataPedido.isEmpty() || valorText.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.",
	                "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Verifica se a data está no formato correto (ano-mês-dia)
	    if (!dataPedido.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
	        JOptionPane.showMessageDialog(this, "Digite a data no formato YYYY-MM-DD.",
	                "Data inválida", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    // Verifica se o valor é um número válido (permite decimais)
	    double valorPedido;
	    try {
	        valorPedido = Double.parseDouble(valorText);
	        if (valorPedido < 0) {
	            JOptionPane.showMessageDialog(this, "O valor deve ser positivo.",
	                    "Valor inválido", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "Digite um valor numérico válido.",
	                "Valor inválido", JOptionPane.WARNING_MESSAGE);
	        return;
	    }

	    try (Connection conn = ConexaoBD.getConnection()) {
	        String sql = "INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES (?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, id_cliente);
	        stmt.setString(2, dataPedido);
	        stmt.setDouble(3, valorPedido);

	        int resultado = stmt.executeUpdate();
	        if (resultado > 0) {
	            JOptionPane.showMessageDialog(this, "Pedido cadastrado com sucesso!");
	        } else {
	            JOptionPane.showMessageDialog(this, "Erro ao cadastrar pedido!");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Erro ao salvar no banco de dados!");
	    }
	}


}
