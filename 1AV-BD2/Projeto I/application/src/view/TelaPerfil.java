package view;

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

        // Painel de dados do cliente - agora com GridBagLayout para mais controle
        JPanel dadosPanel = new JPanel(new GridBagLayout());
        dadosPanel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels com os dados do cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        dadosPanel.add(new JLabel("Nome: " + nome), gbc);
        
        gbc.gridy++;
        dadosPanel.add(new JLabel("Fone: " + fone), gbc);
        
        gbc.gridy++;
        dadosPanel.add(new JLabel("Email: " + email), gbc);

        // Painel para os botões de ação (editar e excluir)
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        // Botão de Editar
        JButton editarClienteBtn = new JButton("🖉 Editar");
        editarClienteBtn.setBackground(new Color(70, 130, 180)); // Azul steel
        editarClienteBtn.setForeground(Color.WHITE);
        editarClienteBtn.setFocusPainted(false);
        editarClienteBtn.addActionListener(e -> editarCliente(nome, fone, email, senha));
        buttonsPanel.add(editarClienteBtn);
        
        // Botão de Excluir
        JButton excluirClienteBtn = new JButton("🗑 Excluir");
        excluirClienteBtn.setBackground(new Color(220, 20, 60)); // Vermelho
        excluirClienteBtn.setForeground(Color.WHITE);
        excluirClienteBtn.setFocusPainted(false);
        excluirClienteBtn.addActionListener(e -> excluirCliente());
        buttonsPanel.add(excluirClienteBtn);

        // Adicionando o painel de botões ao dadosPanel
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        dadosPanel.add(buttonsPanel, gbc);

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
        
        // Adicionando painel com botão de novo pedido
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton novoPedidoButton = new JButton("Novo Pedido");
        novoPedidoButton.setBackground(new Color(255, 255, 255));
        novoPedidoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame pedidoFrame = new JFrame("Cadastro de Pedido");
				pedidoFrame.setSize(400,300);
				pedidoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				pedidoFrame.setContentPane(new TelaPedido(id_cliente, pedidoFrame));
				pedidoFrame.setVisible(true);
			}
		});
        bottomPanel.add(novoPedidoButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        JButton voltarButton = new JButton("Voltar");
        voltarButton.setBackground(new Color(255, 255, 255));
        voltarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaBuscar(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
        });
        bottomPanel.add(voltarButton);
    }
    
    
    
    // Método para edição do cliente
    private void editarCliente(String nome, String fone, String email, String senha) {
        JFrame editarFrame = new JFrame("Editar Cliente");
        editarFrame.setSize(400, 300);
        editarFrame.setLocationRelativeTo(parentFrame);
        editarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomeField = new JTextField(nome);
        JTextField foneField = new JTextField(fone);
        JTextField emailField = new JTextField(email);
        JPasswordField senhaField = new JPasswordField(senha);
        
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Fone:"));
        panel.add(foneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        
        JButton salvarBtn = new JButton("Salvar");
        salvarBtn.setBackground(Color.WHITE);
        salvarBtn.addActionListener(e -> {
            try (Connection conn = ConexaoBD.getConnection()) {
                String sql = "UPDATE clientes SET nome=?, fone=?, email=?, senha=? WHERE id_cliente=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nomeField.getText());
                stmt.setString(2, foneField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, new String(senhaField.getPassword()));
                stmt.setInt(5, id_cliente);
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(editarFrame, "Dados atualizados com sucesso!");
                    editarFrame.dispose();
                    // Atualiza a tela com os novos dados
                    parentFrame.setContentPane(new TelaPerfil(id_cliente, nomeField.getText(), 
                        foneField.getText(), emailField.getText(), 
                        new String(senhaField.getPassword()), parentFrame));
                    parentFrame.revalidate();
                } else {
                    JOptionPane.showMessageDialog(editarFrame, "Nenhum dado foi alterado.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editarFrame, "Erro ao atualizar cliente: " + ex.getMessage());
            }
        });
        
        JButton cancelarBtn = new JButton("Cancelar");
        cancelarBtn.setBackground(Color.WHITE);
        cancelarBtn.addActionListener(e -> editarFrame.dispose());
        
        panel.add(salvarBtn);
        panel.add(cancelarBtn);
        
        editarFrame.add(panel);
        editarFrame.setVisible(true);
    }
    
    // Método para exclusão do cliente
    private void excluirCliente() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja excluir este cliente e todos os seus pedidos?", 
            "Confirmação de Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = ConexaoBD.getConnection()) {
                // Primeiro exclui os pedidos do cliente
                String sqlPedidos = "DELETE FROM pedidos WHERE id_cliente=?";
                PreparedStatement stmtPedidos = conn.prepareStatement(sqlPedidos);
                stmtPedidos.setInt(1, id_cliente);
                stmtPedidos.executeUpdate();
                
                // Depois exclui o cliente
                String sqlCliente = "DELETE FROM clientes WHERE id_cliente=?";
                PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente);
                stmtCliente.setInt(1, id_cliente);
                
                int rows = stmtCliente.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                    parentFrame.setContentPane(new TelaBuscar(parentFrame));
                    parentFrame.revalidate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage());
            }
        }
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