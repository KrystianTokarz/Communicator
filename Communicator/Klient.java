import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;




public class Klient extends JFrame implements ActionListener
{
	private JLabel labelMain,labelMain1, labelMain2;
	private JTextField fieldIP,fieldNick;
	private JButton buttonLoguj,buttonWyjdz;
	private LoginBackgroundPanel backGround;
	private Socket serwer;
	private Dimension dimension;
	
	private static final Pattern PATTERN = Pattern.compile(
	            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public Klient()
    {
        super("Po³¹czenie z u¿ytkownikiem");
        setSize(500,400);
        dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width/3,dimension.height/4);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        labelMain = new JLabel("Komunikator");
    	labelMain.setForeground(Color.BLACK);
    	labelMain.setFont(new Font("Arial",Font.BOLD,25));
    	labelMain.setBounds(150,45,180,30);
    	add(labelMain);
    	
    	fieldNick = new JTextField();
    	fieldNick.setBounds(240,120,120,30);
    	fieldNick.setToolTipText(" Wpisz swój nick ");
		add(fieldNick);
    	
    	fieldIP = new JTextField("127.0.0.1");
    	fieldIP.setBounds(240,175,120,40);
    	fieldIP.setToolTipText(" Wpisz ip gracza z ktorym chcesz sie po³¹czyæ ");
		add(fieldIP);
		
		labelMain1 = new JLabel("Podaj swój nick ");
		labelMain1.setForeground(Color.BLACK);
    	labelMain1.setBounds(120,120,200,20);
    	add(labelMain1);
		
		labelMain1 = new JLabel("Podaj IP komputera z którym");
    	labelMain1.setForeground(Color.BLACK);
    	labelMain1.setBounds(50,170,200,20);
    	add(labelMain1);
    	
    	labelMain2 = new JLabel("chcesz siê po³¹czyæ");
    	labelMain2.setForeground(Color.BLACK);
    	labelMain2.setBounds(50,190,200,20);
    	add(labelMain2);
		
		buttonLoguj = new JButton("Po³¹cz");
		buttonLoguj.setBounds(120,250,110,30);
		buttonLoguj.setToolTipText(" Kliknij aby stworzyæ nowe konto ");
		buttonLoguj.addActionListener(this);
		add(buttonLoguj);
		
		buttonWyjdz = new JButton("Wyjdz");
		buttonWyjdz.setBounds(250,250,110,30);
		buttonWyjdz.setToolTipText(" Kliknij aby wyjsc z gry ");
		buttonWyjdz.addActionListener(this);
		add(buttonWyjdz);
		
		String jakieT³o = "tlo1.jpg";
		backGround  = new LoginBackgroundPanel(jakieT³o);
		add(backGround);
		
		setVisible(true);
    }
    
    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
    

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		if(obj==buttonLoguj)
		{
			if(fieldIP.getText().isEmpty() || fieldNick.getText().isEmpty())
			{
				 JOptionPane.showMessageDialog(this, "Nie wprowadzi³eœ wszystkich potrzebnych danych","Warning",JOptionPane.WARNING_MESSAGE);
				
			}
			else if (validate(fieldIP.getText())==false)
			{
				 JOptionPane.showMessageDialog(this, "Wprowadzi³eœ b³êdny adres ip","Warning",JOptionPane.WARNING_MESSAGE);
			}
			else
			{	
				int odp = JOptionPane.showConfirmDialog(this, "czy na pewno ?","Potwierdzenie",JOptionPane.YES_NO_OPTION);
				if (odp == JOptionPane.YES_OPTION)
				{
					WatekLogowania watek = new WatekLogowania(serwer,fieldNick.getText(),fieldIP.getText(),this,dimension);
					watek.start();	
				}	
			}
		}
		if(obj==buttonWyjdz)
		{
			int odp = JOptionPane.showConfirmDialog(this, "czy na pewno ?","Potwierdzenie",JOptionPane.YES_NO_OPTION);
			if (odp == JOptionPane.YES_OPTION)
			{
				dispose();
			}
		}	
	}
	

 public static void main(String args[])
 {
    Klient klient = new Klient();
 }
  
}

