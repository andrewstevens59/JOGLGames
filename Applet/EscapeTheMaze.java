package Applet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import Applet.HitOrMiss.Square;

import com.jogamp.opengl.util.gl2.GLUT;

class EscapeTheMaze implements GLEventListener, MouseListener, MouseMotionListener 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static int SQUARE_WIDTH = 20;

	class Square {
		public boolean side[] = new boolean[4];
		public int x;
		public int y;
		
		public Square() {
			for(int i=0; i<side.length; i++) {
				side[i] = true;
			}
		}
	}
	
	class BFSNode {
		int x;
		int y;
		int depth;
		BFSNode parent;
		
		public BFSNode(int x, int y, BFSNode parent, int depth) {
			this.x = x;
			this.y = y;
			this.parent = parent;
			this.depth = depth;
		}
	}

	private boolean path_grid[][] = new boolean[60][60];
	private HashMap<Square, Integer> visit_square = new HashMap<Square, Integer>();
    private Square[][] maze = new Square[60][60];
    private GLCanvas canvas;
    private GLU glu = new GLU();
    private int score = 0;
    
    private int x_loc = -1;
    private int y_loc = -1;
    
    private boolean BuildMaze(int x, int y, int depth, int max_depth, boolean is_check) {
    	
    	if(x < 0 || y < 0 || x >= maze.length || y >= maze.length) {
    		return false;
    	}

    	Square s = maze[y][x];
    	if(depth > max_depth && (is_check == false || (Math.abs(x - (maze.length >> 1)) < 3 && Math.abs(y - (maze.length >> 1)) < 3))) {
    		
    		if(x_loc < 0) {
    			x_loc = x;
    			y_loc = y;
    		}
    		return true;
    	}

    	if(visit_square.get(s) != null && visit_square.get(s) > 0) {
    		return false;
    	}
    	
    	visit_square.put(s, visit_square.get(s) == null ? 1 : visit_square.get(s) + 1);
    	
    	Random r = new Random();
    	boolean is_found = false;
    	HashSet<Integer> val_map = new HashSet<Integer>();
    	
    	
    	while(is_found == false && val_map.size() < 4) {
	    	int val = r.nextInt(4);
	    	val_map.add(val);
	    	
	    	if(Math.random() < 0.001 && is_check == true) {
	    		maze[y][x].side[r.nextInt(4)] = false;
	    	}
	    	
	    	switch(val) {
	    	
		    	case 0: 
		    		
		    		if(BuildMaze(x - 1, y, depth + 1, max_depth, is_check) == true) {
			    		s.side[0] = false;
			    		is_found = true;
			    		
			    		if(x > 0) {
			    			maze[y][x-1].side[1] = false;
			    		}
		    		}
		    		
		    	break;
		    	case 1: 
		    		
		    		if(BuildMaze(x + 1, y, depth + 1, max_depth, is_check) == true) {
				    	s.side[1] = false;
				    	is_found = true;
				    	
				    	if(x < maze.length - 1) {
				    		maze[y][x+1].side[0] = false;
				    	}
		    		}
		    	break;
			    	
		    	case 2: 
		    		
		    		if(BuildMaze(x, y - 1, depth + 1, max_depth, is_check) == true) {
				    	s.side[2] = false;
				    	is_found = true;
				    	
				    	if(y > 0) {
				    		maze[y-1][x].side[3] = false;
				    	}
		    		}
		    	break;
		    	case 3: 
		    		
		    		if(BuildMaze(x, y + 1, depth + 1, max_depth, is_check) == true) {
				    	s.side[3] = false;
				    	is_found = true;
				    	
				    	if(y < maze.length - 1) {
				    		maze[y+1][x].side[2] = false;
				    	}

		    		}
		    	break;
	    	}
    	}
    	
    	return is_found;
    }
    
    private void Initialize() {
    	
    	x_loc = -1;
        y_loc = -1;
    	visit_square.clear();
    	for(int i=0; i<maze.length; i++) {
    		for(int j=0; j<maze.length; j++) {
    			maze[i][j] = new Square();
    			path_grid[i][j] = false;
    		}
    	}
    	
    	Random r = new Random();
		for(int i=0; i<10; i++) {
			int x = r.nextInt(maze.length);
			int y = r.nextInt(maze.length);
			
			if(Math.random() < 0.5) {
				BuildMaze(x, 0, 0, 40, i == 0);
				maze[0][x].side[2] = false;
			} else {
				BuildMaze(x, maze.length - 1, 0, 40, i == 0);
				maze[maze.length - 1][x].side[3] = false;
			}
			
			if(Math.random() < 0.5) {
				BuildMaze(0, y, 0, 40, i == 0);
				maze[y][0].side[0] = false;
			} else {
				BuildMaze(maze.length - 1, y, 0, 40, i == 0);
				maze[y][maze.length - 1].side[1] = false;
			}
		}
		
		for(int i=1; i<100; i++) {
			int x = x_loc;
			int y = y_loc;
			
			visit_square.remove(maze[y][x]);
			BuildMaze(x, y, 0, 40, i == 0);
		}
		
		for(int i=1; i<300; i++) {
			int x = r.nextInt(maze.length);
			int y = r.nextInt(maze.length);
			
			if(visit_square.get(maze[y][x]) != null) {
				continue;
			}
			
			BuildMaze(x, y, 0, 10, i == 0);
		}
		
		path_grid[y_loc][x_loc] = true;
    }

    public EscapeTheMaze(GLCanvas canvas) {
    	
    	this.canvas = canvas;
    	Initialize();
    }

    public void display(GLAutoDrawable gLDrawable) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
        final GLUT glut = new GLUT();
   
        gl.glClear (GL.GL_COLOR_BUFFER_BIT);  // Set display window to color.
        gl.glColor3f (0.0f, 0.0f, 0.0f);  // Set text e.color to black
        gl.glMatrixMode (GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        int inc = 10;
        
        for(int i=0; i<path_grid.length; i++) {
        	for(int j=0; j<path_grid.length; j++) {
        		
        		if(path_grid[i][j] == false) {
        			continue;
        		}
        		
        		int x_pos = j * inc;
                x_pos += 100;
                int y_pos = i * inc;
                y_pos += 100;
                
                gl.glColor3f (0.0f, 1.0f, 0.0f);
                gl.glBegin (GL2.GL_QUADS);
                gl.glVertex2i(x_pos, y_pos);
                gl.glVertex2i(x_pos + inc, y_pos);
                gl.glVertex2i(x_pos + inc, y_pos + inc);
                gl.glVertex2i(x_pos, y_pos + inc);
                gl.glEnd();
        	}
        }

        gl.glColor3f (0.0f, 0.0f, 0.0f);
        gl.glBegin (GL2.GL_LINES);

        for(int i=0, y_offset=100; i<maze.length; i++, y_offset+=inc) {
    		for(int j=0, x_offset=100; j<maze.length; j++, x_offset+=inc) {
    			Square s = maze[i][j];
    			
    			if(s.side[0] == true) {
    				gl.glVertex2i(x_offset, y_offset);
    		        gl.glVertex2i(x_offset, y_offset + inc);
    			}
    			
    			if(s.side[1] == true) {
    				gl.glVertex2i(x_offset + inc, y_offset);
    		        gl.glVertex2i(x_offset + inc, y_offset + inc);
    			}
    			
    			if(s.side[2] == true) {
    				gl.glVertex2i(x_offset, y_offset);
    		        gl.glVertex2i(x_offset + inc, y_offset);
    			}
    			
    			if(s.side[3] == true) {
    				gl.glVertex2i(x_offset, y_offset + inc);
    		        gl.glVertex2i(x_offset + inc, y_offset + inc);
    			}
    		}
    	}
        
        gl.glEnd();

        gl.glColor3f (0.0f, 0.0f, 0.0f); 
        gl.glRasterPos2i(50, 30);
		glut.glutBitmapString(5, "Score: "+score);
        
          gl.glEnd();				
          gl.glFlush();


    }

    // This processes a mouse event
    private void ProcessPath(int x, int y) {
    	
    	x -= 100;
    	x /= 10;
    	y -= 100;
    	y /= 10;
    	
    	if(x < 0 || y < 0 || x > maze.length - 1 || y > maze.length - 1) {
    		return;
    	}
    	
    	path_grid[y_loc][x_loc] = true;
    	Queue<BFSNode> queue = new LinkedList<BFSNode>();
    	HashSet<String> visit_map = new HashSet<String>();

    	boolean is_found = false;
    	BFSNode fn = new BFSNode(x, y, null, 0);
    	queue.add(fn);
    	while(queue.size() > 0) {
    		
    		BFSNode n = queue.remove();
    		x = n.x;
    		y = n.y;
    		
    		String str = x + " " + y;
    		if(visit_map.contains(str)) {
    			continue;
    		}

    		visit_map.add(str);
    		
    		if(path_grid[y][x] == true) {
    			is_found = true;
    			
    			while(n != null) {
    				path_grid[n.y][n.x] = true;
    				n = n.parent;
    			}
    			
    			break;
    		}
    		
    		if(n.depth > 4) {
    			break;
    		}

    		if(x > 0 && maze[y][x].side[0] == false) {
        		queue.add(new BFSNode(x - 1, y, n, n.depth + 1));
        	}
        	
        	if(x < maze.length - 2 && maze[y][x].side[1] == false) {
        		queue.add(new BFSNode(x + 1, y, n, n.depth + 1));
        	}
        	
        	if(y > 0 && maze[y][x].side[2] == false) {
        		queue.add(new BFSNode(x, y - 1, n, n.depth + 1));
        	}
        	
        	if(y < maze.length - 2 && maze[y][x].side[3] == false) {
        		queue.add(new BFSNode(x, y + 1, n, n.depth + 1));		
        	}
        	
    	}
    	
    	if(is_found == false) {
    		return;
    	}
    	
    	queue.clear();
    	queue.add(fn);
    	visit_map.clear();
    	
    	while(queue.size() > 0) {
    		
    		BFSNode n = queue.remove();
    		x = n.x;
    		y = n.y;
    		
    		String str = x + " " + y;
    		if(visit_map.contains(str)) {
    			continue;
    		}
    		
    		visit_map.add(str);
    		if(n.x == x_loc && n.y == y_loc) {
    			
    			if(fn.x == 0 && maze[fn.y][fn.x].side[0] == false) {
    				Initialize();
    				score++;
    				return;
    			}
    			
    			if(fn.x == maze.length - 1 && maze[fn.y][fn.x].side[1] == false) {
    				Initialize();
    				score++;
    				return;
    			}
    			
    			if(fn.y == 0 && maze[fn.y][fn.x].side[2] == false) {
    				Initialize();
    				score++;
    				return;
    			}
    			
    			if(fn.y == maze.length - 1 && maze[fn.y][fn.x].side[3] == false) {
    				Initialize();
    				score++;
    				return;
    			}

    			for(int i=0; i<path_grid.length; i++) {
    				for(int j=0; j<path_grid.length; j++) {
    					path_grid[i][j] = false;
    				}
    			}

    			while(n != null) {
    				path_grid[n.y][n.x] = true;
    				n = n.parent;
    			}
    			
    			break;
    		}
    		
    		if(x > 0 && maze[y][x].side[0] == false && path_grid[y][x-1] == true) {
        		queue.add(new BFSNode(x - 1, y, n, n.depth + 1));
        		System.out.println("0");
        	}
        	
        	if(x < maze.length - 2 && maze[y][x].side[1] == false && path_grid[y][x+1] == true) {
        		queue.add(new BFSNode(x + 1, y, n, n.depth + 1));
        		System.out.println("1");
        	}
        	
        	if(y > 0 && maze[y][x].side[2] == false && path_grid[y-1][x] == true) {
        		queue.add(new BFSNode(x, y - 1, n, n.depth + 1));
        		System.out.println("2");
        	}
        	
        	if(y < maze.length - 2 && maze[y][x].side[3] == false && path_grid[y+1][x] == true) {
        		queue.add(new BFSNode(x, y + 1, n, n.depth + 1));
        		System.out.println("3");
        	}
    	}
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

		ProcessPath(x, y);

		canvas.display();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		int x = (int) (arg0.getX() * (float)(800.0f / 785.0f));
		int y = (int) (arg0.getY() * (float)(800.0f / 760.0f));

		ProcessPath(x, y);

		canvas.display();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}