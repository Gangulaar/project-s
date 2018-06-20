/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agangula
 */
public class DownloadFile 
{
    JSch jsch;
    Session session;
    Channel channel;
    ChannelSftp sftpChannel;
    static double numberOfByteTransferred=0;
    static Boolean alive = true;
    DownloadFile(Session session)
    {
        this.session = session;
    }

    DownloadFile() {
        
    }
    public void openChannel() throws JSchException
    {
       channel = session.openChannel("sftp");
       channel.connect();
       
        sftpChannel = (ChannelSftp) channel;
    }
    public int getSelectedFileSize(String fileName)
    {
        
        String cdDir = fileName.substring(0,fileName.lastIndexOf("/")+1);
        InputStream in;
        
        String output = null;
        try 
        {
            //ChannelSftp sftpChannel1 = sftpChannel ;
            sftpChannel.cd(cdDir);
            Channel channel1=session.openChannel("exec");
            String get_count_cmd="ls -l --block-size=k "+fileName;
            System.out.println(get_count_cmd);
            channel1.setInputStream(null);
            ((ChannelExec)channel1).setErrStream(System.err);
            in =  channel1.getInputStream();
            channel1.connect();
            while(in.available() > 0)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                output = br.readLine();
            }
            System.out.println("Output of the cmd: "+output);
            sftpChannel.disconnect();
            String filtered = filter(output);
            int size = 0;
            if(filtered.endsWith("K"))
            {
                //then it is a number
                return Integer.parseInt((String) filtered.subSequence(0,filtered.length() - 1));
                
            }
            else
            {
                //error 
                return -1;
            }
            
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(DownloadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    public void downloadCompleteFile(String fileName,String localDir)
    {
        alive = true;
        byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        System.out.println("Source: "+fileName+"\nDest: "+localDir);
        try
        {
            String cdDir = fileName.substring(0,fileName.lastIndexOf("/")+1);
            //System.out.println("CdDir: "+cdDir);
            sftpChannel.cd(cdDir);
            
            File file = new File(fileName);
            bis = new BufferedInputStream(sftpChannel.get(file.getName()));
            
            File newFile = new File(localDir + "/" + file.getName());
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            int readCount;
            while ((readCount = bis.read(buffer)) > 0) 
            {
                bos.write(buffer, 0, readCount);
                ++DownloadFile.numberOfByteTransferred;
            }
            bis.close();
            bos.close();
            System.out.println("File downloaded successfully - "+ file.getAbsolutePath());
            sftpChannel.disconnect();
            alive =false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String filter(String output) 
    {
        String temp[] = output.split(" ");
        if(temp[4].endsWith("K"))
        {
            return temp[4];
        }
        return "Not found: Retrival syntax error";
    }
    
}
