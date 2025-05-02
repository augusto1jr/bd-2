package com.pedidos.view;

import com.pedidos.model.Cliente;
import com.pedidos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

@Component
public class ClienteView extends JPanel {
    
    private final ClienteService clienteService;
    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private JTextField txtBusca;
    
    @Autowired
    public ClienteView(ClienteService clienteService) {
        this.clienteService = clienteService;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel Superior (Busca + Voltar)
        JPanel panelSuperior = createPanelSuperior();
        
        // Tabela de Clientes
        tabelaClientes = createClientTable();
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        
        // Painel Inferior (Botões)
        JPanel panelInferior = createPanelInferior();
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel createPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Botão Voltar
        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> ((MainView)SwingUtilities.getWindowAncestor(this)).showMainMenu());
        
        // Painel de Busca
        JPanel panelBusca = new JPanel();
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("\uD83D\uDD0E");
        btnBuscar.addActionListener(this::buscarClientes);
        
        panelBusca.add(new JLabel("Buscar:"));
        panelBusca.add(txtBusca);
        panelBusca.add(btnBuscar);
        
        panel.add(btnVoltar, BorderLayout.WEST);
        panel.add(panelBusca, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTable createClientTable() {
        String[] colunas = {"ID", "Nome", "Telefone", "Login"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        return table;
    }
    
    private JPanel createPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnNovo = new JButton("Novo Cliente");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        
        btnNovo.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        
        panel.add(btnNovo);
        panel.add(btnEditar);
        panel.add(btnExcluir);
        
        return panel;
    }
    
    
    public void carregarClientes() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Cliente> clientes = clienteService.listarTodos();
        for (Cliente cliente : clientes) {
            tableModel.addRow(new Object[]{
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getLogin()
            });
        }
    }

    
    private void cadastrarCliente() {
        // Cria um painel para o formulário
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        // Campos do formulário
        JTextField txtNome = new JTextField();
        JTextField txtTelefone = new JTextField();
        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        
        // Adiciona os componentes ao painel
        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Telefone:"));
        panel.add(txtTelefone);
        panel.add(new JLabel("Login:"));
        panel.add(txtLogin);
        panel.add(new JLabel("Senha:"));
        panel.add(txtSenha);
        
        // Exibe o diálogo
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Cadastrar Novo Cliente",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Se o usuário clicou em OK
        if (result == JOptionPane.OK_OPTION) {
            // Validações básicas
            if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e login são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica se login já existe
            if (clienteService.loginExists(txtLogin.getText(), null)) {
                JOptionPane.showMessageDialog(this, "Este login já está em uso!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica se telefone já existe (se foi informado)
            if (!txtTelefone.getText().isBlank() && 
                clienteService.telefoneExists(txtTelefone.getText(), null)) {
                JOptionPane.showMessageDialog(this, "Este telefone já está cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Cria o novo cliente
            Cliente novoCliente = new Cliente();
            novoCliente.setNome(txtNome.getText());
            novoCliente.setTelefone(txtTelefone.getText());
            novoCliente.setLogin(txtLogin.getText());
            novoCliente.setSenha(new String(txtSenha.getPassword()));
            
            try {
                // Salva o cliente no banco de dados
                clienteService.salvar(novoCliente);
                
                // Atualiza a tabela
                carregarClientes();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Cliente cadastrado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Erro ao cadastrar cliente: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    
    private void buscarClientes(ActionEvent e) {
        String termo = txtBusca.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        
        clienteService.listarTodos().stream()
            .filter(c -> c.getNome().toLowerCase().contains(termo) || 
                        c.getLogin().toLowerCase().contains(termo))
            .forEach(c -> tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getTelefone(), c.getLogin()
            }));
    }
    
    
    private void editarCliente() {
        int row = tabelaClientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém o ID do cliente selecionado
        Long id = (Long) tableModel.getValueAt(row, 0);
        
        // Busca o cliente atual no banco de dados
        Cliente cliente = clienteService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        // Cria o formulário de edição
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        // Campos do formulário com os valores atuais
        JTextField txtNome = new JTextField(cliente.getNome());
        JTextField txtTelefone = new JTextField(cliente.getTelefone());
        JTextField txtLogin = new JTextField(cliente.getLogin());
        JPasswordField txtSenha = new JPasswordField(cliente.getSenha());
        
        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Telefone:"));
        panel.add(txtTelefone);
        panel.add(new JLabel("Login:"));
        panel.add(txtLogin);
        panel.add(new JLabel("Senha:"));
        panel.add(txtSenha);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Editar Cliente ID: " + id,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            // Validações básicas
            if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e login são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica se login já existe para outro cliente
            if (clienteService.loginExists(txtLogin.getText(), id)) {
                JOptionPane.showMessageDialog(this, "Este login já está em uso por outro cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica se telefone já existe para outro cliente (se foi informado)
            if (!txtTelefone.getText().isBlank() && 
                clienteService.telefoneExists(txtTelefone.getText(), id)) {
                JOptionPane.showMessageDialog(this, "Este telefone já está cadastrado para outro cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Atualiza os dados do cliente
            cliente.setNome(txtNome.getText());
            cliente.setTelefone(txtTelefone.getText());
            cliente.setLogin(txtLogin.getText());
            cliente.setSenha(new String(txtSenha.getPassword()));
            
            try {
                // Salva as alterações
                clienteService.atualizar(id, cliente);
                
                // Atualiza a tabela
                carregarClientes();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Cliente atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Erro ao atualizar cliente: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    
    private void excluirCliente() {
        int row = tabelaClientes.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Confirmar exclusão do cliente?", "Confirmação", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            clienteService.excluir(id);
            carregarClientes();
        }
    }
}