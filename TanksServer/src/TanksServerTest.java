import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import org.junit.jupiter.api.Test;

class TanksServerTest {
	
	@Test
	public void shouldSetDefaultPort() {
		Main.setPort(1234);
		assertEquals(Main.getPort(), 1234);
	}
	
	@Test
	public void shouldSetDefaultHealthPoints()
	{
		Main.setDefaultHP(1000);
		assertEquals(Main.getDefaultHP(), 1000);
	}

	@Test
	public void shouldSetGameSpeed()
	{
		Main.setGameSpeed(5);
		assertEquals(Main.getGameSpeed(), 5);
	}
	
	@Test
	public void shouldGetActuallyContentList()
	{
		Vector beforeAdd = Main.getActiveClients();
		int sizeBeforeAdd = beforeAdd.size();
		beforeAdd.add(new ClientHandler("exampleNickname", 0, null, null, 0, 0));
		Vector afterAdd = Main.getActiveClients();
		assertNotEquals(sizeBeforeAdd, afterAdd.size());
		Main.getActiveClient(sizeBeforeAdd).disconnect();
	}
	
	@Test
	public void shouldGetExactlyWhatWeAddPlusDisconnectingPlayer()
	{
		Vector vectorWithClients = Main.getActiveClients();
		int vectorWithClientsSize = vectorWithClients.size();
		//(String name, int idTank, Socket s, DataInputStream dis, DataOutputStream dos, int x, int y)
		vectorWithClients.add(new ClientHandler("exampleNickname", 111, null, null, 222, 333));
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getNameOfClient(), "exampleNickname");
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getIdTank(), 111);
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getDis(), null);
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getDos(), null);
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getX(), 222);
		assertEquals(Main.getActiveClient(vectorWithClientsSize).getY(), 333);
		assertTrue(Main.getActiveClient(vectorWithClientsSize).isConnected());
		Main.getActiveClient(vectorWithClientsSize).disconnect();
		assertFalse(Main.getActiveClient(vectorWithClientsSize).isConnected());
	}

	public void testConnectionWithSupportSendToAllMethod()
	{
		Thread testConnection = new Thread(new Runnable()
        {	    	
			@Override
			public void run() {
				try
				{
					final Socket s = new Socket("localhost", 1234);
			        DataInputStream dis = new DataInputStream(s.getInputStream());
			    	DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			    	String tmp;
			    	
			        dos.writeUTF("NewPlayer "+"exampleNickname");
			        float gameSpeed = dis.readFloat();
			        float defaultHP = dis.readFloat();
			        int idTank = dis.readInt();
			        assertEquals(gameSpeed, 5);
			        assertEquals(defaultHP, 1000);
			        assertEquals(idTank, Main.getNumberOfClients());
			        
			        do
			        {
			        	tmp = dis.readUTF();
			        	if(tmp.startsWith("Testowa"))
			        	{
			        		assertEquals(tmp, "TestowaWiadomoscDoWszystkich");
			        		break;
			        	}    		 
			        }while(true);
			        
			        Main.getActiveClient(idTank).disconnect();
			        s.close();
				}
				catch(IOException e)
				{
					System.out.println(e);	
				}
			}
        });
		testConnection.start();
	}
	
	public void testConnectionWithMovementTank()
	{
		Thread testConnection = new Thread(new Runnable()
        {	    	
			@Override
			public void run() {
				try
				{
					final Socket s = new Socket("localhost", 1234);
			        DataInputStream dis = new DataInputStream(s.getInputStream());
			    	DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			    	
			        dos.writeUTF("NewPlayer "+"exampleNickname");
			        float gameSpeed = dis.readFloat();
			        float defaultHP = dis.readFloat();
			        int idTank = dis.readInt();
			        assertEquals(gameSpeed, 5);
			        assertEquals(defaultHP, 1000);
			        assertEquals(idTank, Main.getNumberOfClients()-1);
			        System.out.println(dis.readUTF());
			        int posY = Main.getActiveClient(idTank).getY();
			        dos.writeUTF("TankMove 1000");
			        assertEquals(posY-5, Main.getActiveClient(idTank).getY());
			        dos.writeUTF("Shot");
			        assertEquals(dis.readUTF(), "NewBomb "+idTank);
			        dos.writeUTF("dmg "+idTank+"|40");
			        assertEquals(Main.getActiveClient(idTank).getHealth(), defaultHP-40);
			        Main.getActiveClient(idTank).disconnect();
			        s.close();
				}
				catch(IOException e)
				{
					System.out.println(e);	
				}
			}
        });
		testConnection.start();
	}
	
	@Test
	public void supportClient() throws IOException, InterruptedException
	{
		new Thread(new Runnable()
        {	    	
			@Override
			public void run() {
					try {
						Main.main(null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
        }).start();
		
		testConnectionWithSupportSendToAllMethod();
		Main.sendToAll("TestowaWiadomoscDoWszystkich");
		testConnectionWithMovementTank();
	}
	
	@Test
	public void checkLoadedValueFromXMLFile()
	{
		Main.loadXMLFile();
		assertEquals(Main.getPort(), 1234);
		assertEquals(Main.getDefaultHP(), 1000);
		assertEquals(Main.getGameSpeed(), 5);
	}
}
