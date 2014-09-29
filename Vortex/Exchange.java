package Vortex;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.jogamp.opengl.util.FPSAnimator;

public class Exchange {
	
	public static JLabel text = new JLabel();
	public static long start_time = 0;
	public static int score = 0;
	public static int max_score = 0;
	
	private Game game;
	private String game_title = new String();
	private String instructions = new String();
	private static float max_time = 0;
	private static String api_key = new String();
	private float balance = 0;
	private static int game_id = 0;
	private static String key_code = null;
	private static boolean game_over = false;
	final private JFrame frame = new JFrame ();
	
	private int version = 0;
	private int min_version = 0;
	private int curr_version = 0;
	private static boolean is_no_cash = false;
	
	static String formatLines(
		    String target, int maxLength,
		    Locale currentLocale) {

		String text = new String();
	    BreakIterator boundary = BreakIterator.
	        getLineInstance(currentLocale);
	    boundary.setText(target);
	    int start = boundary.first();
	    int end = boundary.next();
	    int lineLength = 0;

	    while (end != BreakIterator.DONE) {
	        String word = target.substring(start,end);
	        lineLength = lineLength + word.length();
	        if (lineLength >= maxLength) {
	            text += "\n";
	            lineLength = word.length();
	        }

	        text += word;
	        start = end;
	        end = boundary.next();
	    }
	    
	    return text;
	}
	
