

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LoginBackgroundPanel extends JPanel
{
	private BufferedImage backGroundFile;
	
	public LoginBackgroundPanel(String jakieT³o)
	{
		
		setSize(600,500);
		setLayout(null);
		
		try
		{
			this.backGroundFile = ImageIO.read(new File("static"+File.separator+jakieT³o));

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	protected void paintComponent(Graphics arg0)
	{
		super.paintComponent(arg0);
		Graphics2D g2d = (Graphics2D) arg0;
		g2d.drawImage(backGroundFile, 0,0,600,500, this);
	}

}
