/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.Session;

/**
 *
 * @author agangula
 */
public class ConnectingThread extends Thread
{
    private Session session;
    private String host;
    private String username;
    private String password;
    private FTPConnector ftp;
    public int invalid =0;
    ConnectingThread(String host, String username, String password)
    {
        this.host = host;
        this.username = username;
        this.password = password;
    }
    public Session intializeLoginSequence() throws InterruptedException
    {
        this.start();
        this.join();
        return session;
    }
    public void run()
    {
        ftp = new FTPConnector(host,22,username,password);
        session =ftp.connect();
        if(session == null)
        {
            //session not established
            System.out.println("Couldn't create session");
             invalid = 1;
        }
        else
        {
            System.out.println("Session established");
            this.setSession(session);
            this.notifyCompletion();
        }
    }
    public void notifyCompletion()
    {
        System.out.println("Thread completed: Session variable = "+session);
    }
    public void setSession(Session session)
    {
        this.session = session;
    }
    public Session getSession()
    {
        return session;
    }
    public boolean sessionState() // return false if null else true
    {
        if (session == null)
            return false;
        else
            return true;
    }
    public void disconnect()
    {
        ftp.disconnect();
    }
}
