/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.*;
import java.util.logging.Level;

/**
 *
 * @author agangula
 */
public class ConnectionEstablisher extends Thread
{
    private Session session;
    private String host;
    private String username;
    private String password;
    private FTPConnector ftp;
    ConnectingThread ct;
    public int invalid=0;
    ConnectionEstablisher(String host, String username, String password) 
    {
        this.host = host;
        this.username = username;
        this.password = password;
        ct = new ConnectingThread(host,username,password);
        this.start();
    }
    public Boolean ConnectionHandler() throws InterruptedException
    {
        this.join();
        System.out.println("Exiting ConnectionHandlerClass with session ID: "+session);
        if (session == null)
            return false;     
        else
            return true;
        
    }
    public void run()
    {
        
        try 
        {
            session = ct.intializeLoginSequence();
            if(ct.invalid == 1)
            {
                this.invalid = 1;
            }
            else
                System.out.println("Reached ConnectionHandlerClass with session ID: "+session);
        } 
        catch (InterruptedException ex) 
        {
            java.util.logging.Logger.getLogger(ConnectionEstablisher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Session getSessionID()
    {
        return session;
    }
    public void disconnect()
    {
        ct.disconnect();
    }
}
