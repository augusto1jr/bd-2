package view.cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import database.ConexaoBD;

public class TelaPerfil extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable pedidosTable;
    private DefaultTableModel model;
    private int id_cliente;
    private JFrame parentFrame;

    public TelaPerfil(int id_cliente, String nome, String fone, String email, String senha, JFrame parentFrame) {
        this.id_cliente = id_cliente;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JPanel dadosPanel = new JPanel(new GridLayout(4, 1));
        dadosPanel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        dadosPanel.add(new JLabel("Nome: " + nome));
        dadosPanel.add(new JLabel("Telefone: " + fone));
        dadosPanel.add(new JLabel("Email: " + email));
        add(dadosPanel, BorderLayout.NORTH);

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Colunas editáveis: Data (1), Valor Total (2), Ações (3 e 4)
                return column == 1 || column == 2 || column == 3 || column == 4;
            }
        };

        model.addColumn("ID Pedido");
        model.addColumn("Data");
        model.addColumn("Valor Total");
        model.addColumn("Editar");
        model.addColumn("Excluir");

        pedidosTable = new JTable(model);
        pedidosTable.setRowHeight(30);
        
        // Configura renderizadores e editores para os botões
        pedidosTable.getColumn("Editar").setCellRenderer(new IconButtonRenderer("🖉"));
        pedidosTable.getColumn("Editar").setCellEditor(new EditButtonEditor(new JCheckBox()));
        
        pedidosTable.getColumn("Excluir").setCellRenderer(new IconButtonRenderer("🗑"));
        pedidosTable.getColumn("Excluir").setCellEditor(new DeleteButtonEditor(new JCheckBox()));

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
                model.addRow(new Object[]{idPedido, data.toString(), valor, "🖉", "🗑"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos do cliente.");
        }
    }

    class IconButtonRenderer extends JButton implements TableCellRenderer {
        public IconButtonRenderer(String icon) {
            setText(icon);
            setFont(new Font("Dialog", Font.PLAIN, 18));
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int row;
        private boolean isEditing = false;
        private Object[] originalValues = new Object[3]; // Para armazenar valores originais

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("✏️");
            button.setFont(new Font("Dialog", Font.PLAIN, 18));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value.toString());
            isPushed = true;
            
            if (!isEditing) {
                // Iniciar edição - salvar valores originais
                originalValues[0] = table.getValueAt(row, 0);
                originalValues[1] = table.getValueAt(row, 1);
                originalValues[2] = table.getValueAt(row, 2);
                button.setText("✔️"); // Muda para ícone de check
                isEditing = true;
            } else {
                // Finalizar edição - confirmar alterações
                int confirm = JOptionPane.showConfirmDialog(button, 
                    "Tem certeza que deseja atualizar este pedido?", "Confirmação", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = ConexaoBD.getConnection()) {
                        String sql = "UPDATE pedidos SET data_pedido = ?, valor_total = ? WHERE id_pedido = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        
                        // Obter novos valores da tabela
                        String novaData = table.getValueAt(row, 1).toString();
                        double novoValor = Double.parseDouble(table.getValueAt(row, 2).toString());
                        int idPedido = Integer.parseInt(table.getValueAt(row, 0).toString());
                        
                        stmt.setString(1, novaData);
                        stmt.setDouble(2, novoValor);
                        stmt.setInt(3, idPedido);
                        
                        int rowsAffected = stmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(button, "Pedido atualizado com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(button, "Erro ao atualizar pedido.");
                            // Reverter para valores originais
                            table.setValueAt(originalValues[1], row, 1);
                            table.setValueAt(originalValues[2], row, 2);
                        }
                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(button, "Erro ao atualizar pedido.");
                        // Reverter para valores originais
                        table.setValueAt(originalValues[1], row, 1);
                        table.setValueAt(originalValues[2], row, 2);
                    }
                } else {
                    // Reverter para valores originais se o usuário cancelar
                    table.setValueAt(originalValues[1], row, 1);
                    table.setValueAt(originalValues[2], row, 2);
                }
                
                button.setText("🖉"); // Volta para ícone de lápis
                isEditing = false;
            }
            
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Não precisamos fazer nada aqui, toda a lógica está no getTableCellEditorComponent
            }
            isPushed = false;
            return button.getText();
        }
    }

    class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int row;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("🗑");
            button.setFont(new Font("Dialog", Font.PLAIN, 18));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value.toString());
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int idPedido = (int) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(button, "Tem certeza que deseja deletar este pedido?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = ConexaoBD.getConnection()) {
                        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, idPedido);
                        stmt.executeUpdate();
                        model.removeRow(row);
                        JOptionPane.showMessageDialog(button, "Pedido deletado com sucesso!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(button, "Erro ao deletar pedido.");
                    }
                }
            }
            isPushed = false;
            return "🗑";
        }
    }
}