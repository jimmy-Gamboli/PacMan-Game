package PacMan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;
public class Model extends JPanel implements ActionListener {
	
	private Dimension d;
	private final Font smallFont = new Font ("Arial",Font.BOLD, 14);
	private boolean isRunning = false;
	private boolean isDead =false;
	
	private final int blockSize = 24;
	private final int numBlocks = 15;
	private final int screenSize = numBlocks*blockSize;
	private final int numGhostsMax = 12;
	private  int pacmanSpeed = 6;
	
	private int numGhosts = 6;
	private int lives,score;
	private int[] dx,dy;
	private int[] ghostX,ghostY, ghost_dx,ghost_dy, ghostSpeed;
	
	 private Image heart,ghost,up,down,left, right;
	
	private int pacmanX, pacmanY, pacmanDY, pacmanDX;
	private int req_dx, req_dy;
	
	private final int validSpeeds[]= {1,2,3,4,6,8};
	private final int maxSpeed=6;
	private int currentSpeed =3;
	private Timer timer;
	private long startTime;
	private long endTime;
	private boolean isBoosted;
	private short[] screenData;
	
	///*SELF-CREATD/////
	private int numDots=0;
	
	///0=blue boundary, 1=left border, 2 = top border, 8 =bottom border, 4 = right border, 16 = white dots
	private final short levelData[] = {
			
	    	19, 18, 50, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
	        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 32, 20,
	        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
	        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 32, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
	        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
	        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
	        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 32, 16, 16, 16, 16, 16, 20,
	        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
	    };
	
	
	
	public Model() {
		//loadImages;
		initVariables();
		addKeyListener(new TAdapter());
		setFocusable(true);
		initGame();
		
		
	}
	
	
	public void drawScore(Graphics2D g) {
		g.setFont(smallFont);
		g.setColor(Color.RED);
		String s="Score: "+score;
		g.drawString(s,screenSize/2, screenSize+15);
		
		 for (int i = 0; i < lives; i++) {
			 g.fillOval(i*28, screenSize+5, 20, 20);
	        }
	}
	
	
	private void initVariables() {
		screenData = new short[numBlocks*numBlocks];
		d = new Dimension(400,400);
		ghostX=new int[numGhostsMax];
		ghost_dx= new int [numGhostsMax];
		ghostY = new int[numGhostsMax];
		ghost_dy = new int[numGhostsMax];
		ghostSpeed = new int[numGhostsMax];
		dx = new int[4];
		dy = new int[4];
		isBoosted=false;
		
		for(int i=0;i<levelData.length;i++) {
			if(levelData[i]!=0) {
				numDots++;
			}
		}
		
		timer = new Timer(40,this);
		timer.restart();
		
		
	}
	
	class TAdapter extends KeyAdapter {
		        @Override
		        public void keyPressed(KeyEvent e) {

		            int key = e.getKeyCode();

		            if (isRunning) {
		                if (key == KeyEvent.VK_LEFT) {
		                    req_dx = -1;
		                    req_dy = 0;
		                } else if (key == KeyEvent.VK_RIGHT) {
		                    req_dx = 1;
		                    req_dy = 0;
		                } else if (key == KeyEvent.VK_UP) {
		                    req_dx = 0;
		                    req_dy = -1;
		                } else if (key == KeyEvent.VK_DOWN) {
		                    req_dx = 0;
		                    req_dy = 1;
		                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
		                    isRunning = false;
		                } 
		            } else {
		                if (key == KeyEvent.VK_SPACE) {
		                    isRunning = true;
		                    initGame();
		                }
		            }
		        }
	}
	
	public void initGame() {
		lives =3;
		score =0;
		initLevel();
		numGhosts=6;
		currentSpeed=3;
		
	}
	
	private void initLevel() {
		for(int i=0;i<numBlocks*numBlocks; i++) {
			screenData[i]=levelData[i];
		}
		continueLevel();
	}
	private void playGame(Graphics2D graphics) {
		if(isDead) {
			death();
		}
		else {
			movePacman();
			drawPacman(graphics);
			moveGhosts(graphics);
			checkMaze();
		}
	}
	
