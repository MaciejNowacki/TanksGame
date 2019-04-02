package sample;

import javafx.scene.shape.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Controller {
	static private String hostname;
	static private int port;
    static private boolean running = true;
    static private List<String> chatList = new ArrayList<>();
    static private List<Tank> tankList = new LinkedList<>();
    static private List<Bomb> bombList = new LinkedList<>();
    static private String nickName = null;
    static private int idTank = -1;
    static private DataOutputStream dos = null;
    static private DataInputStream dis = null;
    static private float gameSpeed;
    static private float defaultHP;
    
    @FXML
    private TextField textMessage;
    
    @FXML
    private Label chatLabel;

    @FXML
    private AnchorPane gamePlace;
    
    public static float getGameSpeed()
    {
    	return gameSpeed;
    }
    
    public static void setHostname(String value)
    {
 	   hostname = value;
    }
    
    public static String getHostname()
    {
 	   return hostname;
    }
    
    public static void setPort(int value)
    {
 	   port = value;
    }
    
    public static int getPort()
    {
 	   return port;
    }
    
    public static int getIdTank()
    {
    	return idTank;
    }
    
    public static String getNickName()
    {
    	return nickName;
    }
    
    public static void setNickName(String name)
    {
    	if(name.equals("null"))
    	{
    		Random generate = new Random();
            String[] names = {"John", "Marcus", "Susan", "Henry", "Jessica", "Andrzej", "Szymus", "H4xxi0r", "Gruby", "Lysy", "WydziaranyTomek123", "ziomek2004", "PokonamWasWszystkichxdd", "CiskamWasNaHaxach"};
            nickName = names[generate.nextInt(names.length)];
    	}
    	else
    	{
    		nickName = name;
    	}
    }
    
    public static void sendToServer(String mess)
    {
    	Socket s;
		try {
	    	dos.writeUTF(mess);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    public void sendMessage() throws UnknownHostException, IOException
    {
    	String tmp = textMessage.getText();
    	gamePlace.requestFocus();
    	
    	if(tmp.length() > 3)
    	{
    		sendToServer("NewMessage "+nickName+"(ID: "+idTank+"): "+tmp);
            textMessage.setText("");
    	}
    }
    
    public boolean findId(int id)
    {
    	for (Tank tank: tankList)
        {
            if (tank.getIdTank() == id)
            {
            	return true;
            }
        }
    	return false;
    }
    
    
    public String generateChat()
    {
    	String text = "";
    	int index = 0;
    	int pos1;
    	
    	for(String mess: chatList)
    	{
    		for(int i = 0; i < mess.length(); i++)
    		{
    			text += mess.charAt(i);
    			if(i !=0 && i%25 == 0) text += "\n";
    		}
    		text += "\n";
    		index++;
    		if(index > 30)
    		{
    			chatList.remove(30);
    			break;
    		}
    	}
    	
    	return text;
    }
    
    public boolean checkCollision(int idBomb, int idTank)
    {
    	int posX = bombList.get(idBomb).getX();
    	int posY = bombList.get(idBomb).getY();
    	int direct = bombList.get(idBomb).getDir();
    	Random random = new Random();
    	
    	for(Tank tank: tankList)
    	{
    		if(tank.getIdTank() != idTank)
    		{
    			if(tank.isShowing())
    			{
    				switch(direct)
    				{
    				case 0: 
	    					if(Math.abs(tank.getX()-posX) < 15)
	    					{
	    						if(tank.getY() >= posY && tank.getY() <= posY+(gameSpeed*2))
	    						{
	    							float dmg = 20 + random.nextFloat() * (50 - 20);
	    							if(bombList.get(idBomb).getSteps() > 1)
	    								sendToServer("dmg "+tank.getIdTank()+"|"+dmg);
	    		    				bombList.get(idBomb).setSteps(-100);
	    		    				return true;
	    						}
	    					}
	    					break;
    					//if(Math.abs(tank.getY()-posY) < 15 || Math.abs(tank.getY()-posY-gameSpeed*2) < 15)	
    				case 1:
	    					if(Math.abs(tank.getX()-posX) < 15)
							{
	    						if(tank.getY() <= posY && tank.getY() >= posY-(gameSpeed*2))
	    						{
	    							float dmg = 20 + random.nextFloat() * (50 - 20);
	    		    				sendToServer("dmg "+tank.getIdTank()+"|"+dmg);
	    		    				return true;
	    						}
	    					}
	    					break;
    				case 2:
	    					if(Math.abs(tank.getY()-posY) < 15)
							{
	    						if(tank.getX() <= posX && tank.getX() >= posX-(gameSpeed*2))
	    						{
	    							float dmg = 20 + random.nextFloat() * (50 - 20);
	    		    				sendToServer("dmg "+tank.getIdTank()+"|"+dmg);
	    		    				return true;
	    						}
	    					}
	    					break;
    				case 3: 
	    					if(Math.abs(tank.getY()-posY) < 15)
	    					{
	    						if(tank.getX() >= posX && tank.getX() <= posX+(gameSpeed*2))
	    						{
	    							float dmg = 20 + random.nextFloat() * (50 - 20);
	    							if(bombList.get(idBomb).getSteps() > 1)
	    								sendToServer("dmg "+tank.getIdTank()+"|"+dmg);
	    		    				bombList.get(idBomb).setSteps(-100);
	    		    				return true;
	    						}
	    					}
	    					break;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    @FXML 
    private void initialize() throws UnknownHostException, IOException
    {
    	final Socket s = new Socket(hostname, port);
        dis = new DataInputStream(s.getInputStream());
    	dos = new DataOutputStream(s.getOutputStream());
    	
        dos.writeUTF("NewPlayer "+nickName);
        gameSpeed = dis.readFloat();
        defaultHP = dis.readFloat();
        idTank = dis.readInt();
        
        Thread readMessage = new Thread(new Runnable()
        {
            String tmp;
            Rectangle tmpTank;
            int posX, posY, idTankRenderer, angleTankToRenderer, bombs = 0;
            boolean isShow, alertShow = false;
            float healthTank;
            Label tmpNickname;
            
            @Override
            public void run()
            {
                do
                {
                    try
                    {
                        tmp = dis.readUTF();
                        System.out.println(tmp);
                        if(tmp.startsWith("NewMessage"))
                        {
                        	Platform.runLater(new Runnable() {
                        	    public void run() {
                        	    	chatList.add(0, tmp.substring(11, tmp.length()));
                        	    	chatLabel.setText(generateChat());
                        	    }
                        	});
                        }
                        
                        if(tmp.startsWith("NewBomb"))
                        {
                        	idTankRenderer = Integer.parseInt(tmp.substring(8, tmp.length()));
                        	bombList.add(new Bomb(idTankRenderer, tankList.get(idTankRenderer).getAngle(), tankList.get(idTankRenderer).getX()+5, tankList.get(idTankRenderer).getY()+5));
                        	
                        	Thread sendBomb = new Thread(new Runnable()
                            {
                        		Rectangle tmpBomb = null;
                    	    	int posBombX, posBombY, volBombs = bombs, angle, idTank = idTankRenderer;
                    	    	boolean collision = false;
                    	    	
								@Override
								public void run() {
									do
									{
										bombList.get(volBombs).moveBomb();
										posBombX = bombList.get(volBombs).getX();
                        	    		posBombY = bombList.get(volBombs).getY();
                        	    		
                        	    		// System.out.println("("+posBombX+", "+posBombY+")");
                        	    		
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												if(tmpBomb != null)
                                	    			gamePlace.getChildren().remove(tmpBomb);
                                	    		tmpBomb = new Rectangle(posBombX, posBombY, 10, 10);
                                	    		gamePlace.getChildren().add(tmpBomb);

                                	    			if(checkCollision(volBombs, idTank))	//TODO: Dostaje dwa razy dmg - fix it
                                	    				collision = true;
                                	    		//System.out.println("Wyrenderowa³em!");
											}
										});
										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}while(posBombX >= 10 && posBombX <= 575 && posBombY >= 10 && posBombY <= 560 && !collision);
									Platform.runLater(new Runnable() 
									{
										@Override
										public void run() 
										{
											gamePlace.getChildren().remove(tmpBomb);
										}
									});	
								}
                            });
                        	bombs++;
                        	sendBomb.start();
                        }
                        		
                        if(tmp.startsWith("NewPos"))
                        {
                        	tmp = tmp.substring(7, tmp.length());
                        	String[] parts = tmp.split("\\|");
                        	posX = Integer.parseInt(parts[0]);
                        	posY = Integer.parseInt(parts[1]);
                        	idTankRenderer = Integer.parseInt(parts[2]);
                        	angleTankToRenderer = Integer.parseInt(parts[3]);
                        	healthTank = Float.parseFloat(parts[4]);
                        	isShow = Boolean.parseBoolean(parts[5]);
                        	//System.out.println(idTank+". "+posX+","+posY);
                        	//System.out.println(isShow);
                        	if(findId(idTankRenderer))
                        	{
                        		tankList.get(idTankRenderer).setX(posX);
                        		tankList.get(idTankRenderer).setY(posY);
                        		tankList.get(idTankRenderer).setTankAngle(angleTankToRenderer);
                        		tankList.get(idTankRenderer).setHealth(healthTank);
                        		
                        		//System.out.println("#"+idTankRenderer+". ("+posX+", "+posY+") Obrót: "+angleTankToRenderer);
                        		if(!isShow)
                        			tankList.get(idTankRenderer).hidden();
                        	}
                        	else
                        	{
                        		tankList.add(idTankRenderer, new Tank(posX, posY, angleTankToRenderer, healthTank, idTankRenderer, null, isShow));
                        	}
                        	
                        	//System.out.println("Tworze czolg id "+idTank);
                        	
                        	Platform.runLater(new Runnable() {
                        			//boolean isShowTank = isShow;
                            		int idTankToRender = idTankRenderer;
                            		
                            		//int posXToRender = posX;
                            		//int posYToRender = posY;
                            		float healthTank = tankList.get(idTankToRender).getHealth();
                            	    public void run() 
                            	    {
                            	    	if(tankList.get(idTankToRender).isShowing())
                                		{
                        	    		 if(tankList.get(idTankToRender).getModel() != null)
                        	    		 {
                        	    			 gamePlace.getChildren().remove(tankList.get(idTankToRender).getModel());
                        	    			 gamePlace.getChildren().remove(tankList.get(idTankToRender).getTankIdLabel());
                        	    		 }
    	                    	    		 
    	                    	    	 tmpTank = new Rectangle(tankList.get(idTankToRender).getX(), tankList.get(idTankToRender).getY(), 20, 20);
    	                    	    	 tmpNickname = new Label(""+idTankToRender);
    	                    	    	 tmpNickname.relocate(tankList.get(idTankToRender).getX()+20, tankList.get(idTankToRender).getY()+20);
    	                    	    	 if(healthTank > 0.9*defaultHP)
    	                    	    		 tmpTank.setFill(Color.GREEN);
    	                    	    	 else if(healthTank > 0.5*defaultHP)
    	                    	    		 tmpTank.setFill(Color.ORANGE);
    	                    	    	 else if(healthTank > 0.25*defaultHP)
    	                    	    		 tmpTank.setFill(Color.YELLOW);
    	                    	    	 else if(healthTank > 0)
    	                    	    		 tmpTank.setFill(Color.RED);
    	                    	    	 else
    	                    	    	 {
    	                    	    		 tmpTank.setFill(Color.BLACK);
    	                    	    		 if(tankList.get(idTankToRender).getIdTank() == getIdTank())
    	                    	    		 {
    	                    	    			if(!alertShow)
    	                    	    			{
    	                    	    				//Alert alert = new Alert(AlertType.INFORMATION, "Zosta³eœ zabity!");
         	                    	    	 		System.exit(0);
    	                    	    			}
    	                    	    		 } 
    	                    	    	 }
    	                    	    	 

    	                    	    	 
    	                    	    	 
    	                                 gamePlace.getChildren().add(tmpTank);
    	                                 gamePlace.getChildren().add(tmpNickname);
    	                                 
    	                                 tankList.get(idTankToRender).setNewModel(tmpTank);
    	                                 tankList.get(idTankToRender).setTankIdLabel(tmpNickname);
    	                                 
    	                                 //System.out.println("Stworzylem czolg id "+idTankToRender); 
                                		}
                            	    	else
                            	    		if(tankList.get(idTankToRender).getModel() != null)
                            	    		{
                            	    			 gamePlace.getChildren().remove(tankList.get(idTankToRender).getTankIdLabel());
                            	    			 gamePlace.getChildren().remove(tankList.get(idTankToRender).getModel());
                            	    		} 
                            	    }
                            });
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        break;
                    }
                }while(running);
            }
        });

        readMessage.start();
        Platform.runLater( () -> gamePlace.requestFocus() );
    }
}
