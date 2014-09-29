package Vortex;

import java.awt.*;



import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import java.io.File;

import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.format.AudioFormat;
import javax.sound.sampled.AudioSystem;

import com.sun.media.BasicPlayer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

/**
 * JOGL 2.0 Example 2: Rotating 3D Shapes (GLCanvas)
 */
@SuppressWarnings("serial")
public class JOGL2Ex2Rotate3D_GLCanvas implements GLEventListener, KeyListener, MouseListener, Game  {
	
	class Point3D {
		double val[] = new double[3];
		double t = 0;
		
		boolean is_display = true;
		
		public Point3D() {
			
			
			for(int i=0; i<3; i++) {
				val[i] = 0;
			}
		}
		
		public Point3D(Point3D p) {

			t = p.t - 10;
			
			for(int i=0; i<2; i++) {
				val[i] = (float) (p.val[i] + (200 * (Math.random() - 0.5)));
			}
			
			val[2] = p.val[2] - 100;
		}
	}
	
   // Define constants for the top-level container
   private static String TITLE = "Rotating 3D Shapes (GLCanvas)";  // window's title
   private static int CANVAS_WIDTH = 320;  // width of the drawable
   private static int CANVAS_HEIGHT = 240; // height of the drawable
   private static final int FPS = 60; // animator's target frames per second
   
   private HashMap<String, Float> vertex_map = new HashMap<String, Float>();
   private Queue<Point3D> point_queue =  new LinkedList<Point3D>();
   private Queue<Point3D> control_queue =  new LinkedList<Point3D>();
   private Queue<Point3D> target_queue = new LinkedList<Point3D>();
   private Queue<Point3D> particle_queue = new LinkedList<Point3D>();
   private double t_offset = 0;
   private TextRenderer textRenderer;
   
   private Texture tr;
   private int textureid;
   private int asteroidid;
   private double xangle = 0.0f;
   private double yangle = 0.0f;
   private double prev_loc[] = new double[]{0, 0, 0};
   private boolean is_left = false;
   private boolean is_right = false;
   private boolean is_up = false;
   private boolean is_down = false;
   private double loc_offset[] = new double[]{0, 0};
   private double mouse_pos[] = new double[]{0, 0};
   private int count_down = 10;
   private Player player = null;
 
   // Setup OpenGL Graphics Renderer
 
   private GLU glu;  // for the GL Utility
   private float anglex = 0;    // rotational angle in degree for pyramid
   private float angley = 0;    // rotational angle in degree for pyramid
   private float anglez = 0;    // rotational angle in degree for pyramid
 
   /** Constructor to setup the GUI for this Component */
   public JOGL2Ex2Rotate3D_GLCanvas() {
      
      Point3D p = new Point3D();
      for(int i=0; i<300; i++) {
    	  control_queue.add(p);
    	  Point3D temp = new Point3D(p);
    	  p = temp;
      }
      
      Spline sp[] = new Spline[3];
      ArrayList<Point3D> point_buff = new ArrayList<Point3D>();
      for(Point3D p1 : control_queue) {
    	  point_buff.add(p1);
      }
      

      for(int i=0; i<3; i++) {
	      int offset = 0;
	      double xval[] = new double[point_buff.size()];
	      double yval[] = new double[point_buff.size()];
	      for(int j=point_buff.size()-1; j>=0; j--) {
	    	  xval[offset] = point_buff.get(j).t;
	    	  yval[offset++] = point_buff.get(j).val[i];
	      }
	      
	      sp[i] = new Spline(xval, yval);
      }
      
      int target_count = 0;
      for(int i=0; i<point_buff.size()-1; i++) {
    	  Point3D p1 = point_buff.get(i);
    	  Point3D p2 = point_buff.get(i+1);
    	  
    	  double inc = 0.1;
    	  for(double t=p1.t; t>p2.t; t-=inc) {
    		  
    		  Point3D p3 = new Point3D();
    		  p3.t = t;
			  for(int j=0; j<3; j++) {
				  p3.val[j] = sp[j].getValue(t);
			  }
			  
			  point_queue.add(p3);
			  
			  
			  if((point_queue.size() % 20) == 0 && target_count < 20) {
				  Point3D p4 = new Point3D();
		    	  for(int j=0; j<3; j++) {
		    		  p4.val[j] = (Math.random() - 0.5) * 7;
		    		  p4.val[j] += p3.val[j];
		    	  }
		    	  
		    	  p4.t = t;
		    	  target_queue.add(p4);
		    	  target_count++;
			  }
    	  }
      }
      
   }
 
