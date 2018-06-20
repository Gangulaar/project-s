package sync;
import java.io.*;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
public class Syncer
{
    public static boolean new_file=true;
    public static int present_set_lines=0;
    public static String host;
    public static String user;
    public static String password;
    public static String path;
    static PrintWriter writer=null;
    static InputStream in =null;
    static Channel channel = null;
    static Session session = null;
    Syncer(Session session)
    {
        Syncer.session = session;
    }
    public void setPath(String path)
    {
        Syncer.path=path;
    }
    public void closeConnection()
    {
        session.disconnect();
    }
    public void establishChannel(String Download)
    {
        /*This function will make a call to getLines and will sleep if there is nothing to copy else it is call settingCommand that will write the file*/
        try
        {
            int lines=0;
            while(true)
            {
                lines=getLines(path,Download);
                if(lines == 0)
                {
                    //nothing to copy
                    System.out.println("Nothing to copy will try in 5 secs");
                    Thread.sleep(5000);
                }
                else
	        {
                    String command1="tail -"+lines+" "+path;
                    System.out.println("Setting command: "+command1);    
                    settingCommand(command1,Download);
                    Thread.sleep(1000);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public static int getLines(String path, String Download)
    { 
        /*This function will take compare the present copied line and file from server and will return diff of them*/
        int new_set_lines= getFileCount(path);
        if(new_set_lines - Syncer.present_set_lines > 0)
        {
            System.out.println("Something is changed... checking now...");
            int temp = Syncer.present_set_lines;
            Syncer.present_set_lines = new_set_lines;
            return new_set_lines - temp;
        }
        return 0;
    }
    public static int getFileCount(String path)
    {
        /*This functill take source path and session as inputs and will return number of lines that are to be synced*/
        int lines=0;
        System.out.println("Path of the source file is: "+path);
        String raw_output_lines = new String();
        try
        {
            Channel line_channel=Syncer.session.openChannel("exec");
            String get_count_cmd="wc -l < "+path;
            System.out.println(get_count_cmd);
            ((ChannelExec)line_channel).setCommand(get_count_cmd);
            line_channel.setInputStream(null);
            ((ChannelExec)line_channel).setErrStream(System.err);   
            in=line_channel.getInputStream();
            line_channel.connect();            
            while(true)
            {
                while(in.available()>0)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    raw_output_lines = br.readLine();
                    System.out.println("Raw_output: "+raw_output_lines);
                }
                if(line_channel.isClosed())
                    break;                      
            }
            in.close();
            line_channel.disconnect();
            System.out.println("Out of loop");
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    System.out.println("Lines (in string): "+raw_output_lines);
    String buffer = new String(raw_output_lines);
    System.out.println("Out of loop");
    lines = Integer.parseInt(buffer);
    System.out.println("Out of loop");
    System.out.println("lines FOUND: "+lines);
    return lines;
    }
    public static void settingCommand(String command1,String Download)
    {
        /*This function will take the Command and destination path as inputs 
        will sync the file continously until channel is closed*/
        try
        {
            File temp_file = new File(path);
            Download = Download + "/" + temp_file.getName();
            channel=session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command1);
	    channel.setInputStream(null);
	    ((ChannelExec)channel).setErrStream(System.err);   
	    in=channel.getInputStream();
	    channel.connect();
            writer = new PrintWriter(new FileOutputStream(new File(Download),true)); //destination file
            int i;
            char c;
            while(true)
            {
            while(in.available()>0)
            {
                System.out.println("avail:   "+in.available());
                while((i = in.read())!=-1) 
                {
                    c = (char)i;   
                    writer.print(c);
                }
                writer.close();
                }
                if(channel.isClosed())
                    break;
            }
            in.close();
            channel.disconnect();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public void disconnect_Connection()
    {
        try
        {
            writer.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Writer Already closed");
        }
        try
        {
            in.close();
            
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Stream Already closed");
        }
        try
        {
            channel.disconnect();
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Channel Already closed");
        }
    }
}