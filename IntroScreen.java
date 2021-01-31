package PacMan;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.JPanel;

public class IntroScreen extends JPanel implements ActionListener {
	Graphics2D g;
	public IntroScreen() {
		
	}
	public void drawScreen(Graphics2D g) throws Exception{
		
		String title= "A PacMan-esque Game";
				
		String startText ="Press SPACE to Start";
		g.setColor(Color.YELLOW);
		
		URL fontUrl = new URL("http://www.webpagepublicity.com/" +
	            "free-fonts/p/PacFont.ttf");
		
	        Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
	        font = font.deriveFont(Font.PLAIN,18);
	    g.setFont(font);
	        
		g.drawString(title, 40, 50);
		font = font.deriveFont(Font.PLAIN,12);
		g.setFont(font);
		g.drawString(startText,85, 150);
		
		
	   
		
		
	}
	public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 400, 400);
        
        try {
			drawScreen(g2d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      

       

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
	
	
	
	
