/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.*;

/**
 *
 * @author agangula
 */
public class FTPConnector 
{
    private int invalid = 0;
    private final String host;
    private final Integer port;
    private final String user;
    private final String password;
    private JSch jsch;
    private Session session;
    private Channel channel;
    private ChannelSftp sftpChannel;
    public FTPConnector(String host, Integer port, String user, String password) 
    {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }
    public Session connect() 
    {
	System.out.println("connecting..."+host);
	try 
        {
            jsch = new JSch();
            session = jsch.getSession(user, host,port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            //This block is ommited for a faster login process
            
            /*channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel; */
            
        } 
        catch (JSchException e) 
        {
            session = null;
            invalid =1;
            e.printStackTrace();
	}
        
        finally
        {
            return session;
        }
    }
    public void disconnect() 
    {
        
        if(sftpChannel != null)
            sftpChannel.disconnect();
        if(channel != null)
            channel.disconnect();
        if(session != null)
            session.disconnect();
        System.out.println("Session Terminated successfully");
    }
}

