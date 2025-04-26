package view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class TelaInicial extends JPanel {

	private static final long serialVersionUID = 1L;

	public TelaInicial(JFrame parentFrame) {
		setLayout(null);
		
		JButton cadastrarButton = new JButton("Cadastrar Cliente");
		cadastrarButton.setBackground(new Color(255, 255, 255));
		cadastrarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaCadastro(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		cadastrarButton.setBounds(282, 149, 135, 23);
		add(cadastrarButton);
		
		JButton buscarButton = new JButton("Buscar Cliente");
		buscarButton.setBackground(new Color(255, 255, 255));
		buscarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaBuscar(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		buscarButton.setBounds(282, 183, 135, 23);
		add(buscarButton);
		
		JButton listarButton = new JButton("Listar Clientes");
		listarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.setContentPane(new TelaListar(parentFrame));
				parentFrame.revalidate();
				parentFrame.repaint();
			}
		});
		listarButton.setBackground(new Color(255, 255, 255));
		listarButton.setBounds(282, 217, 135, 23);
		add(listarButton);

	}

}
