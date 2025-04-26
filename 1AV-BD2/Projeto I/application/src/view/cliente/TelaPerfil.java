package view.cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

import database.ConexaoBD;

public class TelaPerfil extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable pedidosTable;
    private DefaultTableModel model;
    private int id_cliente;

    public TelaPerfil(int id_cliente, String nome, String fone, String email, String senha, JFrame parentFrame) {
        this.id_cliente = id_cliente;
        setLayout(new BorderLayout());

        // Painel com dados do cliente
        JPanel dadosPanel = new JPanel(new GridLayout(3, 1));
        dadosPanel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        dadosPanel.add(new JLabel("Nome: " + nome));
        dadosPanel.add(new JLabel("Telefone: " + fone));
        dadosPanel.add(new JLabel("Email: " + email));
        add(dadosPanel, BorderLayout.NORTH);

        // Modelo da tabela
        model = new DefaultTableModel();
        model.addColumn("ID Pedido");
        model.addColumn("Data");
        model.addColumn("Valor Total");

        pedidosTable = new JTable(model);
        pedidosTable.setRowHeight(25);

        carregarPedidos();

        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pedidos"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarPedidos() {
        model.setRowCount(0);
        try (Connection conn = ConexaoBD.getConnection()) {
            String sql = "SELECT id_pedido, data_pedido, valor_total FROM pedidos WHERE id_cliente = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id_cliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                Date data = rs.getDate("data_pedido");
                double valor = rs.getDouble("valor_total");
                model.addRow(new Object[]{idPedido, data.toString(), String.format("R$ %.2f", valor)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos do cliente.");
        }
    }
}