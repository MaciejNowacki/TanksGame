import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable
{
	private String nameOfClient;
	private final DataInputStream dis;
	private final DataOutputStream dos;
	private boolean connected;
	private int posX;
	private int posY;
	private int angle = 0;
	private int idTank;
	private float health = Main.getDefaultHP();
	private float speed = Main.getGameSpeed();
	
	public float getHealth()
	{
		return this.health;
	}
	
	public void dmg(float value)
	{
		health -= value;
	}
	
	public int getX()
	{
		return posX;
	}
	
	public int getY()
	{
		return posY;
	}
	
	public boolean isConnected()
	{
		return connected;
	}

	public void disconnect()
	{
		connected = false;
	}
	
	public DataOutputStream getDos()
	{
		return dos;
	}
	
	public DataInputStream getDis()
	{
		return dis;
	}
	
	public void isCollision()
	{
		int posX = this.getX();
		int posY = this.getY();

		for (ClientHandler mc : Main.getActiveClients())
        {
			if(mc.idTank != this.getIdTank())
				if(mc.isConnected())
					if(Math.abs(mc.getX()-posX) < 15 && Math.abs(mc.getY()-posY) < 15)
					{
						this.dmg(this.getHealth());
						mc.dmg(mc.getHealth());
						try {
							Main.sendToAll("NewPos "+this.getX()+"|"+this.getY()+"|"+this.getIdTank()+"|"+this.getTankAngle()+"|"+this.getHealth()+"|"+this.isConnected());
							Main.sendToAll("NewPos "+mc.getX()+"|"+mc.getY()+"|"+mc.getIdTank()+"|"+mc.getTankAngle()+"|"+mc.getHealth()+"|"+mc.isConnected());
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
        }
	}
	
	public int getIdTank()
	{
		return idTank;
	}
	
	public int getTankAngle()
	{
		return angle;
	}
	
	public void move(String direction)
	{
		switch(direction)
		{
			case "1000":	angle=0;	
			if(getY()-speed > 0)
			{
				isCollision();
				posY-=speed; 
			}
			else 
				posY = 0; 	
			break;
			case "0100":	
				angle=1;	
				if(getY()+speed < 560)
				{
					isCollision();
					posY+=speed; 
				}
				else 
					posY = 560; 
				break;
			case "0010":	
				angle=2;	
				if(getX()+speed < 570)
				{
					isCollision();
					posX+=speed;
				}
				else 
					posX = 570; 
				break;
			case "0001":	
				angle=3;	
				if(getX()-speed > 0)
				{
					isCollision();
					posX-=speed;
				}
				else 
					posX = 0; 	
				break;
		}
	}
	
	public ClientHandler(String name, int idTank, DataInputStream dis, DataOutputStream dos, int x, int y)
	{
		this.dis = dis;
		this.dos = dos;
		this.nameOfClient = name;
		this.connected = true;
		this.posX = x;
		this.posY = y;
		this.idTank = idTank;
	}
	
	public String getNameOfClient()
	{
		return nameOfClient;
	}
    
	@Override
	public void run()
	{
		String recived;

		do
		{
			try
			{
				recived = dis.readUTF();
				System.out.println(recived);
				
				if(recived.startsWith("NewMessage"))
					Main.sendToAll(recived);
				
				if(recived.startsWith("TankMove"))
				{
					String direction = recived.substring(9, recived.length());
					this.move(direction);
					Main.sendToAll("NewPos "+this.getX()+"|"+this.getY()+"|"+this.getIdTank()+"|"+this.getTankAngle()+"|"+this.getHealth()+"|"+this.isConnected());
				}
				
				if(recived.startsWith("Shot"))
				{
					Main.sendToAll("NewBomb "+this.getIdTank());
				}
				
				if(recived.startsWith("dmg"))
				{
					int idPlayer = Integer.parseInt(recived.substring(4, recived.indexOf('|')));
					float dmg = Float.parseFloat(recived.substring(recived.indexOf('|')+1, recived.length()));
					Main.getActiveClient(idPlayer).dmg(dmg);
					Main.sendToAll("NewPos "+Main.getActiveClient(idPlayer).getX()+"|"+Main.getActiveClient(idPlayer).getY()+"|"+Main.getActiveClient(idPlayer).getIdTank()+"|"+Main.getActiveClient(idPlayer).getTankAngle()+"|"+Main.getActiveClient(idPlayer).getHealth()+"|"+Main.getActiveClient(idPlayer).isConnected());
				}
			}
			catch (IOException e)
			{
				disconnect();
				System.out.println("Wykryto utrate polaczenia z uzytkownikiem "+this.nameOfClient+". Usunieto z listy aktywnych polaczen.");
				try {
					Main.sendToAll("NewPos "+this.getX()+"|"+this.getY()+"|"+this.getIdTank()+"|"+this.getTankAngle()+"|"+this.getHealth()+"|"+this.isConnected());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}while(connected);
	}
}
