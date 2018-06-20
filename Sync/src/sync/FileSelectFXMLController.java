/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import static java.awt.SystemColor.window;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Aki
 */
public class FileSelectFXMLController extends Application implements Initializable,Runnable {

    @FXML
    private AnchorPane fileAchorPane;
    @FXML
    private Button BSlog;
    @FXML
    private ComboBox recentBox;
    @FXML
    private TextField customDir;
    @FXML
    TableColumn fileNameCol,sizeCol,dateCol;
    @FXML
    private TableView<DirData> dataTable = new TableView<DirData>();
    @FXML
    private TableColumn tableFileName,tableSize;
    @FXML
    private Label recentLog;
    @FXML
    public ProgressBar pb;
    @FXML
    private CheckBox recentCheck;
    private Vector copyList;
    public static String choosenFile;
    //private final Session session;
    static public Session session;
    static public String path_selected;
    /*FileSelectFXMLController(Session session) {
        this.session=session;
    }*/

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void BSLogSearch(ActionEvent event) throws JSchException , SftpException 
    {
        
        clearRecentCache();
        SearchDir("/home/portware/Portware/data/logs/server/");
        
    }
    @FXML
    private void TMLogSearch(ActionEvent event) throws JSchException , SftpException 
    {
        clearRecentCache();
        SearchDir("/home/portware/Portware/data/logs/trademonitor/");
    }
    @FXML
    private void AppLogSearch(ActionEvent event) throws JSchException , SftpException 
    {
        clearRecentCache();
        SearchDir("/home/portware/Portware/data/logs/appserver/");
    }
    @FXML
    private void BuySideLogSearch(ActionEvent event) throws JSchException , SftpException 
    {
        clearRecentCache();
        SearchDir("/home/appia01/Javelin/Appia-7.7.0.1/logs/buy/");
    }
    @FXML
    private void SellSideLogSearch(ActionEvent event) throws JSchException , SftpException 
    {
        clearRecentCache();
        SearchDir("/home/appia01/Javelin/Appia-7.7.0.1/logs/sell/");
    }
    @FXML
    private void CustomSearch(ActionEvent event) throws JSchException, SftpException 
    {
        clearRecentCache();
        String path = customDir.getText();
        SearchDir(path);
    }
    public void setSession(Session session1)
    {
        FileSelectFXMLController.session=session1;
        System.out.println("Session passed"+session);
    }
    
