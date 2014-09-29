package Applet;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A minimal program that draws with JOGL in an AWT Frame.
 *
 * @author Wade Walker
 */
public class OneTriangleAWT {

    public static void main( String [] args ) {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        EscapeTheMaze hm = new EscapeTheMaze(glcanvas);
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

        frame.setSize( 800, 800 );
        frame.setVisible( true );
    }
}