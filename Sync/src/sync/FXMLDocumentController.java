/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 * @author agangula
 */
public class FXMLDocumentController extends Thread implements Initializable {
    
    @FXML
    private ProgressIndicator authenticProgress;
    private ConnectionEstablisher ce;
    private Session session;
    int timer =40;
    private Thread currentRunThread;
    int invalid =0;
    @FXML
    private ComboBox <String> hostName;
    @FXML
    private Button button,button1,button2;
    @FXML
    private TextField userName,password;
    @FXML
    public void checkFields()
    {
        System.out.println("Checking");
        try
        {
            if(!"".equals(userName.getText().toString()) && !password.getText().toString().equals("") && !hostName.getSelectionModel().getSelectedItem().toString().equals(""))
            {
                button.setDisable(false);
            }
        }
        catch(Exception e)
        {
            //Ignoring this exception caused my unselected combobox
            //System.out.println("Excepton");
        }
        
    }
    @FXML
    private void handleButtonAction(ActionEvent event) throws InterruptedException 
    {
        if(!userName.getText().equals("") || !password.getText().equals("") || !hostName.getValue().isEmpty())
        {
            
            System.out.println(hostName.getValue().toString());
            String hostName1=hostName.getValue().toString();
            ce =new ConnectionEstablisher(hostName1,userName.getText().toString(),password.getText().toString());
            authenticProgress.setVisible(true);
            userName.setDisable(true);
            password.setDisable(true);
            hostName.setDisable(true);
            this.start();
            button.setDisable(true);
            new Thread()
            {
               
                public void run()
                {
                    for(double i =0.00 ; i<= 0.99;i+=0.01)
                    {
                        try 
                        {
                            Thread.sleep(timer);
                            authenticProgress.setProgress(i);
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
                    while(true)
                    {
                        if(session != null)
                        {
                            setProgress();
                            break;
                        }
                    }
                    
                }
            }.start();
        }
        else
        {
            //missing mandatory parameter
            System.out.println("missing mandatory parameter");
        }
    }
    @FXML
    private void connectionTerminate() throws URISyntaxException, IOException
    {
        
        if(session != null)
        {
            System.out.println("Application will now Terminate");
            System.exit(0);
        }
        restartApplication();
    }
    public void restartApplication() throws URISyntaxException, IOException
    {
      final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      final File currentJar = new File(Sync.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      if(!currentJar.getName().endsWith(".jar"))
        return;
      final ArrayList<String> command = new ArrayList<String>();
      command.add(javaBin);
      command.add("-jar");
      command.add(currentJar.getPath());

      final ProcessBuilder builder = new ProcessBuilder(command);
      builder.start();
      System.out.println("Application will now Restart");
      System.exit(0);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        try {
            // TODO
            new TempFileHandler().createFile();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        populateHostName();
        checkFields();
        DownloadFile df = new DownloadFile();
        //df.filter();
        //System.out.println(df.getSelectedFileSize("-rw-r--r-- 1 portware oinstall 10429K May 25 00:00 /home/portware/Portware/data/logs/server/BS20180524000001.log"));
    }    
    public void run()
    {
        while(true)
        {
            try 
            {
                if( ce.ConnectionHandler() == true )
                {
                    session = ce.getSessionID();
                    System.out.println("Setting session as an global variable: "+session);
                    this.setSessionID(session);
                    
                    timer = 1;
                    break;
                }
                else if(ce.invalid == 1)
                {
                    System.out.println("Invalid Credentials");
                    invalid = 1;
                    break;
                }
            }
            catch (InterruptedException ex)
            {
                java.util.logging.Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Waiting for threads");
        setProgress();
    }
    public void setSessionID(Session session)
    {
        
        this.session = session;
    }
    private void waitTillConnection()
    {
        
    }
    private void setProgress() 
    { 
        authenticProgress.setProgress(1);
        button2.setVisible(Boolean.TRUE);
        button1.setVisible(false);
    }
    @FXML
    public void selectLogFile(ActionEvent ae) throws IOException
    {
        System.out.println("User initiated sequence for Selecting a log file");
        LocationFeederController lfc = new LocationFeederController();
        System.out.println("Session taht will be passed from main controller is: "+session);
        lfc.session = session;
        Stage stage1 = new Stage();
        lfc.start(stage1);
        //stage1.showAndWait();
        stage1.show();
        Node source = (Node) ae.getSource();
        Window theStage = source.getScene().getWindow();
        theStage.hide();
    }
    @FXML
    void populateHostName()  
    {
        try
        {
            System.out.println("Called");
            String fileName = new TempFileHandler().fileName;
            List<String> myList = Files.lines(Paths.get(fileName)).collect(Collectors.toList());
            System.out.println(myList);
            hostName.setItems(FXCollections.observableArrayList(myList));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}