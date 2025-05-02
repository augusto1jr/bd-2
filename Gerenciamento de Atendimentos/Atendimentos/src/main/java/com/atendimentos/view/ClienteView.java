package com.atendimentos.view;

import com.atendimentos.model.Cliente;
import com.atendimentos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        add(createTopo(), BorderLayout.NORTH);
        add(new JScrollPane(createTabelaClientes()), BorderLayout.CENTER);
        add(createRodape(), BorderLayout.SOUTH);
    }

    private JPanel createTopo() {
        JPanel topo = new JPanel(new BorderLayout());

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> ((MainView) SwingUtilities.getWindowAncestor(this)).showMainMenu());

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.addActionListener(e -> buscarClientes());

        painelBusca.add(new JLabel("Buscar:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);

        topo.add(btnVoltar, BorderLayout.WEST);
        topo.add(painelBusca, BorderLayout.CENTER);

        return topo;
    }

    private JTable createTabelaClientes() {
        String[] colunas = {"ID", "Nome", "Email", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return tabelaClientes;
    }

    private JPanel createRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnCadastrar = new JButton("Novo Cliente");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());

        rodape.add(btnCadastrar);
        rodape.add(btnEditar);
        rodape.add(btnExcluir);

        return rodape;
    }

    public void carregarClientes() {
        tableModel.setRowCount(0);
        clienteService.listarTodos().forEach(cliente ->
            tableModel.addRow(new Object[]{
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone()
            })
        );
    }

    private void buscarClientes() {
        String termo = txtBusca.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        clienteService.listarTodos().stream()
            .filter(c -> c.getNome().toLowerCase().contains(termo) ||
                         c.getEmail().toLowerCase().contains(termo))
            .forEach(c -> tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getEmail(), c.getTelefone()
            }));
    }

    private void cadastrarCliente() {
        // Cria o painel do formulário
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        // Campos do formulário
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefone = new JTextField();

        // Adiciona os componentes ao painel
        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Telefone:"));
        panel.add(txtTelefone);

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
            if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica se email já existe
            if (clienteService.emailExiste(txtEmail.getText(), null)) {
                JOptionPane.showMessageDialog(this, "Este email já está em uso!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica se telefone já existe (se foi informado)
            if (!txtTelefone.getText().isBlank() &&
                clienteService.telefoneExiste(txtTelefone.getText(), null)) {
                JOptionPane.showMessageDialog(this, "Este telefone já está cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cria o novo cliente
            Cliente novoCliente = new Cliente();
            novoCliente.setNome(txtNome.getText());
            novoCliente.setEmail(txtEmail.getText());
            novoCliente.setTelefone(txtTelefone.getText());

            try {
                // Salva no banco de dados
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

        // Cria o painel do formulário
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Campos do formulário com valores atuais
        JTextField txtNome = new JTextField(cliente.getNome());
        JTextField txtEmail = new JTextField(cliente.getEmail());
        JTextField txtTelefone = new JTextField(cliente.getTelefone());

        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Telefone:"));
        panel.add(txtTelefone);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Editar Cliente ID: " + id,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Validações básicas
            if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica se email já existe para outro cliente
            if (clienteService.emailExiste(txtEmail.getText(), id)) {
                JOptionPane.showMessageDialog(this, "Este email já está em uso por outro cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verifica se telefone já existe para outro cliente (se foi informado)
            if (!txtTelefone.getText().isBlank() &&
                clienteService.telefoneExiste(txtTelefone.getText(), id)) {
                JOptionPane.showMessageDialog(this, "Este telefone já está cadastrado para outro cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Atualiza os dados do cliente
            cliente.setNome(txtNome.getText());
            cliente.setEmail(txtEmail.getText());
            cliente.setTelefone(txtTelefone.getText());

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
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir o cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            clienteService.excluir(id);
            carregarClientes();
        }
    }
}
