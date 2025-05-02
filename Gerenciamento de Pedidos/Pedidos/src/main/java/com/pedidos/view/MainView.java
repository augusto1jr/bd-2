package com.pedidos.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {
    
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ClienteView clienteView;
    private PedidoView pedidoView;
    
    public MainView(ClienteView clienteView, PedidoView pedidoView) {
        this.clienteView = clienteView;
        this.pedidoView = pedidoView;
        configureWindow();
        initComponents();
    }
    
    private void configureWindow() {
        setTitle("Sistema de Pedidos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Painel do Menu Principal
        JPanel mainMenuPanel = createMainMenuPanel();
        
        // Adiciona os painéis ao cardPanel
        cardPanel.add(mainMenuPanel, "Menu");
        cardPanel.add(clienteView, "Clientes");
        cardPanel.add(pedidoView, "Pedidos");
        
        add(cardPanel);
        showMainMenu();
    }
    
    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JButton btnClientes = new JButton("Menu Cliente");
        JButton btnPedidos = new JButton("Menu Pedido");
        
        // Estilização dos botões
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        btnClientes.setFont(buttonFont);
        btnPedidos.setFont(buttonFont);
        
        // Ações dos botões
        btnClientes.addActionListener(e -> showClienteView());
        btnPedidos.addActionListener(e -> showPedidoView());
        
        panel.add(btnClientes);
        panel.add(btnPedidos);
        
        return panel;
    }
    
    public void showMainMenu() {
        cardLayout.show(cardPanel, "Menu");
    }
    
    public void showClienteView() {
        clienteView.carregarClientes();
        cardLayout.show(cardPanel, "Clientes");
    }
    
    public void showPedidoView() {
    	pedidoView.carregarPedidos();
        cardLayout.show(cardPanel, "Pedidos");
    }
}