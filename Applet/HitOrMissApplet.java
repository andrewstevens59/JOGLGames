package Applet;

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
public class HitOrMissApplet extends Applet implements MouseListener, MouseMotionListener   {

    public static int WIDTH = 800;
    
    private HitOrMiss tr = null;

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
        tr = new HitOrMiss(glcanvas); 
    	glcanvas.addGLEventListener(tr);
    	glcanvas.addMouseListener(tr);
    	glcanvas.addMouseMotionListener(tr);
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mouseMoved(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		tr.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		tr.mouseReleased(e);
		
	}
}