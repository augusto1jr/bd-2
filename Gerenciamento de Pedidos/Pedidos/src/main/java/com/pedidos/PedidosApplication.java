package com.pedidos;

import com.pedidos.view.ClienteView;
import com.pedidos.view.MainView;
import com.pedidos.view.PedidoView;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;

@SpringBootApplication
public class PedidosApplication {

    public static void main(String[] args) {
        try {
            // Configura o contexto Spring
            ConfigurableApplicationContext context = new SpringApplicationBuilder(PedidosApplication.class)
                    .headless(false)
                    .run(args);

            // Obtém as views do contexto Spring
            ClienteView clienteView = context.getBean(ClienteView.class);
            PedidoView pedidoView = context.getBean(PedidoView.class);

            // Inicia a interface gráfica na thread correta (EDT)
            SwingUtilities.invokeLater(() -> {
                MainView mainView = new MainView(clienteView, pedidoView);
                mainView.setVisible(true);
            });
            
        } catch (Exception e) {
            /*JOptionPane.showMessageDialog(null, 
                "Erro ao iniciar a aplicação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();*/
        }
    }
}