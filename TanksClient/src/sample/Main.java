package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tanks Client. NickName: "+ Controller.getNickName()+" ID:" + Controller.getIdTank());
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        
        scene.setOnKeyPressed(event -> {
        	switch(event.getCode())
        	{
        		case W: Controller.sendToServer("TankMove 1000"); break;	//GÓRA DÓ£ PRAWO LEWO
        		case S: Controller.sendToServer("TankMove 0100"); break;	//GÓRA DÓ£ PRAWO LEWO
        		case D: Controller.sendToServer("TankMove 0010"); break;	//GÓRA DÓ£ PRAWO LEWO
        		case A: Controller.sendToServer("TankMove 0001"); break;	//GÓRA DÓ£ PRAWO LEWO
        		case SPACE: Controller.sendToServer("Shot"); break;
        	}
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
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
					case "port": Controller.setPort(Integer.parseInt(properties.getProperty(key))); break;
					case "hostname": Controller.setHostname(properties.getProperty(key)); break;
					case "nickname": Controller.setNickName(properties.getProperty(key)); break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] args) throws IOException {
    	loadXMLFile();
        launch(args);
    }
}