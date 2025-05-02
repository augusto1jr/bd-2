package com.atendimentos.view;

import com.atendimentos.model.Atendimento;
import com.atendimentos.model.Cliente;
import com.atendimentos.model.Colaborador;
import com.atendimentos.service.AtendimentoService;
import com.atendimentos.service.ClienteService;
import com.atendimentos.service.ColaboradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class AtendimentoView extends JPanel {

    private final AtendimentoService atendimentoService;
    private final ClienteService clienteService;
    private final ColaboradorService colaboradorService;
    private JTable tabelaAtendimentos;
    private DefaultTableModel tableModel;
    private JTextField txtBusca;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> comboFiltroStatus;


    @Autowired
    public AtendimentoView(AtendimentoService atendimentoService, ClienteService clienteService, ColaboradorService colaboradorService) {
        this.atendimentoService = atendimentoService;
        this.clienteService = clienteService;
        this.colaboradorService = colaboradorService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = createPanelSuperior();
        tabelaAtendimentos = createAtendimentosTable();
        JScrollPane scrollPane = new JScrollPane(tabelaAtendimentos);
        JPanel panelInferior = createPanelInferior();

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }


    private JPanel createPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> ((MainView) SwingUtilities.getWindowAncestor(this)).showMainMenu());

        JPanel panelBuscaOrdenacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("\uD83D\uDD0E");
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.addActionListener(this::buscarAtendimentos);

        JButton btnOrdenarData = new JButton("Ordenar por Data");
        btnOrdenarData.addActionListener(e -> ordenarPorData());

        // Botão de filtro por status
        JButton btnFiltrarStatus = new JButton("Filtrar");
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemTodos = new JMenuItem("Todos");
        JMenuItem itemAberto = new JMenuItem("Aberto");
        JMenuItem itemAndamento = new JMenuItem("Andamento");
        JMenuItem itemConcluido = new JMenuItem("Concluído");
        
        itemTodos.addActionListener(e -> filtrarPorStatus(null));
        itemAberto.addActionListener(e -> filtrarPorStatus(Atendimento.Status.Aberto));
        itemAndamento.addActionListener(e -> filtrarPorStatus(Atendimento.Status.Andamento));
        itemConcluido.addActionListener(e -> filtrarPorStatus(Atendimento.Status.Concluido));
        
        popupMenu.add(itemTodos);
        popupMenu.add(itemAberto);
        popupMenu.add(itemAndamento);
        popupMenu.add(itemConcluido);
        
        btnFiltrarStatus.addActionListener(e -> popupMenu.show(btnFiltrarStatus, 0, btnFiltrarStatus.getHeight()));

        panelBuscaOrdenacao.add(new JLabel("Buscar:"));
        panelBuscaOrdenacao.add(txtBusca);
        panelBuscaOrdenacao.add(btnBuscar);
        panelBuscaOrdenacao.add(Box.createHorizontalStrut(20));
        panelBuscaOrdenacao.add(btnOrdenarData);
        panelBuscaOrdenacao.add(Box.createHorizontalStrut(20));
        panelBuscaOrdenacao.add(btnFiltrarStatus);

        panel.add(btnVoltar, BorderLayout.WEST);
        panel.add(panelBuscaOrdenacao, BorderLayout.CENTER);

        return panel;
    }


    private JTable createAtendimentosTable() {
        String[] colunas = {"ID", "Cliente", "Colaborador", "Data", "Descrição", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID
                if (columnIndex == 3) return String.class;   // Data como string formatada (dd/MM/yyyy)
                return super.getColumnClass(columnIndex);
            }
        };

        JTable table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Configura comparador para ordenação da coluna de data
        sorter.setComparator(3, (Comparator<String>) (d1, d2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date1 = LocalDate.parse(d1, formatter);
            LocalDate date2 = LocalDate.parse(d2, formatter);
            return date1.compareTo(date2);
        });

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        return table;
    }


    private JPanel createPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnNovo = new JButton("Novo Atendimento");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnExportar = new JButton("Exportar CSV");

        btnNovo.addActionListener(e -> cadastrarAtendimento());
        btnEditar.addActionListener(e -> editarAtendimento());
        btnExcluir.addActionListener(e -> excluirAtendimento());
        btnExportar.addActionListener(e -> exportarParaCSV());

        panel.add(btnNovo);
        panel.add(btnEditar);
        panel.add(btnExcluir);
        panel.add(btnExportar);

        return panel;
    }
    

    public void carregarAtendimentos() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Atendimento> atendimentos = atendimentoService.listarTodos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Atendimento atendimento : atendimentos) {
            tableModel.addRow(new Object[]{
                atendimento.getId(),
                atendimento.getCliente().getNome(),
                atendimento.getColaborador().getNome(),
                atendimento.getData().format(formatter),
                atendimento.getDescricao(),
                atendimento.getStatus()
            });
        }
    }


    private void buscarAtendimentos(ActionEvent e) {
        String termo = txtBusca.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        atendimentoService.listarTodos().stream()
            .filter(a -> a.getCliente().getNome().toLowerCase().contains(termo) ||
            			a.getColaborador().getNome().toLowerCase().contains(termo))
            .forEach(a -> tableModel.addRow(new Object[]{
                a.getId(),
                a.getCliente().getNome(),
                a.getColaborador().getNome(),
                a.getData().format(formatter),
                a.getDescricao(),
                a.getStatus()
            }));
    }


    private void ordenarPorData() {
        sorter.setComparator(3, (Comparator<String>) (d1, d2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date1 = LocalDate.parse(d1, formatter);
            LocalDate date2 = LocalDate.parse(d2, formatter);
            return date1.compareTo(date2);
        });
        sorter.toggleSortOrder(3);
    }
    
    
    private void filtrarPorStatus(Atendimento.Status status) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tabelaAtendimentos.getModel());
        tabelaAtendimentos.setRowSorter(sorter);
        
        if (status != null) {
            sorter.setRowFilter(RowFilter.regexFilter(status.toString(), 5)); 
        } else {
            sorter.setRowFilter(null);
        }
    }
    

    private void cadastrarAtendimento() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField txtClienteId = new JTextField();
        JTextField txtColaboradorId = new JTextField();
        JTextField txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtDescricao = new JTextField();
        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Aberto", "Andamento", "Concluido"});

        panel.add(new JLabel("ID do Cliente:"));
        panel.add(txtClienteId);
        panel.add(new JLabel("ID do Colaborador:"));
        panel.add(txtColaboradorId);
        panel.add(new JLabel("Data (dd/MM/aaaa):"));
        panel.add(txtData);
        panel.add(new JLabel("Descrição:"));
        panel.add(txtDescricao);
        panel.add(new JLabel("Status:"));
        panel.add(comboStatus);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Cadastrar Novo Atendimento",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (txtClienteId.getText().isEmpty() || txtColaboradorId.getText().isEmpty() ||
                    txtData.getText().isEmpty() || txtDescricao.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Long clienteId = Long.parseLong(txtClienteId.getText());
                Optional<Cliente> cliente = clienteService.buscarPorId(clienteId);

                if (cliente.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado com o ID informado!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Long colaboradorId = Long.parseLong(txtColaboradorId.getText());
                Optional<Colaborador> colaborador = colaboradorService.buscarPorId(colaboradorId);

                if (colaborador.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Colaborador não encontrado com o ID informado!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataBase = LocalDate.parse(txtData.getText(), formatter);
                LocalDateTime data = dataBase.atTime(LocalTime.MIDNIGHT);

                String descricao = txtDescricao.getText();
                Atendimento.Status status = Atendimento.Status.valueOf((String) comboStatus.getSelectedItem());

                Atendimento atendimento = new Atendimento();
                atendimento.setCliente(cliente.get());
                atendimento.setColaborador(colaborador.get());
                atendimento.setData(data);
                atendimento.setDescricao(descricao);
                atendimento.setStatus(status);



                atendimentoService.salvar(atendimento);

                carregarAtendimentos();

                JOptionPane.showMessageDialog(
                    this,
                    "Atendimento cadastrado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID do cliente ou colaborador inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido! Use dd/MM/aaaa", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar atendimento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void editarAtendimento() {
        int row = tabelaAtendimentos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um atendimento para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(row, 0);

        Atendimento atendimento = atendimentoService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        JTextField txtClienteId = new JTextField(atendimento.getCliente().getId().toString());
        JTextField txtColaboradorId = new JTextField(atendimento.getColaborador().getId().toString());
        JTextField txtData = new JTextField(atendimento.getData().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtDescricao = new JTextField(atendimento.getDescricao());

        JComboBox<String> comboStatus = new JComboBox<>();
        for (Atendimento.Status s : Atendimento.Status.values()) {
            comboStatus.addItem(s.name());
        }
        comboStatus.setSelectedItem(atendimento.getStatus().name());

        panel.add(new JLabel("ID do Cliente:"));
        panel.add(txtClienteId);
        panel.add(new JLabel("ID do Colaborador:"));
        panel.add(txtColaboradorId);
        panel.add(new JLabel("Data (dd/MM/yyyy):"));
        panel.add(txtData);
        panel.add(new JLabel("Descrição:"));
        panel.add(txtDescricao);
        panel.add(new JLabel("Status:"));
        panel.add(comboStatus);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Editar Atendimento ID: " + id,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (txtClienteId.getText().isEmpty() || txtColaboradorId.getText().isEmpty() || 
                    txtData.getText().isEmpty() || txtDescricao.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Long clienteId = Long.parseLong(txtClienteId.getText());
                Long colaboradorId = Long.parseLong(txtColaboradorId.getText());

                Cliente cliente = clienteService.buscarPorId(clienteId)
                    .orElseThrow(() -> new Exception("Cliente não encontrado com o ID: " + clienteId));
                Colaborador colaborador = colaboradorService.buscarPorId(colaboradorId)
                    .orElseThrow(() -> new Exception("Colaborador não encontrado com o ID: " + colaboradorId));

                LocalDate dataBase = LocalDate.parse(txtData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDateTime data = dataBase.atTime(LocalTime.MIDNIGHT); // ou outro horário, se necessário

                String descricao = txtDescricao.getText();
                Atendimento.Status status = Atendimento.Status.valueOf((String) comboStatus.getSelectedItem());

                Atendimento atualizado = new Atendimento();
                atualizado.setCliente(cliente);
                atualizado.setColaborador(colaborador);
                atualizado.setData(data);
                atualizado.setDescricao(descricao);
                atualizado.setStatus(status);

                atendimentoService.atualizar(id, atualizado);
                carregarAtendimentos();

                JOptionPane.showMessageDialog(
                    this,
                    "Atendimento atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID do cliente ou colaborador inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido! Use dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void excluirAtendimento() {
        int row = tabelaAtendimentos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um atendimento para excluir.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        atendimentoService.excluir(id);
        carregarAtendimentos();
        JOptionPane.showMessageDialog(this, "Atendimento excluído com sucesso.");
    }
    
    
    private void exportarParaCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar como CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos CSV (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("atendimentos.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Garante que a extensão .csv está presente
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                fileToSave = new File(filePath + ".csv");
            }

            try (PrintWriter writer = new PrintWriter(fileToSave, "UTF-8")) {
                // Escreve o cabeçalho
                DefaultTableModel model = (DefaultTableModel) tabelaAtendimentos.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();

                // Escreve os dados
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.print(model.getValueAt(i, j).toString());
                        if (j < model.getColumnCount() - 1) {
                            writer.print(",");
                        }
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Dados exportados com sucesso!", "Exportar CSV", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