class RamkaInformacyjna extends JFrame// implements ActionListener
{
	
	private Dimension dim;
	private JLabel labelNapis;
	private JLabel labelOczekuj;
	private JButton buttonWyjdz;
	private PrintWriter out;
	private Klient rodzic;
	
	public RamkaInformacyjna(Dimension dim,Klient rodzic,PrintWriter out)
	{
		super("...");
		this.out=out;
		this.dim=dim;
		this.rodzic=rodzic;
		setSize(380,200);
	    setLocationRelativeTo(rodzic);
	    setResizable(false );
	    setLayout(null);
	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    rodzic.setEnabled(false);
	    
	    labelNapis =new JLabel();
	    labelNapis.setText("oczekiwanie na drugiego u¿ytkownika");
	    labelNapis.setFont(new Font("Arial",Font.BOLD,16));
	    labelNapis.setBounds(40,30,350,40);
	    add(labelNapis);
	    
	    labelOczekuj =new JLabel(" ... ");
	    labelOczekuj.setBounds(180,70,50,20);
	    add(labelOczekuj);

		LoginBackgroundPanel bg = new LoginBackgroundPanel("tloOczekiwania.jpg");
		add(bg);
		
		setVisible(true);
		new LiczSekundyDoExitu(labelOczekuj).start();
	}
	

	
public class LiczSekundyDoExitu extends Thread
	{
		private JLabel labelOczekuj;
		
		public LiczSekundyDoExitu(JLabel labelOczekuj)
		{
			this.labelOczekuj=labelOczekuj;
		}
		
		@Override
		public void run()
		{
			long start = System.currentTimeMillis();
			int i=1;
			while(true)
			{
				long koniec = System.currentTimeMillis();
				
				double obliczone = (koniec-start)/1000.0;
				
				if(obliczone==i)
				{
					labelOczekuj.setText(String.valueOf(20- (long) obliczone)); 
					i++;
				}
				else if(obliczone==21)
				{
					break;
				}
			}
		}
	}	
}



class WatekLogowania extends Thread
{
	private String nick;
	private String ip;
	private Socket serwer;
	private PrintWriter out;
	private BufferedReader in; 
	private Klient my;
	private Dimension dimension;
	
	public WatekLogowania(Socket serwer,String nick, String ip,Klient my,Dimension dimension)
	{
		this.nick=nick;
		this.ip=ip;
		this.serwer=serwer;
		this.my=my;
		this.dimension=dimension;
	}
	@Override
	public void run()
	{
	 try
	 {
			serwer = new Socket(); //192.168.56.1
			serwer.connect(new InetSocketAddress("localhost",5500));
			out = new PrintWriter(serwer.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(serwer.getInputStream()));
		    String tmp;
			out.println("1 " + nick);
			RamkaInformacyjna ocf = null;
			tmp: while((tmp=in.readLine())!=null)
			{
					if(tmp.equals("ok"))
					{
					     out.println("2 " + ip);
					     ocf = new RamkaInformacyjna(dimension,my,out);
					}
					else if(tmp.equals("exit"))
					{
						ocf.dispose();
						my.setEnabled(true);
						my.setVisible(true);
						break tmp;
					}
					else if(tmp.substring(0,tmp.indexOf(" ")).equals("odebra³em"))
					{	
						new KlientKomunikator(serwer,nick,tmp.substring(tmp.indexOf(" ")),in,out);
						my.dispose();
						ocf.dispose();
						break tmp;
					}
			  }
		} catch (IOException e)
		{
			JOptionPane.showMessageDialog(my,"Serwer nie jest aktualnie dostêpny ! ","ERROR",JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
	}
}


