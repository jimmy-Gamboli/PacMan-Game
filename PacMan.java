package PacMan;

import javax.swing.JFrame;

public class PacMan extends JFrame{

	public PacMan() {
		add(new Model());
	}
	
	
	public static void main(String[] args) {
		PacMan pac = new PacMan();
		pac.setVisible(true);
		pac.setTitle("Pacman");
		pac.setSize(380,420);
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pac.setLocationRelativeTo(null);
		
	}

}