   // ------ Implement methods declared in GLEventListener ------
 
   /**
    * Called back immediately after the OpenGL context is initialized. Can be used
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
      glu = new GLU();                         // get GL Utilities
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
      gl.glEnable(GL_TEXTURE_2D);
      

      try {
          String filename="C:/wamp/www/GameExchange/JOGL/space.jpg";
          File fs = new File(filename);
          tr = TextureIO.newTexture(fs, true);
          textureid = tr.getTextureObject(gl);
      } catch (IOException ex) {
          System.out.println(ex.getMessage());
      }
      
      try {
          String filename="C:/wamp/www/GameExchange/JOGL/asteroid.jpg";
          File fs = new File(filename);
          tr = TextureIO.newTexture(fs, true);
          asteroidid = tr.getTextureObject(gl);
      } catch (IOException ex) {
          System.out.println(ex.getMessage());
      }
      
      Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
		Format input2 = new AudioFormat(AudioFormat.MPEG);
		Format output = new AudioFormat(AudioFormat.LINEAR);
		PlugInManager.addPlugIn(
			"com.sun.media.codec.audio.mp3.JavaDecoder",
			new Format[]{input1, input2},
			new Format[]{output},
			PlugInManager.CODEC
		);
		try{
			player = Manager.createPlayer(new MediaLocator(new File("SpaceMountainDisneylandThemeSong1.mp3").toURI().toURL()));
			player.start();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 100));
		Exchange.SetStartTime();
   } 
 
   /**
    * Call-back handler for window re-size event. Also called when the drawable is
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
      
      CANVAS_WIDTH = width;
      CANVAS_HEIGHT = height;
 
      if (height == 0) height = 1;   // prevent divide by zero
      float aspect = (float)width / height;
 
      // Set the view port (display area) to cover the entire window
      gl.glViewport(0, 0, width, height);
 
      // Setup perspective projection, with aspect ratio matches viewport
      gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
      gl.glLoadIdentity();             // reset projection matrix
      glu.gluPerspective(45.0, aspect, 0.1, 10000000.0); // fovy, aspect, zNear, zFar
 
      // Enable the model-view transform
      gl.glMatrixMode(GL_MODELVIEW);
      gl.glLoadIdentity(); // reset
   }
   
   public float[] getFirstPerpVector(float x, float y, float z) {
	   float[] result = {0.0f,0.0f,0.0f};
	   // That's easy.
	   if (x == 0.0f || y == 0.0f || z == 0.0f) {
	     if (x == 0.0f)
	       result[0] = 1.0f;
	     else if (y == 0.0f)
	       result[1] = 1.0f;
	     else
	       result[2] = 1.0f;
	   }
	   else {
	     // If xyz is all set, we set the z coordinate as first and second argument .
	     // As the scalar product must be zero, we add the negated sum of x and y as third argument
	     result[0] = z;      //scalp = z*x
	     result[1] = z;      //scalp = z*(x+y)
	     result[2] = -(x+y); //scalp = z*(x+y)-z*(x+y) = 0
	     // Normalize vector
	     float length = 0.0f;
	     for (float f : result)
	       length += f*f;
	     length = (float) Math.sqrt(length);
	     for (int i=0; i<3; i++)
	       result[i] /= length;
	   }
	   return result;
	 }

	 public void drawCylinder(GL2 gl, float x1, float y1, float z1, float x2, float y2, float z2, float text_offset) throws GLException, IOException {
	   final int X = 0,
	             Y = 1,
	             Z = 2;
	   // Get components of difference vector
	   float x = x1-x2,
	         y = y1-y2,
	         z = z1-z2;
	   float[] firstPerp = getFirstPerpVector(x,y,z);
	   // Get the second perp vector by cross product
	   float[] secondPerp = new float[3];
	   secondPerp[X] = y*firstPerp[Z]-z*firstPerp[Y];
	   secondPerp[Y] = z*firstPerp[X]-x*firstPerp[Z];
	   secondPerp[Z] = x*firstPerp[Y]-y*firstPerp[X];
	   // Normalize vector
	   float length = 0.0f;
	   for (float f : secondPerp)
	     length += f*f;
	   length = (float) Math.sqrt(length);
	   for (int i=0; i<3; i++)
	     secondPerp[i] /= length;

	   // Having now our vectors, here we go:
	   // First points; you can have a cone if you change the radius R1

	   final int ANZ = 32;  // number of vertices
	   final float FULL = (float) (2.0f*Math.PI),
	               R1   = 8.5f; // radius
	   float[][] points = new float[ANZ+1][3];
	   for (int i=0; i<ANZ; i++) {
	     float angle = FULL*(i/(float) ANZ);

	     points[i][X] = (float) (R1*(Math.cos(angle)*firstPerp[X]+Math.sin(angle)*secondPerp[X]));
	     points[i][Y] = (float) (R1*(Math.cos(angle)*firstPerp[Y]+Math.sin(angle)*secondPerp[Y]));
	     points[i][Z] = (float) (R1*(Math.cos(angle)*firstPerp[Z]+Math.sin(angle)*secondPerp[Z]));
	   }
	   // Set last to first
	   System.arraycopy(points[0],0,points[ANZ],0,3);

	   //gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
	   gl.glColor3f(1.0f,1.0f,1.0f);
	   
	   gl.glEnable(GL_TEXTURE_2D);
	   gl.glBindTexture(GL_TEXTURE_2D, textureid);
	   gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	   gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	   
	   float[] rgba = {0.3f, 0.5f, 1f};
       gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
       gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
       gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);
	   
	   gl.glBegin(GL_QUAD_STRIP);
	   for (int i=0; i<=ANZ; i++) {
		   
		 gl.glTexCoord2d((double)i / ANZ, text_offset);
	     gl.glVertex3f(vertex_map.get("x "+i) == null ? x1+points[i][X] : vertex_map.get("x "+i),
	    		 		vertex_map.get("y "+i) == null ? y1+points[i][Y] : vertex_map.get("y "+i),
	    				 vertex_map.get("z "+i) == null ? z1+points[i][Z] : vertex_map.get("z "+i));
	     
	     gl.glTexCoord2d((double)i / ANZ, text_offset + 0.03);
	     gl.glVertex3f(x2+points[i][X],
	                   y2+points[i][Y],
	                   z2+points[i][Z]);
	     
	     vertex_map.put("x "+i, (x2+points[i][X]));
	     vertex_map.put("y "+i, (y2+points[i][Y]));
	     vertex_map.put("z "+i, (z2+points[i][Z]));
	   }
	   gl.glEnd();    
   }
	 
   // This draws a triangle on the screen
   private void DrawTriangle(GL2 gl, float size, float x, float y, float z) {
	   
		   gl.glBegin(GL_TRIANGLE_FAN);
		    gl.glColor3f(1.0f,0.0f,0.0f); gl.glVertex3f( 0.0f + x, size + y, 0.0f + z);   //V0(red)
		    gl.glColor3f(0.0f,1.0f,0.0f); gl.glVertex3f(-size + x,-size + y, size + z);   //V1(green)
		    gl.glColor3f(0.0f,0.0f,1.0f); gl.glVertex3f( size + x,-size + y, size + z);   //V2(blue)
		    gl.glColor3f(0.0f,1.0f,0.0f); gl.glVertex3f( size + x,-size + y,-size + z);   //V3(green)
		    gl.glColor3f(0.0f,0.0f,1.0f); gl.glVertex3f(-size + x,-size + y,-size + z);   //V4(blue)
		    gl.glColor3f(0.0f,1.0f,0.0f); gl.glVertex3f(-size + x,-size + y, size + z);   //V1(green)
		gl.glEnd();
   }
   
   private void DrawSphere(GL2 gl, float size, float x, float y, float z) {
	   
	   gl.glTranslated(x, y, z);
	   
	   float SHINE_ALL_DIRECTIONS = 1;
       float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
       float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
       float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

       // Set light parameters.
       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

       // Enable lighting in GL2.
       gl.glEnable(GL2.GL_LIGHT1);
       gl.glEnable(GL2.GL_LIGHTING);

       // Set material properties.
       float[] rgba = {1f, 1f, 1f};
       gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
       gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
       gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

       gl.glBindTexture(GL_TEXTURE_2D, asteroidid);
       
       // Draw sphere (possible styles: FILL, LINE, POINT).
       GLUquadric earth = glu.gluNewQuadric();
       
       glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
       glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
       glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
       final float radius = size;
       final int slices = 8;
       final int stacks = 8;
       glu.gluSphere(earth, radius, slices, stacks);
       glu.gluDeleteQuadric(earth);
       
       gl.glTranslated(-x, -y, -z);
   }
   
   
   public void loadIdentity(GL2 gl){

	      float aspect = (float)CANVAS_WIDTH / CANVAS_HEIGHT;
	 
	      // Set the view port (display area) to cover the entire window
	      gl.glViewport(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
	 
	      // Setup perspective projection, with aspect ratio matches viewport
	      gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
	      gl.glLoadIdentity();             // reset projection matrix
	      glu.gluPerspective(45.0, aspect, 0.1, 10000000.0); // fovy, aspect, zNear, zFar
	 
	      // Enable the model-view transform
	      gl.glMatrixMode(GL_MODELVIEW);
	      gl.glLoadIdentity(); // reset
	      
	      float SHINE_ALL_DIRECTIONS = 1;
	       float[] lightPos = {-30, 0, 0, SHINE_ALL_DIRECTIONS};
	       float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
	       float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

	       // Set light parameters.
	       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
	       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
	       gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

	       // Enable lighting in GL2.
	       gl.glEnable(GL2.GL_LIGHT1);
	       gl.glEnable(GL2.GL_LIGHTING);
	}
   
   
   private void DrawCountDown(GL2 gl) {


	   count_down = (int) (10.0f - Math.max(0, Exchange.ElapsedTime() - 4f));
	   if(count_down < 1) {
		   return;
	   }
	   
	   gl.glMatrixMode(GL2.GL_PROJECTION);
	   gl.glLoadIdentity();
	   gl.glOrtho(0, CANVAS_WIDTH, 0, CANVAS_HEIGHT, 0, 1000);

	   gl.glMatrixMode(GL2.GL_MODELVIEW);
	   gl.glLoadIdentity();
       

       gl.glDisable(GL2.GL_LIGHT1);
       gl.glDisable(GL2.GL_LIGHTING);

       textRenderer.begin3DRendering();

	    textRenderer.setColor(Color.WHITE);

	    textRenderer.draw3D(String.valueOf(count_down), (CANVAS_WIDTH >> 1) - 100 + ((count_down < 10) ? 60 : 0), CANVAS_HEIGHT * 0.4f, -100.0f, 2.0f);
	    // gl.glRotatef(-45, 0.0f, 0.0f, 1.0f);
	    textRenderer.end3DRendering();
		
	    loadIdentity(gl);
   }
   
   private boolean DrawGameOver(GL2 gl) throws IOException {
	   
	   if(Exchange.ElapsedTime() < 90) {
		  return false;
	   }
	   
	   Exchange.updateScore();
	   
	   gl.glMatrixMode(GL2.GL_PROJECTION);
	   gl.glLoadIdentity();
	   gl.glOrtho(0, CANVAS_WIDTH, 0, CANVAS_HEIGHT, 0, 1000);

	   gl.glMatrixMode(GL2.GL_MODELVIEW);
	   gl.glLoadIdentity();
       

       gl.glDisable(GL2.GL_LIGHT1);
       gl.glDisable(GL2.GL_LIGHTING);

       textRenderer.begin3DRendering();

	    textRenderer.setColor(Color.WHITE);
	
	    textRenderer.draw3D("Game Over", (CANVAS_WIDTH >> 1) - 550, CANVAS_HEIGHT * 0.4f, -100.0f, 2.0f);
	    // gl.glRotatef(-45, 0.0f, 0.0f, 1.0f);
	    textRenderer.end3DRendering();
		
	    loadIdentity(gl);
	    
	    return true;
   }
   
   private double DistPlane(double lookAt[], Point3D p) {
	   
	   double vect[] = new double[3];
	   for(int i=0; i<3; i++) {
		   vect[i] = p.val[i] - prev_loc[i];
	   }
	   
	   double dot = 0;
	   for(int i=0; i<3; i++) {
		   dot += vect[i] * lookAt[i];
	   }
	   
	   return dot;
   }


   /**
    * Called back by the animator to perform rendering.
    */
   @Override
   public void display(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

      ArrayList<Point3D> control_buff = new ArrayList<Point3D>();
      for(Point3D p1 : control_queue) {
    	  control_buff.add(p1);
      }
      
      Exchange.UpdateTime();
      
      DrawCountDown(gl); 
      boolean is_game_over = false;
		try {
			is_game_over = DrawGameOver(gl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      
      int[] aiViewport = new int [4]; 
      gl.glGetIntegerv( GL.GL_VIEWPORT, aiViewport, 0 ); 

      double [] adModelView = new double [16]; 
      gl.glGetDoublev( GL_MODELVIEW_MATRIX, adModelView, 0 ); 

      double [] adProjection = new double [16]; 
      gl.glGetDoublev( GL_PROJECTION_MATRIX, adProjection, 0 ); 

      double [] adObjectPos = new double [3]; 
      
      // note viewport[3] is height of window in pixels
      double realy = CANVAS_HEIGHT - (int) mouse_pos[1] - 1;


      int first_point = -1;
      vertex_map.clear();
      HashMap<Double, Point3D> point_map = new HashMap<Double, Point3D>();
      ArrayList<Point3D> point_buff = new ArrayList<Point3D>();
      for(Point3D p : point_queue) {
    	  if(p.t <= t_offset && first_point < 0) {
    		  first_point = point_buff.size();
    	  }
    	  
    	  point_buff.add(p);
    	  point_map.put(p.t, p);
      }

      double vect[] = new double[3];
      for(int i=0; i<3; i++) {
    	  vect[i] = point_buff.get(first_point + 1).val[i] - point_buff.get(first_point).val[i];
      }

	  double yang = Math.atan(vect[0] / vect[2]);
	  yang *= 180 / Math.PI;
	  
	  double xang = Math.atan(vect[1] / vect[2]);
	  xang *= 180 / Math.PI;
	  
	  xangle += (xang - xangle) * 0.8;
	  yangle += (yang - yangle) * 0.8;
	  
	  for(int i=0; i<3; i++) {
		  prev_loc[i] += (point_buff.get(first_point).val[i] - prev_loc[i]) * 0.8;
	  }
	  
	  double small_dist = Double.MAX_VALUE;
	  Point3D min_point =  null;
	  if(mouse_pos[0] > 0) {
		  
		  int count = 0;
		  for(Point3D p : target_queue) {
			  if(p.t > t_offset) {
				  continue;
			  }
			  
			  if(++count >= 5) {
				  break;
			  }
			  
	    	  GLU glu = new GLU(); 
	          glu.gluUnProject( mouse_pos[0], realy, 0.4, 
	                                          adModelView, 0, 
	                                          adProjection, 0, 
	                                          aiViewport, 0, 
	                                          adObjectPos, 0 ) ;
	          
	          FloatBuffer model = FloatBuffer.allocate(16);
	          gl.glGetFloatv(GL_MODELVIEW_MATRIX, model);

	          FloatBuffer proj = FloatBuffer.allocate(16);
	          gl.glGetFloatv(GL_PROJECTION_MATRIX, proj);

	          IntBuffer view = IntBuffer.allocate(4);
	          gl.glGetIntegerv(GL.GL_VIEWPORT, view);

	          FloatBuffer winPos1 = FloatBuffer.allocate(3);
	          glu.gluProject((float)p.val[0], (float)p.val[1], (float)p.val[2], model, proj, view, winPos1);
	          
	          FloatBuffer winPos2 = FloatBuffer.allocate(3);
	          glu.gluProject((float)p.val[0] + 10, (float)p.val[1], (float)p.val[2], model, proj, view, winPos2);
	          
	          double diffx = winPos1.get(0) - mouse_pos[0];
	          double diffy = winPos1.get(1) - (CANVAS_HEIGHT - mouse_pos[1]);
	          double dist = Math.sqrt((diffx * diffx) + (diffy * diffy));
	          double z_dist = DistPlane(vect, p);
	          if(dist < small_dist && dist < Math.min(60, Math.max(10, 3000 / z_dist))) {
	        	  small_dist = dist;
	        	  min_point = p;
	          }
	      }
	  }
	  
	  if(min_point != null && is_game_over == false) {
		  if(min_point.is_display ==  true) {
			  Exchange.score++;
			  Exchange.SetScore();
		  }
		  min_point.is_display = false;
		  
		  for(int i=0; i<50; i++) {
    		  Point3D p2 = new Point3D();
    		  for(int j=0; j<3; j++) {
    			  p2.val[j] = min_point.val[j];
    		  }
    		  particle_queue.add(p2);
    	  }
	  }
	  
	  gl.glPointSize(5f);
	  gl.glColor3f(1.0f,1.0f,1.0f);
	  gl.glBegin(GL_POINTS);
	  for(Point3D p : particle_queue) {
		  for(int j=0; j<3; j++) {
			  p.val[j] += 1 * (Math.random() - 0.5);
			  gl.glVertex3d(p.val[0], p.val[1], p.val[2]);
		  }
	  }
	  
	  gl.glEnd();
	  
	  if(particle_queue.size() > 0) {
		  for(int i=0; i<3; i++) {
			  if(particle_queue.size() > 0) {
				  particle_queue.remove();
			  }
		  }
	  }
	  
	  int radius = 399;
	  
	  if(is_left == true) {
			loc_offset[0] += 5;
	  } 
	  
	  if(is_right == true) { 
		    loc_offset[0] -= 5;
	  } 

	  if(is_up == true) {
		  if(Math.abs(loc_offset[1]) < radius) {
			  loc_offset[1] -= 0.1;
		  }
	  } 
	  
	  if(is_down == true) { 
		  if(Math.abs(loc_offset[1]) < radius) {
			  loc_offset[1] += 0.1;
		  }
	  } 
	  
	  if(is_up == false && is_down == false) { 
		  loc_offset[1] *= 0.8f;
	  }
	  
      // ----- Render the Pyramid -----
      gl.glLoadIdentity();                 // reset the model-view matrix
      
      gl.glRotated(loc_offset[0], 0, 0, 1.0f); // rotate about the y-axis
	  gl.glRotated(xangle, 1, 0, 0.0f); // rotate about the y-axis
      gl.glRotated(-yangle, 0, 1, 0.0f); // rotate about the y-axis
     
     
      gl.glTranslated(-prev_loc[0], -prev_loc[1], -prev_loc[2]); // translate left and into the screen


      float text_offset = 0;
      for(int i=0; i<Math.min(900, point_buff.size()-10); i+=10) {
    	  Point3D p1 = point_buff.get(i);
    	  Point3D p2 = point_buff.get(i+10);

			try {
				drawCylinder(gl, (float)p1.val[0], (float)p1.val[1], (float)p1.val[2], 
						  (float)p2.val[0], (float)p2.val[1], (float)p2.val[2], text_offset);
			} catch (GLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			text_offset += 0.03;
			text_offset %= 1.0f;
      }
      
      for(Point3D p : target_queue) {
    	  if(p.is_display == true) {
    		  DrawSphere(gl, 1, (float)p.val[0], (float)p.val[1], (float)p.val[2]);
    	  }
      }
      
      if(count_down < 1) {
    	  t_offset-=0.15;
      }
      
      for(int i=0; i<control_buff.size()-1; i++) {
    	  
    	  if(control_buff.get(i+1).t  < t_offset) {
    		  break;
    	  }
    	  
    	  Point3D temp = new Point3D(control_buff.get(control_buff.size()-1));
    	  control_queue.add(temp);
    	  
    	  Spline sp[] = new Spline[3];
          ArrayList<Point3D> temp_buff = new ArrayList<Point3D>();
          for(Point3D p1 : control_queue) {
        	  temp_buff.add(p1);
          }

          control_queue.remove();
          
          for(int k=0; k<3; k++) {
    	      int offset = 0;
    	      double xval[] = new double[temp_buff.size()];
    	      double yval[] = new double[temp_buff.size()];
    	      for(int j=temp_buff.size()-1; j>=0; j--) {
    	    	  xval[offset] = temp_buff.get(j).t;
    	    	  yval[offset++] = temp_buff.get(j).val[k];
    	      }
    	      
    	      sp[k] = new Spline(xval, yval);
          }

          point_queue.clear();
    	  for(int k=0; k<temp_buff.size()-1; k++) {
    		  Point3D p1 = temp_buff.get(k);
    		  Point3D p2 = temp_buff.get(k+1);
        	  
        	  double inc = 0.1;
        	  for(double t=p1.t; t>p2.t; t-=inc) {
        		  
        		  Point3D prev = point_map.get(t);
        		  Point3D p3 = new Point3D();
        		  p3.t = t;
    			  for(int j=0; j<3; j++) {
    				  
    				  if(prev != null && Math.abs(t - t_offset) < 1000) {
    					  p3.val[j] = prev.val[j];
    				  } else {
    					  p3.val[j] = sp[j].getValue(t); 
    				  }
    			  }
    			  
    			  point_queue.add(p3);
        	  }
          }
    	  
    	  ArrayList<Point3D> target_buff = new ArrayList<Point3D>();
          for(Point3D p : target_queue) {
        	  target_buff.add(p);
          }
    	  
    	  for(int k=0; k<target_buff.size()-1; k++) {
        	  if(target_buff.get(k).t < t_offset) {
        		  break;
        	  }
        	  
        	  target_queue.remove();
          }
    	  
    	  int size = target_queue.size();
    	  while(size < 50) {
    		  
    		  target_buff = new ArrayList<Point3D>();
              for(Point3D p : target_queue) {
            	  target_buff.add(p);
              }
    		  
    		  Point3D p3 = target_buff.get(target_buff.size()-1);
        	  
        	  Point3D p4 = new Point3D();
        	  p4.t = p3.t - (Math.random() * 5);
        	  for(int j=0; j<3; j++) {
        		  p4.val[j] = (Math.random() - 0.5) * 7;
        		  p4.val[j] += sp[j].getValue(p4.t); 
        	  }
        	  
        	  
        	  
        	  target_queue.add(p4);
	    	  
	    	  size = target_queue.size();
    	  }
      }

   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }

   @Override
   public void keyTyped(KeyEvent e) {}
 
   @Override
   public void keyPressed(KeyEvent e) {
	   
      int keyCode = e.getKeyCode();
      switch (keyCode) {
         case KeyEvent.VK_LEFT: // quit
        	 is_left = true;
        	 is_right = false;
            break;
         case KeyEvent.VK_RIGHT: // quit
        	 is_right = true;
        	 is_left = false;
            break;
         case KeyEvent.VK_UP: // quit
        	 is_up = true;
        	 is_down = false;
            break;
         case KeyEvent.VK_DOWN: // quit
        	 is_down = true;
        	 is_up = false;
            break;
      }
   }
 
   @Override
   public void keyReleased(KeyEvent e) {
	   
	   int keyCode = e.getKeyCode();
      switch (keyCode) {
         case KeyEvent.VK_LEFT: // quit
        	 is_left = false;
            break;
         case KeyEvent.VK_RIGHT: // quit
        	 is_right = false;
            break;
         case KeyEvent.VK_UP: // quit
        	 is_up = false;
            break;
         case KeyEvent.VK_DOWN: // quit
        	 is_down = false;
            break;
      }
      
      is_left = false;
  	  is_right = false;
  	  is_up = false;
  	  is_down = false;
	      
   }

@Override
public void mouseClicked(MouseEvent arg0) {

}

@Override
public void mouseEntered(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseExited(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void mousePressed(MouseEvent arg0) {
	// TODO Auto-generated method stub
	
	int x = (int) (arg0.getX() * (float)(800.0f / 785.0f));
	int y = (int) (arg0.getY() * (float)(800.0f / 760.0f));
	mouse_pos[0] = x;
	mouse_pos[1] = y;
}

@Override
public void mouseReleased(MouseEvent arg0) {
	mouse_pos[0] = -1;
	
}

@Override
public void stop() {
	player.stop();
	
}

public static void main( String [] args ) throws Exception {
	
	
	new Exchange(new JOGL2Ex2Rotate3D_GLCanvas(), 0, 90, 4, 
			"Destroy as many asteriods as possible in the alloted time by clicking on them.", "Vortex");

}
}