package Graph;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

class Hikenashi implements GLEventListener, MouseListener, MouseMotionListener 
{
	
	class Bar {
		Double low = Double.MAX_VALUE;
		Double high = -Double.MAX_VALUE;
		Double open;
		Double close;
		Double volume;
		int tick_offset;
	}
	
	// This stores the set of squares
	private ArrayList<Bar> bar_set = new ArrayList<Bar>();
	// This stores the one hour period
	private ArrayList<Bar> period_set = new ArrayList<Bar>();
    private GLCanvas canvas;
    private GLU glu = new GLU();
    private int x_click = -1;
    private int prev_x_click = -1;
    private int start_series = 0;


    public Hikenashi(GLCanvas canvas) throws IOException {
    	
    	this.canvas = canvas;
    	
    	Bar b = new Bar();
    	Bar b1 = new Bar();
    	BufferedReader br = new BufferedReader(new FileReader("AUDCAD_UTC_Daily_Bid_2008.01.01_2012.12.31.csv"));
    	String line;
    	br.readLine();
    	int count = 0;
    	int count1 = 0;
    	while ((line = br.readLine()) != null) {
    		String set[] = line.split(" ");
    		Double open = Double.parseDouble(set[2]);
    		double volume = Double.parseDouble(set[6]);
    		
    		System.out.println(volume);
    		
    		if(count == 0) {
    			b = new Bar();
    			
    			if(bar_set.size() == 0) {
    				b.open = open;
    			} else {
    				b.open = bar_set.get(bar_set.size()-1).close;
    			}
    			
    			b.volume = volume;
    		}
    		
    		if(count1 == 0) {
    			b1 = new Bar();
    			
    			if(period_set.size() == 0) {
    				b1.open = open;
    			} else {
    				b1.open = period_set.get(period_set.size()-1).close;
    			}
    		}
    		
    		b.high = Math.max(b.high, open);
    		b.low = Math.min(b.low, open);
    		
    		b1.high = Math.max(b1.high, open);
    		b1.low = Math.min(b1.low, open);
    		
    		if(++count1 >= 60) {
    			b1.close = open;
    			if(Math.abs(b1.close - b1.open) > 0.0005) {
    				period_set.add(b1);
    			}
    			count1 = 0;
    		}
    		
    		if(++count >= 1) {
    			b.close = open;
    			b.tick_offset = period_set.size() - 1;
    			if(b.volume > 0) {
    				bar_set.add(b);
    			}
    			count = 0;
    		}
    	}
    	br.close();
    }

    public void display(GLAutoDrawable gLDrawable) 
    {
    	Random r = new Random();
    	if(prev_x_click < 0) {
	    	start_series = r.nextInt(bar_set.size()) - 100;
    		prev_x_click = x_click;
    	} else {
    		
    		if(x_click < prev_x_click) {
    			return;
    		}

    		prev_x_click = -1;
    	}

        final GL2 gl = gLDrawable.getGL().getGL2();
        final GLUT glut = new GLUT();
   
        gl.glClear (GL.GL_COLOR_BUFFER_BIT);  // Set display window to color.
        gl.glColor3f (0.0f, 0.0f, 0.0f);  // Set text e.color to black
        gl.glMatrixMode (GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        int start = start_series;
        int end = Math.min(start_series + 200, Math.min(bar_set.size(), start_series + (x_click / 8) + 2));
        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        
        double max_vol = 0;
        for(int i=start; i<end; i++) {
        	min = Math.min(min, bar_set.get(i).open);
        	max = Math.max(max, bar_set.get(i).open);
        	min = Math.min(min, bar_set.get(i).close);
        	max = Math.max(max, bar_set.get(i).close);
        	
        	max_vol += bar_set.get(i).volume;
        }
        
        
        max_vol /= (end - start);
        max_vol *= 3;
        double scale = 500 / (max - min);

        double offset = 0;
        double jump = 800.0f / 100;
        double mid = (max + min) / 2;
        double prev_open = 800 - (((bar_set.get(start).open - mid) * scale) + 400);
        double prev_close = 800 - (((bar_set.get(start).close - mid) * scale) + 400);

        for(int i=start + 1; i<end; i++) {

        	Bar b = bar_set.get(i);
        	double open = 800 - (((bar_set.get(i).open - mid) * scale) + 400);
        	double close = 800 - (((bar_set.get(i).close - mid) * scale) + 400);
        	double high = 800 - (((bar_set.get(i).high - mid) * scale) + 400);
        	double low = 800 - (((bar_set.get(i).low - mid) * scale) + 400);
        	
        	double xClose = (open+high+low+close)/4;
            double xOpen = (prev_open + prev_close)/2;
            
         
            double vol_scale = max_vol * 0.0001;
            gl.glColor3f (0.6f, 0.6f, 0.6f);
        	gl.glBegin (GL2.GL_QUADS);
        	gl.glVertex2d(offset, 800);
        	gl.glVertex2d(offset + jump, 800);
        	gl.glVertex2d(offset + jump, 800 - ((b.volume / max_vol) * 400));
        	gl.glVertex2d(offset, 800 - ((b.volume / max_vol) * 400));
        	gl.glEnd();
            
        	if(xClose > xOpen) {
        		gl.glColor3f (1.0f, 0.0f, 0.0f); 
        	} else {
        		gl.glColor3f (0.0f, 1.0f, 0.0f); 
        	}
        	
        	gl.glBegin (GL2.GL_QUADS);
        	gl.glVertex2d(offset, xOpen);
        	gl.glVertex2d(offset + jump, xOpen);
        	gl.glVertex2d(offset + jump, xClose);
        	gl.glVertex2d(offset, xClose);
        	gl.glEnd();
        	
        	
        	prev_open = xOpen;
            prev_close = xClose;
            
            gl.glColor3f (0.0f, 0.0f, 0.0f); 
            
        	offset += jump;
        }
        			
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
        gl.glClearColor (0.0f, 0.0f, 0.0f, 0.0f);   //set background to white
        glu.gluOrtho2D (0.0, 1000.0, 800.0, 0.0);  // define drawing area
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
		
		x_click = x;
		
		if(SwingUtilities.isRightMouseButton(arg0) == true) {
			prev_x_click = -1;
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
	
	public static void main( String [] args ) throws IOException {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        Hikenashi hm = new Hikenashi(glcanvas);
        glcanvas.addGLEventListener(hm);
        glcanvas.addMouseListener(hm);
    	glcanvas.addMouseMotionListener(hm);
    	
    	FPSAnimator animator = new FPSAnimator(glcanvas, 60);
    	animator.start();       // start the animator

        final Frame frame = new Frame( "One Triangle AWT" );
        frame.add( glcanvas );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                frame.remove( glcanvas );
                frame.dispose();
                System.exit( 0 );
            }
        });

        frame.setSize( 1000, 800 );
        frame.setVisible( true );
    }
}