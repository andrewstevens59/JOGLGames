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

class TextRenderer implements GLEventListener, MouseListener, MouseMotionListener 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class SWord {
		String word;
		int id;
		
		public SWord(int id, String word) {
			this.id = id;
			this.word = word;
		}
	}
	
    private GLU glu = new GLU();
    private char word_grid[][];
    private boolean highlight_grid[][];
    
    private int inc = 40;
    private int width = inc * 14;
    // This stores the set of words that have been found
    private HashMap<String, Boolean> found_word_set = new HashMap<String, Boolean>();
    
    private int x_down;
    private int y_down;
    private int x_curr = -1;
    private int y_curr = -1;
    private GLCanvas canvas;
    
    private String words[] = {"apple", "zookeeper", "orange", "hospital", "airport", "zoologist", "education", "australia", "neptune",
    		"venus", "university", "subway", "holidays", "seasons", "recreation", "rainforest"};
    
    // This places a word on tbe grid
    private boolean PlaceWord(String str, int grid_size, int max_it) {
    	
    	Random r = new Random();
    	
    	for(int k=0; k<max_it; k++) {
    		
    		boolean is_found = true;
    		int x = r.nextInt(grid_size);
        	int y = r.nextInt(grid_size);
        	
	    	for(int i=0; i<str.length(); i++) {
	    		if(x + i + 1 >= grid_size) {
	    			is_found = false;
	    			break;
	    		}
	    		
	    		if(word_grid[y][x + i] != '-' && word_grid[y][x + i] != str.charAt(i)) {
	    			is_found = false;
	    			break;
	    		}
	    	}
	    	
	    	if(is_found == true) {
	    		for(int i=0; i<str.length(); i++) {
	    			word_grid[y][x + i] = str.charAt(i);
	    		}
	    		return true;
	    	}
	    	
	    	is_found = true;
    		x = r.nextInt(grid_size);
        	y = r.nextInt(grid_size);
        	
	    	for(int i=0; i<str.length(); i++) {
	    		if(y + i + 1 >= grid_size) {
	    			is_found = false;
	    			break;
	    		}
	    		
	    		if(word_grid[y + i][x] != '-' && word_grid[y + i][x] != str.charAt(i)) {
	    			is_found = false;
	    			break;
	    		}
	    	}
	    	
	    	if(is_found == true) {
	    		for(int i=0; i<str.length(); i++) {
	    			word_grid[y + i][x] = str.charAt(i);
	    		}
	    		
	    		return true;
	    	}
    	}

    	return false;
    }
    
    public TextRenderer(GLCanvas canvas) {
    	
    	this.canvas = canvas;
    	
    	int grid_size = 0;
    	int y_offset = inc;
        while(y_offset <= width) {
	        grid_size++;
	        y_offset += inc;
        }
        
    	word_grid = new char[grid_size][grid_size];
    	highlight_grid = new boolean[grid_size][grid_size];
        for(int i=0; i<grid_size; i++) {
        	for(int j=0; j<grid_size; j++) {
        		word_grid[i][j] = '-';
        		highlight_grid[i][j] = false;
        	}
        }

        Random r = new Random();
        ArrayList<SWord> buff = new ArrayList<SWord>();
        for(int i=0; i<words.length; i++) {
        	buff.add(new SWord(r.nextInt(words.length), words[i]));
        }
        
        Collections.sort(buff, new Comparator<SWord>() {

			@Override
			public int compare(SWord arg0, SWord arg1) {
				if(arg0.id < arg1.id) {
					return -1;
				}
				
				if(arg0.id > arg1.id) {
					return 1;
				}
				
				return 0;
			}
        });
        
        for(int i=0; i<Math.min(8, words.length); i++) {
        	if(PlaceWord(buff.get(i).word, grid_size, 100) == true) {
        		found_word_set.put(buff.get(i).word, false);
        	}
        }

        for(int i=0; i<grid_size; i++) {
        	for(int j=0; j<grid_size; j++) {
        		if(word_grid[i][j] == '-') {
        			word_grid[i][j] = (char) ('a' + r.nextInt(26));
        		}
        	}
        }
    }
    
    private ArrayList<Integer> HighlightSet() {
    	
    	if(x_curr < 0 || y_curr < 0) {
    		return null;
    	}
    		
    	int x = TransformCoord(x_curr);
    	int y = TransformCoord(y_curr);
    	
    	if(x_down < 0 || x < 0 || y_down < 0 || y < 0) {
    		return null;
    	}
    	
    	if(x_down >= word_grid.length || x >= word_grid.length || y_down >= word_grid.length || y >= word_grid.length) {
    		return null;
    	}
    	
    	int x_delta = x - x_down;
    	int y_delta = y - y_down;
    	
    	int x_min = Math.min(x, x_down);
    	int x_max = Math.max(x, x_down);
    	
    	int y_min = Math.min(y, y_down);
    	int y_max = Math.max(y, y_down);
    	
    	
    	String word = new String();
    	ArrayList<Integer> buff = new ArrayList<Integer>();
    	if(Math.abs(y_delta) > Math.abs(x_delta)) {
    		
    		while(y_min <= y_max) {
    			word += word_grid[y_min][x_down];
    			buff.add(x_down);
    			buff.add(y_min);
    			y_min++;
    		}
    	} else {
    		
    		while(x_min <= x_max) {
    			word += word_grid[y_down][x_min];
    			buff.add(x_min);
    			buff.add(y_down);
    			x_min++;
    		}
    	}

    	ArrayList<Integer> temp_buff = new ArrayList<Integer>();
    	for(int i=0; i<buff.size(); i+=2) {
    		
    		if(highlight_grid[buff.get(i+1)][buff.get(i)] == false) {
    			temp_buff.add(buff.get(i));
    			temp_buff.add(buff.get(i+1));
    		}
    		
    		highlight_grid[buff.get(i+1)][buff.get(i)] = true;
    	}
    	
    	if(found_word_set.get(word) != null) {
    		found_word_set.put(word, true);
    		return null;
    	}
    	
    	return temp_buff;
    }
 
    public void display(GLAutoDrawable gLDrawable) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
        final GLUT glut = new GLUT();
   
        ArrayList<Integer> coord_buff = HighlightSet();
        gl.glClear (GL.GL_COLOR_BUFFER_BIT);  // Set display window to color.
        gl.glColor3f (0.0f, 0.0f, 0.0f);  // Set text e.color to black
        gl.glMatrixMode (GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        int y_offset = inc;
        gl.glBegin (GL.GL_LINES);
        for(int i=0; i<word_grid.length+1; i++) {
	        gl.glVertex2i (inc, y_offset);
	        gl.glVertex2i (width + inc, y_offset);
	        y_offset += inc;
        }
        
        int x_offset = inc;
        for(int i=0; i<word_grid.length+1; i++) {
	        gl.glVertex2i (x_offset, inc);
	        gl.glVertex2i (x_offset,  width + inc);
	        x_offset += inc;
        }
        
        gl.glEnd();

        for(int y=inc, j=0; j<word_grid.length; y+=inc, j++) {
        	for(int x=inc, i=0; i<word_grid.length; x+=inc, i++) {

        		if(highlight_grid[j][i] == true) {
	    			gl.glColor3f (0.0f, 1.0f, 0.0f); 
	    		} else {
	    			gl.glColor3f (0.0f, 0.0f, 0.0f); 
	    		}
        		
        		gl.glRasterPos2i(x + 16, y + 28);
        		glut.glutBitmapString(5, String.valueOf(word_grid[j][i]));
        	}
        }
        
        gl.glColor3f (0.0f, 0.0f, 0.0f); 
        
        int offset = 0;
        int count = 0;
        String word_str = new String();
        for(int i=0; i<words.length; i++) {
        	if(found_word_set.get(words[i]) != null && found_word_set.get(words[i]) != true) {
        		
        		if(count >= 4) {
        			count = 0;
        			gl.glRasterPos2i(50, WordPuzzleApplet.WIDTH - (50 * (offset + 1)));
        			glut.glutBitmapString(5, word_str);
        			offset++;
        			word_str = new String();
        		}
        		
        		word_str += words[i] + "     ";
				
        		count++;
        	}
        }
        
        gl.glRasterPos2i(50, WordPuzzleApplet.WIDTH - (50 * (offset + 1)));
		glut.glutBitmapString(5, word_str);
        
          gl.glEnd();				
          gl.glFlush();
          
        if(coord_buff != null) {
          for(int i=0; i<coord_buff.size(); i+=2) {
      		highlight_grid[coord_buff.get(i+1)][coord_buff.get(i)] = false;
      	  }
        }
    }

    private int TransformCoord(int coord) {
    	
    	coord -= inc;
    	coord /= inc;
    	
    	return coord;
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
		// TODO Auto-generated method stub
		x_down = TransformCoord(arg0.getX());
		
		double y_val = arg0.getY();
		y_val *= (800.0f / 760.0f);
		y_down = TransformCoord((int)y_val);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		x_curr = -1;
		y_curr = -1;
		canvas.display();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		int x = (int) (arg0.getX() * (float)(800.0f / 785.0f));
		int y = (int) (arg0.getY() * (float)(800.0f / 760.0f));
		
		x_curr = x;
		// TODO Auto-generated method stub
		y_curr = y;
		canvas.display();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}