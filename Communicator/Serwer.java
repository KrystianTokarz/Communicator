import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


class DaneKlienta
{
	private  String nick;
	private  String ip;
	private  Socket socket;
	private boolean isBusy = false;
	
	public boolean getIsBusy()
	{
		return isBusy;
	}

	public void setIsBusy(boolean isBusy)
	{
		this.isBusy = isBusy;
	}
	
	public String getNick()
	{
		return nick;
	}

	public void setNick(String nick)
	{
		this.nick = nick;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public Socket getSocket()
	{
		return socket;
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
}


public class Serwer extends JFrame implements ActionListener{

    public boolean serwerIsRunning =false;
    private ServerSocket socketNasluchujacy;
	protected LinkedList<DaneKlienta> listaKlientow = new LinkedList<>();
	private JButton buttonOn, buttonOff;
	
	
	public Serwer()
	{
		 super("Serwer");
		 Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	     setLocation(dimension.width/3,dimension.height/4);
	     setSize(250,80);
	     setLayout(new FlowLayout());
	     buttonOn = new JButton("ON");
	     buttonOn.setBackground(Color.GREEN);
	     buttonOn.addActionListener(this);
	     buttonOff = new JButton("OFF");
	     buttonOff.setBackground(Color.RED);
	     buttonOff.addActionListener(this);
	     add(buttonOn);
	     add(buttonOff);
	     setVisible(true);
	     
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if(obj==buttonOn)
		{
			if(serwerIsRunning==true)
			{
				JOptionPane.showMessageDialog(this,"Serwer ju¿ dzia³a");
			}
			else
			{
			buttonOn.setEnabled(false);	
		
			serwerIsRunning=true;
			uruchomSerwer();
			buttonOff.setEnabled(true);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}
		}
		if(obj==buttonOff)
		{
			if(serwerIsRunning==false)
				JOptionPane.showMessageDialog(this,"Serwer nie jest uruchomiony");
			else 
			{
				serwerIsRunning = false;
				try
				{
					socketNasluchujacy.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				buttonOn.setEnabled(true);	
				buttonOff.setEnabled(false);
				setDefaultCloseOperation(EXIT_ON_CLOSE);
			}
		}

	}
	
    public void uruchomSerwer()
    {
    	
        try 
        {
        socketNasluchujacy = new ServerSocket();
        socketNasluchujacy.bind(new InetSocketAddress("localhost",5500));
        Sluchacz sl =  new Sluchacz();
        sl.start();
//        while (serwerIsRunning || sl.isAlive());
//            socketNasluchujacy.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) 
    {
    	Serwer serwer = new Serwer();
    	
    }

	class Sluchacz extends Thread
	{	
		@Override
		public void run()
		{
			while(serwerIsRunning)
			{
				try
				{
					Socket klient = socketNasluchujacy.accept();
					if(klient.isBound())
					{
		            WatekNasluchujacyKlientow threadListen = new WatekNasluchujacyKlientow(klient);
		            threadListen.start();
					}
				} catch (IOException e)
				{
					serwerIsRunning=false;
					//e.printStackTrace();
				}
			}
		}
	}

    class WatekNasluchujacyKlientow extends Thread
    {
    	private Socket klient;
    	private String naszNick;
    	private PrintWriter out;
    	private BufferedReader in;
    	private DaneKlienta daneKlienta;
    	
    	public WatekNasluchujacyKlientow(Socket klient)
		{
			this.klient=klient;
		}

        @Override
        public void run() 
        {
        	try
			{
				  out = new PrintWriter(klient.getOutputStream(),true);
 				  in = new BufferedReader(new InputStreamReader(klient.getInputStream()));
 				  String tmp;

 				  uchwyt: while((tmp=in.readLine())!=null)
 				  {
 					  int numer =  Integer.parseInt(tmp.substring(0, 1));
 					  if(numer==1)
 					  {
 						 synchronized (listaKlientow) {
 						 daneKlienta = new DaneKlienta();
 						 daneKlienta.setSocket(klient);
 						 daneKlienta.setIp(klient.getInetAddress().getHostAddress());
 						 daneKlienta.setNick(tmp.substring(2));
// 						 daneKlienta.setIsBusy(true);
 						 naszNick=tmp.substring(2);
 						 listaKlientow.add(daneKlienta);
 						 out.println("ok");
 						 }
 					  }
 					  if(numer==2)
 					  {
 						 String ipKolegi = tmp.substring(2);
 						 long rozpoczecie = System.currentTimeMillis();
 						 
 					     while(true)
 						 { 
 						  	int rozmiarBuforu = listaKlientow.size(); 	
 						  	for(int i=0;i<rozmiarBuforu;i++)
 						  	{
 						  		synchronized (listaKlientow)
 						  		{ 
	 						  		if(ipKolegi.equals(listaKlientow.get(i).getIp()) 
	 						  				&& (naszNick.equals(listaKlientow.get(i).getNick())==false)==true
	 						  				&& listaKlientow.get(i).getIsBusy()==false)
	 						  		{
	 						  			listaKlientow.get(i).setIsBusy(true);
	 						  			out.println("odebra³em " + listaKlientow.get(i).getNick());
	 						  			new WatekKomunikacyjny(klient,listaKlientow.get(i).getSocket(),in,out,naszNick,daneKlienta).start();
	 						  			break uchwyt;
	 						  		}
 						  		}
 						  	}		  	
 						  	long zakonczenie = System.currentTimeMillis(); 
 						  	if(((zakonczenie-rozpoczecie)/1000.0)>21)
 						  	{
 						  		listaKlientow.remove(daneKlienta);
 						  		out.println("exit");
 						  		in.close();
 						  		out.close();
 						  	    klient.close();
 						  		break uchwyt;
 						  	}
 						  }
 					  }
 				  }
			} catch (IOException e)
			{
				e.printStackTrace();
			}
        }
    }
    
    
    class WatekKomunikacyjny extends Thread
    {
    	private Socket wlasciciel;
    	private Socket kolega;
    	private PrintWriter  outKolega;
    	private BufferedReader inWlasciciel;
    	private String nick;
    	private DaneKlienta daneKlienta;
    	
    	public WatekKomunikacyjny(Socket wlasciciel, Socket kolega,BufferedReader inWlasciciel,PrintWriter outWlasciciel,String nick,DaneKlienta daneKlienta )
    	{
    		this.wlasciciel=wlasciciel;
    		this.kolega = kolega;
    		this.inWlasciciel=inWlasciciel;
    		this.nick=nick;
    		this.daneKlienta=daneKlienta;
    	try
    	{
    		outKolega = new PrintWriter(this.kolega.getOutputStream(),true);
		
		} catch (IOException e)
		{
			e.printStackTrace();
		}
    	}
    	
    	@Override
    	public void run()
    	{
    		String tmp;
    		try
			{
				while((tmp=inWlasciciel.readLine())!=null)
				{
				if(tmp.equals("exit"))
				{
					synchronized (listaKlientow)
					{
						listaKlientow.remove(daneKlienta);
					}
					if(kolega.isConnected())
					outKolega.println("exit");
					inWlasciciel.close();
					wlasciciel.close();
					break;  		
				}
				else
					outKolega.println(tmp);
 
				}
			} catch (IOException e)
			{
			
				e.printStackTrace();
			}
    	}
    }


	
}
