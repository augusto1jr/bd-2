package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.ConexaoBD;
import view.TelaInicial;

public class TelaListar extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable clientesTable;
    private DefaultTableModel model;
    private JFrame parentFrame;

    public TelaListar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        // Criação do modelo da tabela
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Apenas as colunas de ação são editáveis
                return column == 4 || column == 5;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Fone");
        model.addColumn("Email");
        model.addColumn("Editar");
        model.addColumn("Excluir");

        clientesTable = new JTable(model);
        clientesTable.setRowHeight(30);
        
        // Configura renderizadores e editores para os botões
        clientesTable.getColumn("Editar").setCellRenderer(new IconButtonRenderer("🖉"));
        clientesTable.getColumn("Editar").setCellEditor(new EditButtonEditor(new JCheckBox()));
        
        clientesTable.getColumn("Excluir").setCellRenderer(new IconButtonRenderer("🗑"));
        clientesTable.getColumn("Excluir").setCellEditor(new DeleteButtonEditor(new JCheckBox()));

        carregarClientes();

        // Configurações da tabela
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientesTable.setAutoCreateRowSorter(true);
        clientesTable.setShowGrid(false);
        clientesTable.setIntercellSpacing(new Dimension(0, 0));

        // Define larguras preferenciais das colunas
        clientesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        clientesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome
        clientesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Telefone
        clientesTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        clientesTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Editar
        clientesTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Excluir

        JScrollPane scrollPane = new JScrollPane(clientesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        add(scrollPane, BorderLayout.CENTER);

        // Botão de voltar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton voltarButton = new JButton("Voltar");
        voltarButton.setBackground(new Color(255, 255, 255));
        voltarButton.addActionListener(e -> {
            parentFrame.setContentPane(new TelaInicial(parentFrame));
            parentFrame.revalidate();
        });
        
        JButton novoClienteButton = new JButton("Novo Cliente");
        novoClienteButton.setBackground(new Color(255, 255, 255));
        novoClienteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaCadastro(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
        });
        bottomPanel.add(novoClienteButton);
        bottomPanel.add(voltarButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void carregarClientes() {
        model.setRowCount(0); // Limpa a tabela
        try (Connection conn = ConexaoBD.getConnection()) {
            String sql = "SELECT id_cliente, nome, fone, email FROM clientes ORDER BY nome";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String telefone = rs.getString("fone");
                String email = rs.getString("email");
                
                model.addRow(new Object[]{id, nome, telefone, email, "🖉", "🗑"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    // Renderizador de botões (igual ao da TelaPerfil)
    class IconButtonRenderer extends JButton implements TableCellRenderer {
        public IconButtonRenderer(String icon) {
            setText(icon);
            setFont(new Font("Dialog", Font.PLAIN, 16));
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

    // Editor para o botão Editar
    class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;

        public EditButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("🖉");
            button.setFont(new Font("Dialog", Font.PLAIN, 16));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                editarCliente(row);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        private void editarCliente(int row) {
            int id = (int) model.getValueAt(row, 0);
            String nome = (String) model.getValueAt(row, 1);
            String telefone = (String) model.getValueAt(row, 2);
            String email = (String) model.getValueAt(row, 3);

            JFrame editarFrame = new JFrame("Editar Cliente");
            editarFrame.setSize(400, 300);
            editarFrame.setLocationRelativeTo(parentFrame);
            editarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField nomeField = new JTextField(nome);
            JTextField telefoneField = new JTextField(telefone);
            JTextField emailField = new JTextField(email);
            
            panel.add(new JLabel("Nome:"));
            panel.add(nomeField);
            panel.add(new JLabel("Fone:"));
            panel.add(telefoneField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            
            JButton salvarBtn = new JButton("Salvar");
            salvarBtn.addActionListener(e -> {
                try (Connection conn = ConexaoBD.getConnection()) {
                    String sql = "UPDATE clientes SET nome=?, fone=?, email=? WHERE id_cliente=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nomeField.getText());
                    stmt.setString(2, telefoneField.getText());
                    stmt.setString(3, emailField.getText());
                    stmt.setInt(4, id);
                    
                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(editarFrame, "Cliente atualizado com sucesso!");
                        model.setValueAt(nomeField.getText(), row, 1);
                        model.setValueAt(telefoneField.getText(), row, 2);
                        model.setValueAt(emailField.getText(), row, 3);
                        editarFrame.dispose();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(editarFrame, "Erro ao atualizar cliente: " + ex.getMessage());
                }
            });
            
            JButton cancelarBtn = new JButton("Cancelar");
            cancelarBtn.addActionListener(e -> editarFrame.dispose());
            
            panel.add(salvarBtn);
            panel.add(cancelarBtn);
            
            editarFrame.getContentPane().add(panel);
            editarFrame.setVisible(true);
        }
    }

    // Editor para o botão Excluir
    class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;

        public DeleteButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("🗑");
            button.setFont(new Font("Dialog", Font.PLAIN, 16));
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                excluirCliente(row);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                    boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }

        private void excluirCliente(int row) {
            int id = (int) model.getValueAt(row, 0);
            String nome = (String) model.getValueAt(row, 1);
            
            int confirm = JOptionPane.showConfirmDialog(
                parentFrame, 
                "Tem certeza que deseja excluir o cliente " + nome + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = ConexaoBD.getConnection()) {
                    // Primeiro verifica se o cliente tem pedidos
                    String sqlVerifica = "SELECT COUNT(*) FROM pedidos WHERE id_cliente=?";
                    PreparedStatement stmtVerifica = conn.prepareStatement(sqlVerifica);
                    stmtVerifica.setInt(1, id);
                    ResultSet rs = stmtVerifica.executeQuery();
                    rs.next();
                    int count = rs.getInt(1);
                    
                    if (count > 0) {
                        int confirmPedidos = JOptionPane.showConfirmDialog(
                            parentFrame,
                            "Este cliente possui " + count + " pedido(s). Deseja excluir todos os pedidos também?",
                            "Pedidos Associados",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (confirmPedidos == JOptionPane.YES_OPTION) {
                            // Exclui os pedidos primeiro
                            String sqlPedidos = "DELETE FROM pedidos WHERE id_cliente=?";
                            PreparedStatement stmtPedidos = conn.prepareStatement(sqlPedidos);
                            stmtPedidos.setInt(1, id);
                            stmtPedidos.executeUpdate();
                        } else {
                            return; // Cancela a exclusão se o usuário não quiser excluir os pedidos
                        }
                    }
                    
                    // Exclui o cliente
                    String sqlCliente = "DELETE FROM clientes WHERE id_cliente=?";
                    PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente);
                    stmtCliente.setInt(1, id);
                    
                    int rows = stmtCliente.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(parentFrame, "Cliente excluído com sucesso!");
                        model.removeRow(row); // Remove a linha da tabela
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "Erro ao excluir cliente: " + ex.getMessage());
                }
            }
        }
    }
}