	// This finds the api key
	private boolean findAPIKey() throws IOException {
		
		long curr_date = 0;
		boolean is_found = false;
		final File folder = new File("./");
		 for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory() == false) {
	            if(fileEntry.getName().contains("game_api_user")) {
	            	
	            	long date = Long.parseLong(fileEntry.getName().substring(13, fileEntry.getName().length()-4));
	            	if(date < curr_date) {
	            		continue;
	            	}
	            	
	            	is_found = true;
	            	BufferedReader br = new BufferedReader(new FileReader(fileEntry.getName()));
	            	String line;
	            	while ((line = br.readLine()) != null) {
	            	   api_key = line;
	            	}
	            	br.close();
	            }
	        } 
	    }
	 
		return is_found;
	}
	
	 
	// This verifies the account credentials
	private int veryifyRegistration() throws IOException {
		
	    URL                 url;
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input;
	    
	    // URL connection channel.
	    try {
	    	// URL of CGI-Bin script.
		    url = new URL ("http://localhost/GameExchange/check_registration.php");
			urlConn = url.openConnection();
		} catch (IOException e) {
			return -6;
		}
	    // Let the run-time system (RTS) know that we want input.
	    urlConn.setDoInput (true);
	    // Let the RTS know that we want to do output.
	    urlConn.setDoOutput (true);
	    // No caching, we want the real thing.
	    urlConn.setUseCaches (false);
	    // Specify the content type.
	    urlConn.setRequestProperty
	    ("Content-Type", "application/x-www-form-urlencoded");
	    // Send POST output.
	    
	    try {
		    printout = new DataOutputStream (urlConn.getOutputStream ());
		    String content =
		    "api=" + URLEncoder.encode (api_key) +
		    "&game=" + URLEncoder.encode (String.valueOf(game_id));
		    printout.writeBytes (content);
		    printout.flush ();
		    printout.close ();
	    }  catch (IOException e) {
			return -6;
		}
	    
	    // Get response data.
	    try {
	    	input = new DataInputStream (urlConn.getInputStream ());
	    }  catch (IOException e) {
			return -6;
		}
	    String str;
	    
	    int validate_code = -1;
	    while (null != ((str = input.readLine())))
	    {

	    	if(str.length() > 3 && str.substring(0, 3).equals("yes")) {
	    		String macAddress = macAddress();
	    		if(macAddress.equals(str.substring(4, str.length())) == false) {
	    			validate_code = -2;
	    		} else {
	    			validate_code = 0;
	    		}
	    	}
	    	
	    	if(str.equals("expired")) {
	    		validate_code = -3;
	    	}
	    	
	    	if(str.equals("blocked")) {
	    		validate_code = -5;
	    	}
	    	
	    	if(str.equals("removed")) {
	    		validate_code = -4;
	    	}
	    	
	    	if(str.length() > 10 && str.substring(0, 10).equals("Max Score:")) {
	    		max_score = Integer.parseInt(str.substring(11, str.length()));
	    	}
	    	
	    	if(str.length() > 8 && str.substring(0, 8).equals("Version:")) {
	    		String split[] = str.substring(9, str.length()).split(" ");
	    		min_version = Integer.parseInt(split[0]);
	    		curr_version = Integer.parseInt(split[1]);
	    	}
	    }
	    input.close ();
	    
	    return validate_code;
		
	}
	
	public static void updateScore() throws IOException {
		
		if(game_over == true || is_no_cash == true) {
			return;
		}
		
		game_over = true;
		
		URL                 url;
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input;
	    // URL of CGI-Bin script.
	    url = new URL ("http://localhost/GameExchange/update_score.php");
	    // URL connection channel.
	    urlConn = url.openConnection();
	    // Let the run-time system (RTS) know that we want input.
	    urlConn.setDoInput (true);
	    // Let the RTS know that we want to do output.
	    urlConn.setDoOutput (true);
	    // No caching, we want the real thing.
	    urlConn.setUseCaches (false);
	    // Specify the content type.
	    urlConn.setRequestProperty
	    ("Content-Type", "application/x-www-form-urlencoded");
	    // Send POST output.
	    printout = new DataOutputStream (urlConn.getOutputStream ());
	    String content =
	    "api=" + URLEncoder.encode (api_key) +
	    "&score=" + URLEncoder.encode (String.valueOf(score)) +
	    "&game=" + URLEncoder.encode (String.valueOf(game_id));
	    printout.writeBytes (content);
	    printout.flush ();
	    printout.close ();
	    // Get response data.
	    input = new DataInputStream (urlConn.getInputStream ());
	    String str;
	    
	    while (null != ((str = input.readLine())))
	    {
	    	System.out.println(str);
	    }
	    input.close ();
	}
	
	// This finds the mac address for the computer
	private String macAddress() {
		
		String address = new String();
		InetAddress ip;
		try {
	 
			ip = InetAddress.getLocalHost();
	 
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	 
			byte[] mac = network.getHardwareAddress();
	 
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}

			address += sb.toString() + "-" + ip.getHostAddress();
	 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
		
		return address;
	}
	
	private void mainScreen() throws Exception {
		
		frame.setTitle (game_title);
    	frame.setResizable (true);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationByPlatform(true);
    	

    	boolean is_verified = true;
    	boolean val = findAPIKey();
    	Image im = Toolkit.getDefaultToolkit().getImage("icon.png");
    	frame.setIconImage(im);
    	
    	JPanel panel2 = new JPanel ();
    	JPanel panel1 = new JPanel ();
    	panel1.add(text);
    	panel1.setBackground(Color.white);
    	panel2.setBackground(Color.white);
    	frame.getContentPane().setLayout(new BorderLayout());
    	
    	JLabel text = new JLabel("Gwindle");
    	text.setFont(new Font("", Font.PLAIN, 50));
    	panel1.add(text);
    	
    	JPanel middlePanel = new JPanel ();
        middlePanel.setBorder ( new TitledBorder ( new EtchedBorder ()) );
        middlePanel.setBackground(Color.white);

        // create the middle panel components

        final JTextArea errorCode = new JTextArea( 5, 30 );
        JTextArea display = new JTextArea ( 10, 58 );
        display.setEditable ( false ); // set textArea non-editable
        JScrollPane scroll = new JScrollPane ( display );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        //Add Textarea in to middle panel
        middlePanel.add ( scroll );
        
        String text1 = "Gwindle offers monthly cash prizes to players that obtain the high score on a particular game. Inolves no betting, games are of skill only. Play a fixed cost to download the game and play as many times as you like in a given period of time. Improve your skill by playing frequently where skilled players win more frequently.";
        text1 = formatLines(text1, 110, new Locale ("en","US"));
        text1 += "\n\nInstructions\n";
        text1 += formatLines(instructions, 110, new Locale ("en","US"));
        display.setFont(new Font("", Font.PLAIN, 15));
        display.setText(text1);
        
        String text2 = null;
        if(val == false) {
        	text2 = "To play the game you must first save your API key file in the same directory as this game. Your API key file helps us to identify you. You can find your API key file by first creating an account and saving the API key file in the same directory. The API key file can be downloaded from your account page.";
        	text2 = formatLines(text2, 110, new Locale ("en","US"));
        	errorCode.setText(text2);
			errorCode.setFont(new Font("", Font.PLAIN, 14));
			errorCode.setForeground(Color.red);
			errorCode.setVisible(true);
			middlePanel.add(errorCode);
			is_verified = false;
        } else {

	        int validate_code = veryifyRegistration();
	        switch(validate_code) {
		        case -1: text2 =  "You are currently not registered to play this game. To help fund the development of more games we ask you pay a small reistration fee. Please click register as the final step to playing this game.";;
		        break;
		        case -2: text2 =  "This game is not currently registered for this computer, you have the option of purchasing a new registration for this computer or reverting to the machine on which this game was first registered.";
		        break;
		        case -3: text2 =  "Your registration for this game has expired please complete a new registration.";
		        break;
		        case -4: text2 =  "This game is not currently offered, meaning no cash prizes are available. Check the website for details.";
		        break;
		        case -5: text2 =  "A timeout period is placed on players who have recently achieved a high score for this game. This means that players who have recently won the cash prize cannot win again for a period of time, see website for details.";
		        break;
		        case -6: text2 =  "Could connect to server or the internet please check your internet connection and try again later.";
		        break;
	        }
	       
	        if(validate_code != 0) {
	        	text2 = formatLines(text2, 120, new Locale ("en","US"));
	        	errorCode.setText(text2);
				errorCode.setFont(new Font("", Font.PLAIN, 14));
				errorCode.setForeground(Color.red);
				errorCode.setVisible(true);
				
				if(validate_code >= -3) {
					JButton button2 = new JButton("Register");
			    	button2.addActionListener(new ActionListener() {
			    		  public void actionPerformed(ActionEvent e)
			    		  {
			    			  
			    			  try {
		    			         //Set your page url in this string. For eg, I m using URL for Google Search engine
		    			         String url = "http://localhost/GameExchange/register_game.php?";
		    			         String param = api_key;
		    			         param += " " + String.valueOf(game_id);
		    			         
		    			         String macAddress = macAddress();
		    			         MCrypt crypt = new MCrypt();
		    			         param += " " + String.valueOf(macAddress);
		    			         
		    			         System.out.println(param);
		    			         url += "param=" + MCrypt.bytesToHex(crypt.encrypt(param));
		    			         
		    			         java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		    			       }
		    			       catch (java.io.IOException e1) {
		    			           System.out.println(e1.getMessage());
		    			       } catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			    		  }
			    		});
			    	
			    	button2.setFont(new Font("", Font.PLAIN, 20));
			    	middlePanel.add(button2,BorderLayout.CENTER);
			    	is_verified = false;
				}
				
				if(validate_code == -4 || validate_code == -5) {
					is_no_cash = true;
				}
		    	
				middlePanel.add(errorCode,BorderLayout.CENTER);
	        }
	
	        if(version < min_version) {
	        	text2 = "The current version of this game is out of date, please download the latest version from the website.";
	        	text2 = formatLines(text2, 110, new Locale ("en","US"));
	        	errorCode.setText(text2);
				errorCode.setFont(new Font("", Font.PLAIN, 14));
				errorCode.setForeground(Color.red);
				errorCode.setVisible(true);
				middlePanel.add(errorCode,BorderLayout.CENTER);
				is_verified = false;
	        }
	        
	        if(version < curr_version) {
	        	text2 = "There is a newer version of this game available for download.";
	        	text2 = formatLines(text2, 110, new Locale ("en","US"));
	        	errorCode.setText(text2);
				errorCode.setFont(new Font("", Font.PLAIN, 14));
				errorCode.setForeground(Color.blue);
				errorCode.setVisible(true);
				middlePanel.add(errorCode,BorderLayout.CENTER);
	        }
        }
    	
        if(is_verified == true) {
	    	JButton button2 = new JButton("Play Game");
	    	button2.addActionListener(new ActionListener() {
	    		  public void actionPerformed(ActionEvent e)
	    		  {
	    			  
	    			  startGame();
	    			  score = 0;
	    			  game_over = false;
	    		  }
	    		});
	    	
	    	button2.setFont(new Font("", Font.PLAIN, 20));
	    	button2.setPreferredSize(new Dimension(200, 100));
	
	    	panel2.add(button2);
        }
        
    	frame.getContentPane().add (panel1,BorderLayout.NORTH);
    	frame.getContentPane().add (middlePanel,BorderLayout.CENTER);
    	frame.getContentPane().add (panel2,BorderLayout.SOUTH);
        

        frame.setSize( 800, 800 );
        frame.setVisible( true );
	}
	
    public Exchange(Game game, int version, float max_time, int game_id, String instructions, String game_title) throws Exception {
    	this.game = game;
    	Exchange.max_time = max_time;
    	Exchange.game_id = game_id;
    	this.instructions = instructions;
    	this.version = version;
    	this.game_title = game_title;
    	
    	mainScreen();
    }
    
    private void startGame() {
    	final JFrame frame1 = new JFrame ();
    	frame1.setResizable(false);
    	frame1.setLocationByPlatform(true);
    	
    	Image im = Toolkit.getDefaultToolkit().getImage("icon.png");
    	frame1.setIconImage(im);
    	
    	GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );

        glcanvas.addGLEventListener((GLEventListener) game);
        glcanvas.addMouseListener((MouseListener) game);
    	
    	FPSAnimator animator = new FPSAnimator(glcanvas, 60);
    	animator.start();       // start the animator
    	


    	JPanel panel = new JPanel ();
    	JButton exit = new JButton ("Exit");
    	exit.addActionListener(new ActionListener() {
  		  public void actionPerformed(ActionEvent e)
  		  {
  			frame1.setVisible(false);
  			game.stop();
  			try {
  				if(game_over == true) {
  					mainScreen();
  				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
  			frame.show();
  		  }
  		});
    	
    	panel.setLayout (new BorderLayout ());
    	panel.add (exit, BorderLayout.EAST);
    	
    	panel.add(text, BorderLayout.CENTER);
    	panel.setBackground(Color.white);

    	
    	frame1.getContentPane().setLayout(new BorderLayout());
    	frame1.getContentPane().add (panel,BorderLayout.NORTH);
    	frame1.setVisible (true);
        
        
    	frame1.add( glcanvas );
    	frame1.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
            	frame1.remove( glcanvas );
            	frame1.dispose();
                System.exit( 0 );
            }
        });

    	frame1.setSize( 1200, 800 );
    	frame1.setVisible( true );
    	
    	frame.hide();
    	start_time = System.currentTimeMillis();
    }
    
    private static void UpdateText() {
    	int time_left = Math.max(0, (int) (max_time - ElapsedTime()));
    	
    	if(score > max_score) {
    		text.setForeground(Color.green);
    	}
    	
    	if(is_no_cash == false) {
    		text.setText("Score "+score+"                   Time Remaining "+time_left+" (s)        High Score "+max_score);
    	} else {
    		text.setText("Score "+score+"                   Time Remaining "+time_left+" (s)");
    	}
    }
	
	public static void SetScore() {
		UpdateText();
	}
	
	public static void UpdateTime() {
		UpdateText();
	}
	
	public static float ElapsedTime() {
		return (float)Math.abs(System.currentTimeMillis() - start_time) / 1000;
	}
	
	public static void SetStartTime() {
		start_time = System.currentTimeMillis();
	}
}
