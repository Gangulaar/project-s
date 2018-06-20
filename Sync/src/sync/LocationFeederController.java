package sync;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import static sync.Progress.DestFileSize;

/**
 * FXML Controller class
 *
 * @author agangula
 */
public class LocationFeederController extends Thread implements Initializable {

    public static Session session;
    @FXML
    public TextField source,downDir;
    @FXML
    public Button sync;
    @FXML
    public ProgressIndicator progressBar;
    @FXML
    public Label progressLabel;
    static int sourceFileSize = 0;
    static double percent=0;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    public void setSession(Session passed_session)
    {
        LocationFeederController.session = passed_session;
    }
    @FXML
    public void selectLog()
    {
        FileSelectFXMLController fsfxmlc = new FileSelectFXMLController();
        System.out.println("Session passed to LocationFeeder is: "+session);
        fsfxmlc.setSession(session);  
        Stage stage1=new Stage();
        
        try 
        {
            fsfxmlc.start(stage1);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(LocationFeederController.class.getName()).log(Level.SEVERE, null, ex);
        }
        stage1.showAndWait();
        System.out.println(FileSelectFXMLController.choosenFile);
        source.setText(FileSelectFXMLController.choosenFile);
    }
    public void openFileChooser()
    {
        DirectoryChooser fc = new DirectoryChooser();
        fc.setTitle("Select Path");
        File selectedDir = fc.showDialog(new Stage());
        if (selectedDir == null)
        {
            //Handle if directory is null
        }
        else
        {
            downDir.setText(selectedDir.getAbsolutePath());
            System.out.println(source.getText());
            if (!source.getText().equals(""))
            {
                sync.setDisable(false);
            }
        }
    }
    DownloadFile df = null;
    Syncer s = null;
    double transferred;
    static int lines_present_synced = 0;
    @FXML
    public void startSync()
    {
        System.out.println("User intiated SYNC");
        for(double i =0.00 ; i<= 0.99;i+=0.01)
                    {
                        try 
                        {
                            Thread.sleep(1000);
                            progressBar.setProgress(i);
                            if(session != null)
                            {
                                setProgress();
                                break;
                            }
                        }
                        catch (InterruptedException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
        df = new DownloadFile(session);
        try 
        {
            df.openChannel();
            progressBar.setProgress(-1);
        }
        catch (JSchException ex) 
        {
            Logger.getLogger(LocationFeederController.class.getName()).log(Level.SEVERE, null, ex);
        }
        new Thread()
        {
            public void run()
            {
                
                df.downloadCompleteFile(source.getText(), downDir.getText());
                s = new Syncer(session);
                //lines_present_synced=Syncer.getFileCount(source.getText());
                //System.out.println("Download file name: "+downDir.getText().substring(downDir.getText().lastIndexOf("/")+1,downDir.getText().length()));
               // s.establishChannel(downDir.getText().substring(downDir.getText().lastIndexOf("/")+1,downDir.getText().length()));
            }
        }.start();
        System.out.println("Source text "+source.getText());
        Syncer.present_set_lines = Syncer.getFileCount(source.getText());
    }    
    public void setProgress()
    {
        progressBar.setProgress(LocationFeederController.percent);
    }
    public static Stage stage1;
    public static Scene scene1;    
    public void start(Stage stage) throws IOException 
    {
        LocationFeederController.stage1=stage;
        Parent root = FXMLLoader.load(getClass().getResource("LocationFeeder.fxml"));
        Scene scene =  new Scene(root);
        LocationFeederController.scene1=scene;
        stage.setScene(scene);
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
        {
          public void handle(WindowEvent we) {
              System.exit(0);
          }
      });
        
    }
}
