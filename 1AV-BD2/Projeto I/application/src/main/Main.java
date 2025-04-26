package main;

import javax.swing.JFrame;

import view.TelaInicial;


public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Gerenciador");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setContentPane(new TelaInicial(frame));
        frame.setVisible(true);
    }
}
