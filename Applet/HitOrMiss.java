package Applet;


import java.applet.Applet;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLBase;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

class HitOrMiss implements GLEventListener, MouseListener, MouseMotionListener 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static int SQUARE_WIDTH = 20;

	class Square {
		public double x_loc;
		public double y_loc;
		
		public double x_vec;
		public double y_vec;
		
		public boolean is_bad;
		
		public Square() {
			Relocate();
		}
		
		public void Mutate() {
			x_vec = Math.random();
			y_vec = Math.random();
			
			is_bad = Math.random() > 0.1;
		}
		
		public void Relocate() {
			x_loc = Math.random() * HitOrMissApplet.WIDTH;
			y_loc = Math.random() * HitOrMissApplet.WIDTH;
			
			x_vec = Math.random() - 0.5;
			y_vec = Math.random() - 0.5;
			
			is_bad = Math.random() < 0.1;
		}
		
		public void Reroute() {
			x_vec = Math.random() - 0.5;
			y_vec = Math.random() - 0.5;
		}
		
		public void UpdatePos() {
			x_loc += x_vec * 5;
			y_loc += y_vec * 5;
			
			if(x_loc < 0 || y_loc < 0 || x_loc >= HitOrMissApplet.WIDTH || y_loc >= HitOrMissApplet.WIDTH) {
				x_loc = Math.random() * HitOrMissApplet.WIDTH;
				y_loc = Math.random() * HitOrMissApplet.WIDTH;
				is_bad = Math.random() < 0.1;
			}
		}
		
		public boolean IsContained(int x, int y) {
			
			double x_diff = x - x_loc;
			double y_diff = y - y_loc;

			if(Math.abs(x_diff) > SQUARE_WIDTH) {
				return false;
			}
			
			if(Math.abs(y_diff) > SQUARE_WIDTH) {
				return false;
			}
			
			return true;
		}
		
		public boolean IsIntersect(Square s) {
			
			double x_diff = s.x_loc - x_loc;
			double y_diff = s.y_loc - y_loc;

			if(Math.abs(x_diff) > SQUARE_WIDTH) {
				return false;
			}
			
			if(Math.abs(y_diff) > SQUARE_WIDTH) {
				return false;
			}
			
			return true;
		}
	}

	// This stores the set of squares
	private ArrayList<Square> square_set = new ArrayList<Square>();
    private GLCanvas canvas;
    private GLU glu = new GLU();
    private int score = 0;

    public HitOrMiss(GLCanvas canvas) {
    	
    	this.canvas = canvas;
    	
    	for(int i=0; i<100; i++) {
    		square_set.add(new Square());
    	}
    }

    public void display(GLAutoDrawable gLDrawable) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
        final GLUT glut = new GLUT();
   
        gl.glClear (GL.GL_COLOR_BUFFER_BIT);  // Set display window to color.
        gl.glColor3f (0.0f, 0.0f, 0.0f);  // Set text e.color to black
        gl.glMatrixMode (GL2.GL_MODELVIEW);
        gl.glLoadIdentity();


        gl.glBegin (GL2.GL_QUADS);
        
        for(Square s : square_set) {
        	
	       	 if(s.is_bad == false) {
	       		continue;
	       	 }
	       	 
	       	 gl.glColor3f (0.0f, 1.0f, 0.0f); 
	       	 gl.glVertex2d(s.x_loc - SQUARE_WIDTH, s.y_loc - SQUARE_WIDTH);
	       	 gl.glVertex2d(s.x_loc + SQUARE_WIDTH, s.y_loc - SQUARE_WIDTH);
	       	 gl.glVertex2d(s.x_loc + SQUARE_WIDTH, s.y_loc + SQUARE_WIDTH);
	       	 gl.glVertex2d(s.x_loc - SQUARE_WIDTH, s.y_loc + SQUARE_WIDTH);
	       	 
	       	 s.UpdatePos();
      }
        
        for(Square s : square_set) {
        	
        	 if(s.is_bad == true) {
        		 continue;
        	 }
        	 
        	 gl.glColor3f (1.0f, 0.0f, 0.0f); 
        	 gl.glVertex2d(s.x_loc - SQUARE_WIDTH, s.y_loc - SQUARE_WIDTH);
        	 gl.glVertex2d(s.x_loc + SQUARE_WIDTH, s.y_loc - SQUARE_WIDTH);
        	 gl.glVertex2d(s.x_loc + SQUARE_WIDTH, s.y_loc + SQUARE_WIDTH);
        	 gl.glVertex2d(s.x_loc - SQUARE_WIDTH, s.y_loc + SQUARE_WIDTH);
        	 
        	 s.UpdatePos();
        }
        
        gl.glColor3f (1.0f, 1.0f, 1.0f); 
        gl.glVertex2d(0, 0);
        gl.glVertex2d(HitOrMissApplet.WIDTH, 0);
        gl.glVertex2d(HitOrMissApplet.WIDTH, 50);
        gl.glVertex2d(0, 50);

        gl.glEnd();

        gl.glColor3f (0.0f, 0.0f, 0.0f); 
        gl.glRasterPos2i(50, 30);
		glut.glutBitmapString(5, "Score: "+score);
        
          gl.glEnd();				
          gl.glFlush();


    }

 
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) 
    {
    	System.out.println("displayChanged called");
    }
 
    public void init(GLAutoDrawable gLDrawable) 
    {
    	System.out.println("init() called");
        GL2 gl = gLDrawable.getGL().getGL2();
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
    }
 
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
 
        gl.glMatrixMode (GL2.GL_PROJECTION); 
        gl.glClearColor (1.0f, 1.0f, 1.0f, 0.0f);   //set background to white
        glu.gluOrtho2D (0.0, 800.0, 800.0, 0.0);  // define drawing area
    }
 
 
	public void dispose(GLAutoDrawable arg0) 
	{
		System.out.println("dispose() called");
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		int x = (int) (arg0.getX() * (float)(800.0f / 785.0f));
		int y = (int) (arg0.getY() * (float)(800.0f / 760.0f));

		boolean is_found = false;
		for(Square s : square_set) {
			if(s.is_bad == true) {
				continue;
			}
			if(s.IsContained(x, y)) {
				s.Relocate();
				score--;
				is_found = true;
			}
		}
		
		for(Square s : square_set) {
			if(s.is_bad == false) {
				continue;
			}
			if(s.IsContained(x, y)) {
				s.Relocate();
				
				if(is_found == false) {
					score++;
				}
			}
		}
		
		canvas.display();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}