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
import java.text.DecimalFormat;
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
 * NeHe Lesson 14: 3D Text Rendering using TextRenderer
 */
public class TextLayer extends JFrame implements GLEventListener
{
  private static final int CANVAS_WIDTH = 640; // width of drawable
  private static final int CANVAS_HEIGHT = 480; // height of drawable
  private static final int FPS = 60; // animation rate in frames per second
  private GLU glu; // for the GL Utility

  private TextRenderer textRenderer;
  private String msg = "Callum McDonald";
  private DecimalFormat formatter = new DecimalFormat("###0.00");

  private float textPosX; // x-position of the 3D text
  private float textPosY; // y-position of the 3D text
  private float textScaling; // scaling factor for 3D text

  private static float rotateAngle = 0.0f;

  // Constructor
  public TextLayer()
  {
    Container cp = getContentPane();
    GLCanvas canvas = new GLCanvas();
    canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    cp.add(canvas);
    canvas.addGLEventListener(this);

    // Run the animation loop using the fixed-rate Frame-per-second animator
    final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
    addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(WindowEvent e)
      {
        // Use a thread to run the stop() to ensure that the animator stops
        // before program exits.
        new Thread()
        {
          @Override
          public void run()
          {
            animator.stop(); // stop the animator loop
            System.exit(0);
          }
        }.start();
      }
    });
    pack();
    setTitle("Nehe Lesson 14: 3D Texts");
    setVisible(true);
    animator.start(); // start the animator loop
  }

  // Main program
  public static void main(String[] args)
  {
    new TextLayer();
  }

  // ------ Implement methods declared in GLEventListener ------

  /**
   * Called back immediately after the OpenGL context is initialized. Can be
   * used to perform one-time initialization such as setup of lights. Run only
   * once.
   */
  @Override
  public void init(GLAutoDrawable drawable)
  {
	  GL2 gl = drawable.getGL().getGL2(); // Get the OpenGL graphics context
    glu = new GLU(); // GL Utilities
    // Enable smooth shading (blends colors nicely) and smoothes out lighting.
    gl.glShadeModel(GL_SMOOTH);
    // Set background color in RGBA. Alpha: 0 (transparent) 1 (opaque)
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glClearDepth(1.0f); // clear z-buffer to the farthest
    gl.glEnable(GL_DEPTH_TEST); // enables depth testing
    gl.glDepthFunc(GL_LEQUAL); // the type of depth test to do
    // Do the best perspective correction
    gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

    gl.glEnable(GL_LIGHT0); // Enable default light (quick and dirty)
    gl.glEnable(GL_LIGHTING); // Enable lighting
    gl.glEnable(GL_COLOR_MATERIAL); // Enable coloring of material

    textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 24));
  }

  /**
   * Called back by the animator to perform rendering.
   */
  @Override
  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    gl.glLoadIdentity();
    gl.glTranslatef(0.0f, 0.0f, -80.0f);

    textRenderer.begin3DRendering();

    textRenderer.setColor(Color.RED);
    textRenderer.draw3D(msg, -20.0f, 0.0f, -20.0f, 1.0f);
    // gl.glRotatef(-45, 0.0f, 0.0f, 1.0f);
    textRenderer.end3DRendering();

    gl.glRotatef(45, 0.0f, 0.0f, 1.0f);
    textRenderer.begin3DRendering();
    textRenderer.setColor(Color.RED);

    textRenderer.draw3D(msg + formatter.format(rotateAngle / 50), -10.0f, 0.0f, -20.0f, 1.0f);
    // gl.glRotatef(60, 0.0f, 0.0f, -1.0f);
    textRenderer.end3DRendering();

  }

  /**
   * Called back when the canvas is first set to visible, and during the first
   * repaint after the canvas has been resized.
   */
  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
  {
	  GL2 gl = drawable.getGL().getGL2(); // Get the OpenGL graphics context
    height = (height == 0) ? 1 : height; // prevent divide by zero
    float aspect = (float) width / (float) height;
    gl.glViewport(0, 0, width, height); // Reset the current view port

    // Set up the projection matrix - choose perspective view
    gl.glMatrixMode(GL_PROJECTION);
    gl.glLoadIdentity(); // reset
    // Angle of view (fovy) is 45 degrees (in the up y-direction). Based on this
    // canvas's aspect ratio. Clipping z-near is 0.1f and z-near is 100.0f.
    glu.gluPerspective(45.0f, aspect, 0.1f, 100.0f); // fovy, aspect, zNear,
    // zFar

    // Enable the model-view transform
    gl.glMatrixMode(GL_MODELVIEW);
    gl.glLoadIdentity(); // reset
  }

  /**
   * Called when the display mode (eg. resolution) has been changed.
   */
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
  {
  }

@Override
public void dispose(GLAutoDrawable arg0) {
	// TODO Auto-generated method stub
	
}
}