     void SearchDir(String hostDir) throws JSchException, SftpException
    {
        path_selected = hostDir;
        try
        {
            System.out.println(session);
            Session temp_session=session;
            Channel channel=temp_session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp)channel;
            channelSftp.cd(hostDir);
            Vector filelist = channelSftp.ls(hostDir);
            copyList = filelist;
            ObservableList<DirData> data1 = FXCollections.observableArrayList();
            HashSet<String> extension = new HashSet<String>();
            for(int i=2; i<filelist.size();i++)
            {
                String temp1 = filelist.get(i).toString();
                temp1 = temp1.trim().replaceAll(" +", " ");
                String buffer[] =temp1.split(" ");
                int p=buffer[8].lastIndexOf(".");
                
                String e=buffer[8].substring(p+1);
                if( p==-1 || !e.matches("\\w+") )
                {
                    /* file has no extension */
                    
                }
                else
                {
                    extension.add(e);
                    data1.add( new DirData(buffer[8],buffer[4],buffer[6]+" "+buffer[5]+" "+buffer[7]));
                }
                
            }
            fileNameCol.setCellValueFactory(
                new PropertyValueFactory<DirData, String>("fileName"));
            sizeCol.setCellValueFactory(
                new PropertyValueFactory<DirData, String>("fileSize"));
            dateCol.setCellValueFactory(
                new PropertyValueFactory<DirData, String>("fileDate"));
            dataTable.setItems(data1);
            /*Iterator<String> itr=extension.iterator();  
            while(itr.hasNext())
            {  
                System.out.println(itr.next());  
            }*/
            recentCheck.setVisible(true);
            if(!hostDir.contains("Javelin"))
            {
                extension.add("GC logs");
            }
            recentBox.getItems().addAll(extension);
            System.out.println("Called to this end");
        }
        catch(Exception e)
        {
            System.out.println("Can't connect to the given path");
            System.out.println(e);
        }
    }
     public void recentCheckBox()
     {
         if(recentCheck.isSelected()) 
         {
             recentBox.setDisable(false);
             recentBox.setVisible(true);
             recentLog.setVisible(true);
             System.out.println("Erasing Data set by table");
             dataTable.getSelectionModel().select(null);
             dataTable.setDisable(true);
             choosenFile = "";
         } 
         else 
         {
             recentBox.setDisable(true);
             recentBox.setVisible(false);
             recentLog.setVisible(false);
             System.out.println("Erasing Data set by checkbox");
             choosenFile = "";
             dataTable.setDisable(false);
         }
     }
    public void itemIndexChanged()
    {
        String temp_ext=(String) recentBox.getSelectionModel().getSelectedItem();
        System.out.println(temp_ext);
        String latest = (searchLatestExtension(temp_ext));
        recentLog.setText(latest);
        choosenFile = sendAbsolutePath(latest);
        System.out.println("Setting data by combo box");
    }
    public void getSelectedLogName()
    {
        String name_file="";
        if(dataTable.getSelectionModel().getSelectedItem() != null)
        {
            DirData name=dataTable.getSelectionModel().getSelectedItem();
            name_file = name.getFileName();
            choosenFile = sendAbsolutePath(name_file);
            System.out.println("Setting data by table");
        }
        
        //System.out.println(name_file);
        
        FileSelectFXMLController.stage1.close();
    }
    public static Stage stage1;
    public static Scene scene1;
    public void start(Stage stage) throws IOException 
    {
        FileSelectFXMLController.stage1=stage;
        Parent root = FXMLLoader.load(getClass().getResource("FileSelectFXML.fxml"));
        Scene scene =  new Scene(root);
        FileSelectFXMLController.scene1=scene;
        stage.setScene(scene);
        //stage.show();
    }

    private String sendAbsolutePath(String file_name) 
    {
        System.out.println("pathed"+path_selected+file_name);
        return(path_selected+file_name);
    }

    private String searchLatestExtension(String temp_ext) 
    { 
        String lastest_file = new String();
        String latest = new String ();
        try
        {
            for(int i=2; i<copyList.size();i++)
            {
                if(temp_ext.equals("GC logs")) 
                {
                    temp_ext = "GC"; // modifying search for GC logs
                }
                if(copyList.get(i).toString().contains(temp_ext))
                {

                    System.out.println(copyList.get(i).toString());
                    String temp1 = copyList.get(i).toString();
                    temp1 = temp1.trim().replaceAll(" +", " ");
                    String buffer[] =temp1.split(" ");
                    int p=buffer[8].lastIndexOf(".");
                    String e=buffer[8].substring(p+1);
                    if( p==-1 || !e.matches("\\w+") )
                    {   /* file has no extension */   }
                    else
                    {
                        /*buffer[8] //has name
                        buffer[6] // date
                        buffer[5] // month
                        buffer[7]) // time
                        */
                        String month = Integer.toString(getMonthtoNumber(buffer[5]));
                        String mins;
                        if(buffer[7].contains(":"))
                        {
                            mins = buffer[7].replace(":", "");
                        }
                        else
                        {
                            mins = buffer[7];
                        }
                        String time = new String(buffer[6]+month+mins);
                        if(latest.equals(""))
                        {
                            latest = time;
                            lastest_file = buffer[8];
                        }
                        else
                        {
                            if(Integer.parseInt(latest) < Integer.parseInt(time))
                            {
                                latest = time;
                                lastest_file = buffer[8];
                            }
                        }
                    }        
                }    
            }
        }
        catch(Exception e)
        {
            System.out.println("Error is popluating combo box");
        }
        return lastest_file;
    }

    private int getMonthtoNumber(String string) 
    {
        switch(string)
        {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
        }
        return 0;
    }

    private void clearRecentCache() 
    {
        recentBox.getItems().clear();
    }

    @Override
    public void run() {
        pb.setVisible(true);
    }
    public static class DirData
    {
        private final SimpleStringProperty fileName;
        private final SimpleStringProperty fileSize;
        private final SimpleStringProperty fileDate;
        private DirData(String fileName, String fileSize, String fileDate)
        {
            this.fileName = new SimpleStringProperty(fileName);
            this.fileSize = new SimpleStringProperty(fileSize);
            this.fileDate = new SimpleStringProperty(fileDate);
        }
        public String getFileName()
        {
            return fileName.get();
        }
        public String getFileSize()
        {
            return fileSize.get();
        }
        public String getFileDate()
        {
            return fileDate.get();
        }
        public void setFileName(String name)
        {
            fileName.set(name);
        }
        public void setFileSize(String size)
        {
            fileSize.set(size);
        }
        public void setFileDate(String datetime)
        {
            fileDate.set(datetime);
        }
    }
}
