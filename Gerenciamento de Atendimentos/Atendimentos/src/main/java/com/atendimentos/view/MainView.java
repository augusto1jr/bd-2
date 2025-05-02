package com.atendimentos.view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private ClienteView clienteView;
    private ColaboradorView colaboradorView;
    private AtendimentoView atendimentoView;

    public MainView(ClienteView clienteView, ColaboradorView colaboradorView, AtendimentoView atendimentoView) {
        this.clienteView = clienteView;
        this.colaboradorView = colaboradorView;
        this.atendimentoView = atendimentoView;
        configureWindow();
        initComponents();
    }

    private void configureWindow() {
        setTitle("Sistema de Atendimentos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel mainMenuPanel = createMainMenuPanel();

        cardPanel.add(mainMenuPanel, "Menu");
        cardPanel.add(clienteView, "Clientes");
        cardPanel.add(colaboradorView, "Colaboradores");
        cardPanel.add(atendimentoView, "Atendimentos");

        add(cardPanel);
        showMainMenu();
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton btnClientes = new JButton("Menu Cliente");
        JButton btnColaboradores = new JButton("Menu Colaborador");
        JButton btnAtendimentos = new JButton("Menu Atendimento");

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        btnClientes.setFont(buttonFont);
        btnColaboradores.setFont(buttonFont);
        btnAtendimentos.setFont(buttonFont);

        btnClientes.addActionListener(e -> showClienteView());
        btnColaboradores.addActionListener(e -> showColaboradorView());
        btnAtendimentos.addActionListener(e -> showAtendimentoView());

        panel.add(btnClientes);
        panel.add(btnColaboradores);
        panel.add(btnAtendimentos);

        return panel;
    }

    public void showMainMenu() {
        cardLayout.show(cardPanel, "Menu");
    }

    public void showClienteView() {
        clienteView.carregarClientes();
        cardLayout.show(cardPanel, "Clientes");
    }

    public void showColaboradorView() {
        colaboradorView.carregarColaboradores();
        cardLayout.show(cardPanel, "Colaboradores");
    }

    public void showAtendimentoView() {
        atendimentoView.carregarAtendimentos();
        cardLayout.show(cardPanel, "Atendimentos");
    }
}
