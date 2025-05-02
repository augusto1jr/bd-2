package com.pedidos.view;

import com.pedidos.model.Cliente;
import com.pedidos.model.Pedido;
import com.pedidos.service.PedidoService;
import com.pedidos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.time.format.DateTimeParseException;


@Component
public class PedidoView extends JPanel {
    
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private JTable tabelaPedidos;
    private DefaultTableModel tableModel;
    private JTextField txtBusca;
    private TableRowSorter<DefaultTableModel> sorter;
    
    @Autowired
    public PedidoView(PedidoService pedidoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;  // Injetando ClienteService
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel Superior (Busca + Voltar + Ordenação)
        JPanel panelSuperior = createPanelSuperior();
        
        // Tabela de Pedidos
        tabelaPedidos = createPedidosTable();
        JScrollPane scrollPane = new JScrollPane(tabelaPedidos);
        
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
        
        // Painel de Busca e Ordenação
        JPanel panelBuscaOrdenacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        // Campo de Busca
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("\uD83D\uDD0E"); // Ícone de lupa
        btnBuscar.setToolTipText("Buscar");
        btnBuscar.addActionListener(this::buscarPedidos);
        
        // Botões de Ordenação
        JButton btnOrdenarData = new JButton("Ordenar por Data");
        btnOrdenarData.addActionListener(e -> ordenarPorData());
        
        JButton btnOrdenarValor = new JButton("Ordenar por Valor");
        btnOrdenarValor.addActionListener(e -> ordenarPorValor());
        
        panelBuscaOrdenacao.add(new JLabel("Buscar:"));
        panelBuscaOrdenacao.add(txtBusca);
        panelBuscaOrdenacao.add(btnBuscar);
        panelBuscaOrdenacao.add(Box.createHorizontalStrut(20));
        panelBuscaOrdenacao.add(btnOrdenarData);
        panelBuscaOrdenacao.add(btnOrdenarValor);
        
        panel.add(btnVoltar, BorderLayout.WEST);
        panel.add(panelBuscaOrdenacao, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTable createPedidosTable() {
        String[] colunas = {"ID", "Cliente", "Data", "Valor Total"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Double.class; // Valor Total
                return super.getColumnClass(columnIndex);
            }
        };
        
        JTable table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Configura comparadores para ordenação
        sorter.setComparator(2, (Comparator<String>) (d1, d2) -> {
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
        
        JButton btnNovo = new JButton("Novo Pedido");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        
        btnNovo.addActionListener(e -> cadastrarPedido());
        btnEditar.addActionListener(e -> editarPedido());
        btnExcluir.addActionListener(e -> excluirPedido());
        
        panel.add(btnNovo);
        panel.add(btnEditar);
        panel.add(btnExcluir);
        
        return panel;
    }
    
    
    public void carregarPedidos() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Pedido> pedidos = pedidoService.listarTodos();
        for (Pedido pedido : pedidos) {
            tableModel.addRow(new Object[]{
            	pedido.getId(),
            	pedido.getCliente().getNome(),
            	pedido.getDataPedido(),
            	pedido.getValorTotal()
            });
        }
    }
    
    
    private void cadastrarPedido() {
        // Cria um painel para o formulário
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        // Campos do formulário
        JTextField txtClienteId = new JTextField();
        JTextField txtData = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtValor = new JTextField("0.00");
        
        // Adiciona os componentes ao painel
        panel.add(new JLabel("ID do Cliente:"));
        panel.add(txtClienteId);
        panel.add(new JLabel("Data (dd/MM/aaaa):"));
        panel.add(txtData);
        panel.add(new JLabel("Valor Total:"));
        panel.add(txtValor);
        
        // Exibe o diálogo
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Cadastrar Novo Pedido",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Se o usuário clicou em OK
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validações básicas
                if (txtClienteId.getText().isEmpty() || txtData.getText().isEmpty() || txtValor.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verifica se o cliente existe
                Long clienteId = Long.parseLong(txtClienteId.getText());
                Optional<Cliente> cliente = clienteService.buscarPorId(clienteId);
                
                if (cliente.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Cliente não encontrado com o ID informado!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Parse da data
                LocalDate dataPedido = LocalDate.parse(txtData.getText(), 
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                
                // Parse do valor
                double valorTotal = Double.parseDouble(txtValor.getText().replace(",", "."));
                
                if (valorTotal <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor deve ser positivo!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Cria o novo pedido
                Pedido novoPedido = new Pedido();
                novoPedido.setCliente(cliente.get());
                novoPedido.setDataPedido(dataPedido);
                novoPedido.setValorTotal(valorTotal);
                
                // Salva o pedido no banco de dados
                pedidoService.salvar(novoPedido);
                
                // Atualiza a tabela
                carregarPedidos();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Pedido cadastrado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "ID do cliente ou valor inválido!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Formato de data inválido! Use dd/MM/aaaa",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Erro ao cadastrar pedido: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    } 
    
    
    private void buscarPedidos(ActionEvent e) {
        String termo = txtBusca.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        
        pedidoService.listarTodos().stream()
            .filter(p -> p.getCliente().getNome().toLowerCase().contains(termo) || 
                        p.getCliente().getLogin().toLowerCase().contains(termo))
            .forEach(p -> tableModel.addRow(new Object[]{
                p.getId(), p.getCliente().getNome(), p.getDataPedido(), p.getValorTotal()
            }));
    }
    
    
    private void editarPedido() {
        int row = tabelaPedidos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um pedido para editar", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém o ID do pedido selecionado
        Long id = (Long) tableModel.getValueAt(row, 0);
        
        // Busca o pedido atual no banco de dados
        Pedido pedido = pedidoService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        
        // Cria o formulário de edição SIMPLES (igual ao de clientes)
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        // Campos do formulário com os valores atuais
        JTextField txtClienteId = new JTextField(pedido.getCliente().getId().toString());
        JTextField txtData = new JTextField(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        JTextField txtValor = new JTextField(pedido.getValorTotal().toString());
        
        panel.add(new JLabel("ID do Cliente:"));
        panel.add(txtClienteId);
        panel.add(new JLabel("Data (dd/MM/yyyy):"));
        panel.add(txtData);
        panel.add(new JLabel("Valor Total:"));
        panel.add(txtValor);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Editar Pedido ID: " + id,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validações básicas
                if (txtClienteId.getText().isEmpty() || txtData.getText().isEmpty() || txtValor.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verifica se o cliente existe
                Long clienteId = Long.parseLong(txtClienteId.getText());
                Cliente cliente = clienteService.buscarPorId(clienteId)
                    .orElseThrow(() -> new Exception("Cliente não encontrado com o ID: " + clienteId));
                
                // Parse da data
                LocalDate dataPedido = LocalDate.parse(txtData.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                
                // Parse do valor
                double valorTotal = Double.parseDouble(txtValor.getText());
                if (valorTotal <= 0) {
                    throw new Exception("O valor deve ser positivo");
                }
                
                // Cria objeto com atualizações (igual ao do cliente)
                Pedido pedidoAtualizado = new Pedido();
                pedidoAtualizado.setCliente(cliente);
                pedidoAtualizado.setDataPedido(dataPedido);
                pedidoAtualizado.setValorTotal(valorTotal);
                
                // Usa o método atualizar do service
                pedidoService.atualizar(id, pedidoAtualizado);
                
                // Atualiza a tabela
                carregarPedidos();
                
                JOptionPane.showMessageDialog(
                    this,
                    "Pedido atualizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID do cliente ou valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido! Use dd/MM/yyyy", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void ordenarPorData() {
        sorter.setComparator(2, (Comparator<String>) (d1, d2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date1 = LocalDate.parse(d1, formatter);
            LocalDate date2 = LocalDate.parse(d2, formatter);
            return date1.compareTo(date2);
        });
        sorter.toggleSortOrder(2); // Alterna entre ascendente e descendente
    }
    
    private void ordenarPorValor() {
        sorter.setComparator(3, Comparator.comparingDouble(value -> (Double) value));
        sorter.toggleSortOrder(3); // Alterna entre ascendente e descendente
    }
    
    
    private void excluirPedido() {
        int row = tabelaPedidos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para excluir");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Confirmar exclusão do pedido?", "Confirmação", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            pedidoService.excluir(id);
            carregarPedidos();
        }
    }
}