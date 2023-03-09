import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.util.LinkedList;

public class Partie extends JPanel {
	VueGraphe g;
	Graphe current_g;
	LinkedList<Point> current_c;

	Image background;
	JButton regenerer = new JButton(new ImageIcon("../textures/retry.png"));
	JButton editeur = new JButton(new ImageIcon("../textures/jeu-editeur.png"));
	boolean testing_editing;

	private static class Level {
		public Level(String pack, int n) {
			this.pack = pack;
			this.n = n;
		}
		public String pack;
		public int n;
	}

	private java.util.ArrayList<Level> levels = new java.util.ArrayList<Level>();
	private int current_level = 0;

	private void loadPack(String pack) {
		if (pack == null) {
			for (var p : (new java.io.File("../packs")).listFiles()) {
				loadPack(p.getName());
			}
		} else {
			int n = (new java.io.File("../packs/" + pack)).listFiles().length;
			for (int i = 0; i < n; i++) {
				levels.add(new Level(pack, i));
			}
		}
	}

	public Partie(JFrame frame, Image bg, String pack, VueGraphe vg, int level) {
		loadPack(pack);
		if (levels.size() == 0) {
			System.err.println("No levels in pack " + (pack == null ? "Ω" : pack) + ".");
			System.exit(1);
		}
		current_level = level;
		background = bg;

		MouseInputListener ml = new MouseInputListener() {
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) { next_point(e); }
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e) { next_point(e); }

			private void next_point(MouseEvent e) {
				final int point = g.getId(e.getX(), e.getY());
				if (point == -1) {
					return;
				}
				if (g.get_selected() < 0) {
					g.select(point);
				} else if (g.getGraphe().getConnexion(g.get_selected(), point) != 0) {
					g.getGraphe().setConnexion(g.get_selected(), point, false);
					g.select(point);
					if (estFinie()) {
						finDePartie();
					}
				}
			}
		};
		if (vg != null) {
			g = vg;
			g.set_editing(false);
			for (var l : g.getMouseListeners()) {
				g.removeMouseListener(l);
			}
			for (var l : g.getMouseMotionListeners()) {
				g.removeMouseMotionListener(l);
			}
		} else {
			g = new VueGraphe(false);
		}
		add(g);
		g.addMouseListener(ml);
		g.addMouseMotionListener(ml);

		editeur.setBorderPainted(false);
		editeur.setContentAreaFilled(false);
		editeur.setFocusPainted(false);
		editeur.addActionListener(e -> {
			frame.setContentPane(new Editeur(frame, background, pack, current_level));
			frame.revalidate();
			frame.repaint();
		});

		regenerer.setBorderPainted(false);
		regenerer.setContentAreaFilled(false);
		regenerer.setFocusPainted(false);
		regenerer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				g.setGraphe(current_g, current_c);
				g.select(-1);
				update_current();
			}
		});

		add(regenerer);
		testing_editing = vg != null;
		if (testing_editing) {
			add(editeur);
		} else {
			g.importer(pack, current_level);
			g.select(-1);
		}
		update_current();
	}

	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
	}

	public void finDePartie() {
		if (testing_editing) {
			reset();
			return;
		}
		if (current_level + 1 < levels.size()) {
			suivant();
			return;
		}
		remove(g);
		remove(regenerer);

		JButton congrats = new JButton("NEXT");
		congrats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suivant();
				add(g);
				add(regenerer);
				remove(congrats);
				validate();
				repaint();
			}
		});

		add(congrats);
		validate();
		repaint();
	}

	public boolean estFinie() {
		return g.getGraphe().nbConnexions() == 0; // on peut aussi tester si la partie ne peut plus être gagnée
	}

	public void suivant() {
		current_level = (current_level + 1) % levels.size();
		final var lvl = levels.get(current_level);
		g.importer(lvl.pack, lvl.n);
		g.select(-1);
		update_current();
	}

	private void update_current() {
		current_g = g.getGraphe();
		current_c = g.getCoords();
		reset();
	}
	private void reset() {
		try {
			g.setGraphe(current_g.clone(), current_c);
		} catch (CloneNotSupportedException err) { // This won't happen, java's just being a dick.
			System.err.println("This shouldn't happen.");
		}
	}
}
