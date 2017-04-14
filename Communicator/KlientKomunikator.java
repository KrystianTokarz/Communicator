import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class KlientKomunikator extends JFrame implements ActionListener,KeyListener
{
		private String nickNasz;
		private String nickKolegi;
		private Socket serwer;
		private BufferedReader in; 
		private PrintWriter out;
		private JButton buttonWyslij,buttonWyjdz;
		private JEditorPane poleTekstowe;
		private JTextArea poleWyslij;
		private JScrollPane suwak;

	public KlientKomunikator(Socket serwer,String nickNasz,String nickKolegi,BufferedReader in,PrintWriter out)
	{
		super("Komunikator");
		this.serwer=serwer;	
		this.nickKolegi=nickKolegi;
		this.nickNasz=nickNasz;
		this.in=in;
		this.out=out;
		
		 
	        setSize(600,500);
	        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	        setLocation(d.width/3,d.height/4);
	        setLayout(null);
	        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	        setResizable(false);
	        
//	        getContentPane().setBackground(new Color(0,102,204));
	        
	        buttonWyjdz = new JButton("Exit");
			buttonWyjdz.setBackground(Color.LIGHT_GRAY);
			buttonWyjdz.setBounds(520,10,60,35);
			buttonWyjdz.setToolTipText(" Kliknij aby wyjœæ z programu ");
			buttonWyjdz.addActionListener(this);
			add(buttonWyjdz);
	        
	        buttonWyslij = new JButton("Wyslij");
	        buttonWyslij.setBounds(410,360,100,60);
	        buttonWyslij.setToolTipText(" Kliknij aby stworzyæ nowe konto ");
	        buttonWyslij.addActionListener(this);
			add(buttonWyslij);
		
			poleTekstowe = new JEditorPane("text/html","<center><b> WITAMY </b></center> <hr>");
			poleTekstowe.setEditable(false);
			suwak = new JScrollPane(poleTekstowe);
			suwak.setBounds(50,50, 460, 300);
			add(suwak);
			
			poleWyslij = new JTextArea();
			poleWyslij.setBounds(50,360, 350, 60);	
			poleWyslij.setWrapStyleWord(true);
			poleWyslij.setLineWrap(true);
			poleWyslij.addKeyListener(this);
			add(poleWyslij);
	        
			LoginBackgroundPanel lg = new LoginBackgroundPanel("tloKomunikatora.jpg");
			add(lg);
			
	        setVisible(true);
	        
	        new WatekKomunikacjiOdbioru(serwer,nickNasz,in).start();
	}
	public void wyslijWiadomosc(String tmp)
	{
		if(tmp.equals(":)"))
			tmp=" <a href=\"http://emotikona.pl/emotikony/\"><img src=\"http://emotikona.pl/emotikony/pic/0acute.gif\" border=0></a>";
		poleTekstowe.setText(poleTekstowe.getText().substring(0,poleTekstowe.getText().lastIndexOf("</body>")) + "<br/> <font color=\"blue\"> " + nickNasz + " : " + tmp +"</font>");
		suwak.getVerticalScrollBar().setValue(suwak.getVerticalScrollBar().getMaximum());
		out.println(tmp);
		poleWyslij.setText("");
		out.flush();
	}
	
	public void zakoncz()
	{
			out.println("exit");
			dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();

		if(obj==buttonWyslij )
		{
			String tmp=  poleWyslij.getText();
			wyslijWiadomosc(tmp);
		}
		if(obj == buttonWyjdz)
		{
			zakoncz();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		//nie uzywamy
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		
		int klawisz = e.getKeyCode();
		if(klawisz == 10)
		{
			String tmp = poleWyslij.getText();
			if (poleWyslij.getText().length()==1)
			{
			 	tmp="";
			}
			else
			{
				tmp=tmp.substring(0, tmp.length()-1);
			}
			wyslijWiadomosc(tmp);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		//nie uzywamy
	}
	
	class WatekKomunikacjiOdbioru extends Thread
	{
		private String nick;
		private Socket serwer;
		private BufferedReader in; 
		
		public WatekKomunikacjiOdbioru(Socket serwer,String nick,BufferedReader in)
		{
			this.nick=nick;
			this.in=in;
			this.serwer=serwer;	
		}
		
		@Override
		public void run()
		{
			String tmp;
   		    try
			{
				while((tmp=in.readLine())!=null)
				  {
					if(tmp.equals("exit"))
					{
						out.println("exit");
						poleTekstowe.setText(poleTekstowe.getText().substring(0,poleTekstowe.getText().lastIndexOf("</body>")) + "<br/> <font color=\"red\"> " + nickKolegi + " : " + "ROZ£¥CZONO ROZMOWE" +"</font>");
						buttonWyslij.setEnabled(false);
						poleWyslij.setEnabled(false);
						break;
					} else
					{
						if(tmp.equals(":)"))
								tmp=" <a href=\"http://emotikona.pl/emotikony/\"><img src=\"http://emotikona.pl/emotikony/pic/0acute.gif\" border=0></a>";
						poleTekstowe.setText(poleTekstowe.getText().substring(0,poleTekstowe.getText().lastIndexOf("</body>")) + "<br/> <font color=\"green\"> " + nickKolegi + " : " + tmp +"</font>");
						int len = poleTekstowe.getDocument().getLength();
						poleTekstowe.setCaretPosition(len);
					}
				  }
			} catch (IOException e)
			{
				JOptionPane.showMessageDialog(null,"Serwer SHUTDOWN  ! ","ERROR",JOptionPane.WARNING_MESSAGE);
				buttonWyslij.setEnabled(false);
				poleWyslij.setEnabled(false);
				e.printStackTrace();
			}
		}	
	}
}