	public void movePacman() {
		int pos;
        short ch;

        if (pacmanX % blockSize == 0 && pacmanY % blockSize == 0) {
            pos = pacmanX / blockSize + numBlocks * (int) (pacmanY / blockSize);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
            	int c=ch & 15;
                screenData[pos] = (short) (ch & 15);
                score++;
                numDots--;
            }
            // if it hits a red dot, then speed it up
            if ((ch & 32) != 0) {
            	int c=ch & 15;
                screenData[pos] = (short) (ch & 15);
                score++;
                numDots--;
                pacmanSpeed=12;
                startTime=System.nanoTime();
                isBoosted=true;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmanDX = req_dx;
                    pacmanDY = req_dy;
                }
            }

            // Check for standstill
            if ((pacmanDX == -1 && pacmanDY == 0 && (ch & 1) != 0)
                    || (pacmanDX == 1 && pacmanDY == 0 && (ch & 4) != 0)
                    || (pacmanDX == 0 && pacmanDY == -1 && (ch & 2) != 0)
                    || (pacmanDX == 0 && pacmanDY == 1 && (ch & 8) != 0)) {
                pacmanDX = 0;
                pacmanDY = 0;
            }
        } 
        pacmanX = pacmanX + pacmanSpeed * pacmanDX;
        pacmanY = pacmanY + pacmanSpeed * pacmanDY;
		}
	
	public void drawPacman(Graphics2D graphics) {
		if(req_dx==-1) {
			//graphics.drawImage(left,pacmanX+1,pacmanY+1,this);
			graphics.setColor(Color.YELLOW);
			graphics.fillRect(pacmanX+1, pacmanY+1, blockSize,blockSize);
		}
		else if(req_dx==1) {
			//graphics.drawImage(right,pacmanX+1,pacmanY+1,this);
			graphics.setColor(Color.YELLOW);
			graphics.fillRect(pacmanX+1, pacmanY+1, blockSize,blockSize);
		}
		else if(req_dy ==-1) {
			//graphics.drawImage(up,pacmanX+1,pacmanY+1,this);
			graphics.setColor(Color.YELLOW);
			graphics.fillRect(pacmanX+1, pacmanY+1, blockSize,blockSize);
			
		}
		else {
			//graphics.drawImage(down,pacmanX+1,pacmanY+1,this);
			graphics.setColor(Color.YELLOW);
			graphics.fillRect(pacmanX+1, pacmanY+1, blockSize,blockSize);
		}
	}
	
	public void moveGhosts(Graphics2D g) {
		int position,count;
		 for (int i = 0; i < numGhosts; i++) {
	            if (ghostX[i] % blockSize == 0 && ghostY[i] % blockSize == 0) {
	                position = ghostX[i] / blockSize + numBlocks * (int) (ghostY[i] / blockSize);

	                count = 0;

	                if ((screenData[position] & 1) == 0 && ghost_dx[i] != 1) {
	                    dx[count] = -1;
	                    dy[count] = 0;
	                    count++;
	                }

	                if ((screenData[position] & 2) == 0 && ghost_dy[i] != 1) {
	                    dx[count] = 0;
	                    dy[count] = -1;
	                    count++;
	                }

	                if ((screenData[position] & 4) == 0 && ghost_dx[i] != -1) {
	                    dx[count] = 1;
	                    dy[count] = 0;
	                    count++;
	                }

	                if ((screenData[position] & 8) == 0 && ghost_dy[i] != -1) {
	                    dx[count] = 0;
	                    dy[count] = 1;
	                    count++;
	                }

	                if (count == 0) {

	                    if ((screenData[position] & 15) == 15) {
	                        ghost_dx[i] = 0;
	                        ghost_dy[i] = 0;
	                    } else {
	                        ghost_dx[i] = -ghost_dx[i];
	                        ghost_dy[i] = -ghost_dy[i];
	                    }

	                } else {

	                    count = (int) (Math.random() * count);

	                    if (count > 3) {
	                        count = 3;
	                    }

	                    ghost_dx[i] = dx[count];
	                    ghost_dy[i] = dy[count];
	                }

	            }

	            ghostX[i] = ghostX[i] + (ghost_dx[i] * ghostSpeed[i]);
	            ghostY[i] = ghostY[i] + (ghost_dy[i] * ghostSpeed[i]);
	            drawGhost(g, ghostX[i] + 1, ghostY[i] + 1);
	            
	            if(!isBoosted) {

	            if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12)
	                    && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12)
	                    && isRunning) {

	                isDead = true;
	            }
	            }
	        }
		}
	
	public void drawGhost(Graphics2D g , int x, int y) {
		g.setColor(Color.PINK);
		g.fillRect(x, y, blockSize, blockSize);
		//g.drawImage(ghost,x,y, this);
	}
	
	public void checkMaze() {
		//check if timer is out
		if(isBoosted) {
			endTime=System.nanoTime();
			if((long)(endTime-startTime)>=Long.valueOf("8000000000")) {
				pacmanSpeed=6;
				isBoosted=false;
			}
		}
		
		int i = 0;
        boolean finished = true;

        while (i < numBlocks * numBlocks && finished) {

            if (screenData[i] != 0) {
                finished = false;
            }
            if(numDots==0) {
            	finished =true;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (numGhosts < numGhostsMax) {
                numGhosts++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
	}
	private void death() {
		lives--;
		pacmanSpeed=6;

        if (lives == 0) {
            isRunning = false;
            
        }

        continueLevel();
	}
	
	
	public void paintComponent(Graphics g) {
		 super.paintComponent(g);

	        Graphics2D g2d = (Graphics2D) g;

	        g2d.setColor(Color.black);
	        g2d.fillRect(0, 0, d.width, d.height);

	        drawMaze(g2d);
	        drawScore(g2d);

	        if (isRunning) {
	            playGame(g2d);
	        } else {
	        	 IntroScreen screen = new IntroScreen();
	             screen.paintComponent(g2d);
	             
	        }

	        Toolkit.getDefaultToolkit().sync();
	        g2d.dispose();
	}
	
	private void continueLevel() {
		int dx=1;
		int random;
		for(int i=0;i<numGhosts;i++) {
			ghostY[i]=4*blockSize;
			ghostX[i]=4*blockSize;
			ghost_dy[i]=0;
			ghost_dx[i]=dx;
			dx=-dx;
			random=(int) Math.random()*(currentSpeed+1);
			if(random>currentSpeed) {
				random = currentSpeed;
			}
			ghostSpeed[i]=validSpeeds[random];
			
			pacmanX=7*blockSize;
			pacmanY=11*blockSize;
			pacmanDX=0;
			pacmanDY=0;
			req_dx=0;
			req_dy=0;
			isDead=false;
		}
	}
	public void drawMaze(Graphics2D g) {
		
		 short i = 0;
	        int x, y;

	        for (y = 0; y < screenSize; y += blockSize) {
	            for (x = 0; x < screenSize; x += blockSize) {

	                g.setColor(new Color(0,72,251));
	                g.setStroke(new BasicStroke(5));
	                
	                if ((levelData[i] == 0)) { 
	                	g.fillRect(x, y, blockSize, blockSize);
	                 }

	                if ((screenData[i] & 1) != 0) { 
	                    g.drawLine(x, y, x, y + blockSize - 1);
	                }

	                if ((screenData[i] & 2) != 0) { 
	                    g.drawLine(x, y, x + blockSize - 1, y);
	                }

	                if ((screenData[i] & 4) != 0) { 
	                    g.drawLine(x + blockSize - 1, y, x + blockSize - 1,
	                            y + blockSize - 1);
	                }

	                if ((screenData[i] & 8) != 0) { 
	                    g.drawLine(x, y + blockSize - 1, x + blockSize - 1,
	                            y + blockSize - 1);
	                }

	                if ((screenData[i] & 16) != 0) { 
	                    g.setColor(new Color(255,255,255));
	                    g.fillOval(x + 10, y + 10, 6, 6);
	               }
	                if ((screenData[i] & 32) != 0) { 
	                    g.setColor(Color.RED);
	                    g.fillOval(x + 10, y + 10, 6, 6);
	               }
	              

	                i++;
	            }
	        }
	}
	
	 
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}

}
