import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import javax.sound.sampled.*;

public class DracarysVoyage extends JPanel implements ActionListener,KeyListener{
    int boardWidth = 360;
    int boardHeight = 640;

    Image backGroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdx=boardWidth/8;
    int birdy=boardHeight/2;
    int birdWidth=70;
    int birdHeight=70;
  
    class Bird{
    	int x= birdx;
    	int y= birdy;
    	int width = birdWidth;
    	int height = birdHeight;
    	Image img;
    	
    	Bird(Image img){
    		this.img=img;
    	}
    }
    
    //Pipes
    int pipeX=boardWidth;
    int pipeY=0;
    int pipewidth=64;
    int pipeheight=512;
    
    class Pipe{
    	int x=pipeX;
    	int y=pipeY;
    	int width=pipewidth;
    	int height=pipeheight;
    	Image img;
    	boolean passed =false;
    	
    	Pipe(Image img){
    		this.img=img;
    	}

		public DracarysVoyage.Pipe get(int i) {
			// TODO Auto-generated method stub
			return null;
		}
    }
    
    //game logic
    Bird bird;
    int velocityX=-4;
    int velocityY=0;
    int gravity=1;
    
    ArrayList<Pipe> pipes;
    Random random = new Random();
    
    
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver=false;
    double score=0;
    
    // Constructor
    DracarysVoyage() {
    	
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.PINK);
        setFocusable(true);
        addKeyListener(this);
        // Load images
        try {
            backGroundImg = new ImageIcon(getClass().getResource("/Images/bg.jpg")).getImage();
            birdImg = new ImageIcon(getClass().getResource("/Images/dae.png")).getImage();
            topPipeImg = new ImageIcon(getClass().getResource("/Images/topfire8.png")).getImage();
            bottomPipeImg = new ImageIcon(getClass().getResource("/Images/bottomfire8.jpg")).getImage();
        } catch (Exception e) {
            System.out.println("Error loading images. Please check the file paths!"+ e.getMessage());
        }
        
        // Play background music
        playMusic("/Images/BGM.wav"); 
        
        
        //bird
        bird= new Bird(birdImg);
        
        //pipes
        pipes= new ArrayList<Pipe>();
        
        //place pipes timer
        placePipesTimer= new Timer(1500,new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		placePipes();
        	}
        });
        
        placePipesTimer.start();
        //game Timer
        gameLoop= new Timer(1000/60,this);//1000/60=16.6
        gameLoop.start();
    }
    
    public void placePipes() {
    	int randomPipeY=(int)(pipeY-pipeheight/4-Math.random()*(pipeheight/2));
    	int openingSpace=boardHeight/2;
    	
    	Pipe topPipe= new Pipe(topPipeImg);
    	topPipe.y=randomPipeY;
    	pipes.add(topPipe);
    	
    	Pipe bottomPipe= new Pipe(bottomPipeImg);
    	bottomPipe.y=topPipe.y+pipeheight+openingSpace;
    	pipes.add(bottomPipe);
    }

    // Paint component
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    //Playing Music
    public void playMusic(String musicFile) {
    	try {
    		AudioInputStream audioStream =AudioSystem.getAudioInputStream(getClass().getResource(musicFile));
    		Clip clip =AudioSystem.getClip();
    		clip.open(audioStream);
    		clip.loop(Clip.LOOP_CONTINUOUSLY);
    		clip.start();
    	}
    	catch(Exception e) {
    		System.out.println("Error playing music: "+e.getMessage());
    	}
    }

    //Playing SoundEffects
    public void playSound(String soundFile) {
    	try {
    		AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
    		Clip clip =AudioSystem.getClip();
    		clip.open(audioStream);
    		clip.start();
    	}
    	catch(Exception e) {
    		System.out.println("Error playing Sound: "+e.getMessage());
    	}
    }
    
    // Draw images
    public void draw(Graphics g) {
    	//background
        g.drawImage(backGroundImg, 0, 0, boardWidth, boardHeight, null);
        
        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for(int i=0;i<pipes.size();i++) {
        	Pipe pipe =pipes.get(i);
        	g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
        }
        //score
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial",Font.BOLD,32));
        if(gameOver) {
        	g.drawString("Game Over: "+String.valueOf((int)score),85,320);
        }
        else {
        	g.drawString(String.valueOf((int)score),10,35);
        }
        
    }
    
    public void move() {
    	velocityY+=gravity;
        bird.y += velocityY; // Update the bird's position
        bird.y = Math.max(bird.y,0);// Ensure the bird doesn't fall below the screen
        
        for(int i=0;i<pipes.size();i++) {
        	Pipe pipe =pipes.get(i);
        	pipe.x+=velocityX;
        	
        	if(!pipe.passed && bird.x>pipe.x+pipe.width) {
        		pipe.passed=true;
        		score+=0.5;
        	}
        	
        	if(collision(bird,pipe)) {
        		playSound("/Images/collision.wav");
        		gameOver=true;
        	}
        }
        if(bird.y>boardHeight) {
        	playSound("/Images/collision.wav");
        	gameOver=true;
        }
    }
    
    public boolean collision(Bird a,Pipe b) {
    	return a.x<b.x+b.width &&
    		   a.x+a.width>b.x &&
    		   a.y<b.y+b.height &&
    		   a.y+a.height>b.y;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	move();
        repaint(); // Request repainting to reflect updated positions
        if(gameOver) {
        	placePipesTimer.stop();
        	gameLoop.stop();
        }
    }

	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			playSound("/Images/flap.wav");
			velocityY=-9;
			if(gameOver) {
				bird.y=birdy;
				velocityY=0;
				pipes.clear();
				score=0;
				gameOver=false;
				gameLoop.start();
				placePipesTimer.start();        
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}


	@Override
	public void keyReleased(KeyEvent e) {}
}
