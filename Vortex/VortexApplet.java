package Vortex;

import java.applet.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.media.opengl.GLAnimatorControl;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * A minimal applet that draws with JOGL in a browser window.
 *
 * @author Wade Walker
 */
@SuppressWarnings("serial")
public class VortexApplet extends Applet implements MouseListener  {

    public static int WIDTH = 800;
    
    private JOGL2Ex2Rotate3D_GLCanvas tr = null;

    public void init() {
    	if(tr != null) {
    		return;
    	}
    	
    	//String inputFromPage = this.getParameter("Message");
    	
    	/*URL oracle;
		try {
			oracle = new URL("http://localhost/GameExchange/login.php/");
		
	        URLConnection yc = oracle.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    yc.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
        GLProfile.initSingleton();
        setLayout( new BorderLayout() );

        final GLCanvas glcanvas = new GLCanvas();
        tr = new JOGL2Ex2Rotate3D_GLCanvas(); 
    	glcanvas.addGLEventListener(tr);
    	glcanvas.addKeyListener(tr);
    	glcanvas.addMouseListener(tr);
    	glcanvas.setSize( WIDTH, WIDTH );
    	
    	FPSAnimator animator = new FPSAnimator(glcanvas, 60);
    	animator.start();       // start the animator
    
        add( glcanvas, BorderLayout.CENTER );
        
        /*FPSAnimator animator = new FPSAnimator(glcanvas, 60);
        animator.add(glcanvas);
        animator.start();*/
    }

    public void start() {
    }
    
    public void stop() {
    }
    
    public void destroy() {
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		tr.mouseClicked(arg0);
		
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
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}