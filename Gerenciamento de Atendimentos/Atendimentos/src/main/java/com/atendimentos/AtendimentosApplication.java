package com.atendimentos;

import com.atendimentos.view.ClienteView;
import com.atendimentos.view.ColaboradorView;
import com.atendimentos.view.AtendimentoView;
import com.atendimentos.view.MainView;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class AtendimentosApplication {

    public static void main(String[] args) {
        try {
            // Inicia o contexto Spring sem modo headless (para permitir Swing)
            ConfigurableApplicationContext context = new SpringApplicationBuilder(AtendimentosApplication.class)
                    .headless(false)
                    .run(args);

            // Recupera as views do contexto Spring
            ClienteView clienteView = context.getBean(ClienteView.class);
            ColaboradorView colaboradorView = context.getBean(ColaboradorView.class);
            AtendimentoView atendimentoView = context.getBean(AtendimentoView.class);

            // Inicia a interface gráfica na Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                MainView mainView = new MainView(clienteView, colaboradorView, atendimentoView);
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
