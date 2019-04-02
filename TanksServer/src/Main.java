import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class Main
{
	static private Vector<ClientHandler> activeClients = new Vector<>();
	static private int numberOfClients = 0;
	static private int port;
	static private float defaultHP;
	static private float gameSpeed;
	
	public static int getNumberOfClients()
	{
		return numberOfClients;
	}
	
	public static void setPort(int value)
	{
		port = value;
	}
	
	public static int getPort()
	{
		return port;
	}
	
	public static void setDefaultHP(float value)
	{
		defaultHP = value;
	}
	
	public static float getDefaultHP()
	{
		return defaultHP;
	}
	
	public static void setGameSpeed(float value)
	{
		gameSpeed = value;
	}
	
	public static float getGameSpeed()
	{
		return gameSpeed;
	}
	
	public static Vector<ClientHandler> getActiveClients()
	{
		return activeClients;
	}

	public static ClientHandler getActiveClient(int id)
	{
		return activeClients.get(id);
	}

    public static void sendToAll(String text) throws IOException
    {
    	for (ClientHandler mc : activeClients)
        {
            if (mc.isConnected() == true)
            {
            	mc.getDos().writeUTF(text);
            }
        }
    }
    
    public static void sendData() throws IOException
    {
    	for (ClientHandler mc : activeClients)
        {
            sendToAll("NewPos "+mc.getX()+"|"+mc.getY()+"|"+mc.getIdTank()+"|"+mc.getTankAngle()+"|"+mc.getHealth()+"|"+mc.isConnected());
        }
    }
    
	public static int findPlayerByNickname(String name)
	{
    	for (ClientHandler mc : activeClients)
        {
            if (mc.getNameOfClient().equals(name))
            {
            	return activeClients.indexOf(mc);
            }
        }
    	return -1;
	}
	
	public static void loadXMLFile()
	{
		try {
			File file = new File("config.xml");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.loadFromXML(fileInput);
			fileInput.close();

			Enumeration enuKeys = properties.keys();
			String key;
			while (enuKeys.hasMoreElements()) {
				switch(key = (String) enuKeys.nextElement())
				{
					case "port": setPort(Integer.parseInt(properties.getProperty(key))); break;
					case "defaultHP": setDefaultHP(Float.parseFloat(properties.getProperty(key))); break;
					case "gameSpeed": setGameSpeed(Float.parseFloat(properties.getProperty(key))); break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		loadXMLFile();	
		ServerSocket ss = new ServerSocket(port);
		Socket s;
		String recived = null;
		ClientHandler new_client = null;
		Thread tmp = null;
		Scanner scn = new Scanner(System.in);
		Random random = new Random();
		
		do
		{
			s = ss.accept();
			System.out.println(s);
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			recived = dis.readUTF();
			//System.out.println(recived);
			dos.writeFloat(getGameSpeed());
			dos.writeFloat(getDefaultHP());
			
			if(recived.startsWith("NewPlayer "))
			{
				int posX = random.nextInt(560);
				int posY = random.nextInt(560);
				dos.writeInt(numberOfClients);
				new_client = new ClientHandler(recived.substring(10, recived.length()), numberOfClients, dis, dos, posX, posY);
				tmp = new Thread(new_client);
				activeClients.add(new_client);
				sendData();
				//sendToAll("NewPos "+activeClients.get(numberOfClients).getX()+"|"+activeClients.get(numberOfClients).getY()+","+numberOfClients);
				tmp.start();
				numberOfClients++;
			}
		}while(numberOfClients > 0);
	} 
}