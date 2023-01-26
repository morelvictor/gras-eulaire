import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import java.util.LinkedList;
import java.util.Random;

public class Editeur extends JComponent {
	private static int DIAMETRE = 15;
	private static Color COULEUR = new Color(39, 78, 140);

	private VueGrapheEditeur vuegraphe;


	private JButton poser_sommet;
	private boolean peut_poser_sommet = false;

	private JButton lier;
	private boolean peut_lier = false;
	//indice dans la liste sommets du sommet à lier
	private int a_lier = -1;


	public Editeur() {

		vuegraphe = new VueGrapheEditeur(COULEUR, DIAMETRE, this);
		vuegraphe.setBounds(20,20,800,800);
		add(vuegraphe);

		poser_sommet = new JButton("o");

		poser_sommet.setBounds(900,50,50,50);
		add(poser_sommet);

		poser_sommet.addActionListener(
			(ActionEvent e) -> {
				peut_poser_sommet = !peut_poser_sommet;
				lier.setEnabled(!peut_poser_sommet);
			});

		lier = new JButton("x");
		lier.setBounds(900,110,50,50);
		add(lier);

		lier.addActionListener(
			(ActionEvent e) -> {
				peut_lier = !peut_lier;
				poser_sommet.setEnabled(!peut_lier);
				a_lier = -1;
			});
	
		repaint();
	}

	public int getALier(){
		return a_lier;
	}

	public void setALier(int n){
		a_lier = n;
	}

	public boolean getPeutPoserSommet(){
		return peut_poser_sommet;
	}

	public boolean getPeutLier(){
		return peut_lier;
	}


	public void paintComponent(Graphics g) {

		lier.setBounds(getWidth() - 100,110,50,50);
		poser_sommet.setBounds(getWidth() - 100,50,50,50);

		vuegraphe.repaint();	
	}



	
}
