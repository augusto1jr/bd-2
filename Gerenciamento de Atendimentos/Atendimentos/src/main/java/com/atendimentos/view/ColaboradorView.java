package com.atendimentos.view;

import com.atendimentos.model.Colaborador;
import com.atendimentos.service.ColaboradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Component
public class ColaboradorView extends JPanel {

    private final ColaboradorService colaboradorService;
    private JTable tabelaColaboradores;
    private DefaultTableModel tableModel;
    private JTextField txtBusca;

    @Autowired
    public ColaboradorView(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createTopo(), BorderLayout.NORTH);
        add(new JScrollPane(createTabelaColaboradores()), BorderLayout.CENTER);
        add(createRodape(), BorderLayout.SOUTH);
    }

    private JPanel createTopo() {
        JPanel topo = new JPanel(new BorderLayout());

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> ((MainView) SwingUtilities.getWindowAncestor(this)).showMainMenu());

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.addActionListener(e -> buscarColaboradores());

        painelBusca.add(new JLabel("Buscar:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);

        topo.add(btnVoltar, BorderLayout.WEST);
        topo.add(painelBusca, BorderLayout.CENTER);

        return topo;
    }

    private JTable createTabelaColaboradores() {
        String[] colunas = {"ID", "Nome", "Cargo", "Email"};
        tableModel = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabelaColaboradores = new JTable(tableModel);
        tabelaColaboradores.setFont(new Font("Arial", Font.PLAIN, 14));
        tabelaColaboradores.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabelaColaboradores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return tabelaColaboradores;
    }

    private JPanel createRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnCadastrar = new JButton("Novo Colaborador");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnCadastrar.addActionListener(e -> cadastrarColaborador());
        btnEditar.addActionListener(e -> editarColaborador());
        btnExcluir.addActionListener(e -> excluirColaborador());

        rodape.add(btnCadastrar);
        rodape.add(btnEditar);
        rodape.add(btnExcluir);

        return rodape;
    }

    public void carregarColaboradores() {
        tableModel.setRowCount(0);
        colaboradorService.listarTodos().forEach(c ->
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getCargo(),
                c.getEmail()
            })
        );
    }

    private void buscarColaboradores() {
        String termo = txtBusca.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        colaboradorService.listarTodos().stream()
            .filter(c -> c.getNome().toLowerCase().contains(termo) ||
                         c.getEmail().toLowerCase().contains(termo) ||
                         c.getCargo().toLowerCase().contains(termo))
            .forEach(c -> tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getCargo(), c.getEmail()
            }));
    }

    private void cadastrarColaborador() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField txtNome = new JTextField();
        JTextField txtCargo = new JTextField();
        JTextField txtEmail = new JTextField();

        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Cargo:"));
        panel.add(txtCargo);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Cadastrar Novo Colaborador",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (colaboradorService.emailExiste(txtEmail.getText(), null)) {
                JOptionPane.showMessageDialog(this, "Este email já está em uso!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Colaborador colaborador = new Colaborador();
            colaborador.setNome(txtNome.getText());
            colaborador.setCargo(txtCargo.getText());
            colaborador.setEmail(txtEmail.getText());

            try {
                colaboradorService.salvar(colaborador);
                carregarColaboradores();
                JOptionPane.showMessageDialog(this, "Colaborador cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar colaborador: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarColaborador() {
        int row = tabelaColaboradores.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um colaborador para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        Colaborador colaborador = colaboradorService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JTextField txtNome = new JTextField(colaborador.getNome());
        JTextField txtCargo = new JTextField(colaborador.getCargo());
        JTextField txtEmail = new JTextField(colaborador.getEmail());

        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Cargo:"));
        panel.add(txtCargo);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Editar Colaborador ID: " + id,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (colaboradorService.emailExiste(txtEmail.getText(), id)) {
                JOptionPane.showMessageDialog(this, "Este email já está em uso por outro colaborador!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            colaborador.setNome(txtNome.getText());
            colaborador.setCargo(txtCargo.getText());
            colaborador.setEmail(txtEmail.getText());

            try {
                colaboradorService.atualizar(id, colaborador);
                carregarColaboradores();
                JOptionPane.showMessageDialog(this, "Colaborador atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar colaborador: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirColaborador() {
        int row = tabelaColaboradores.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um colaborador para excluir.");
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir o colaborador?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            colaboradorService.excluir(id);
            carregarColaboradores();
        }
    }
}
