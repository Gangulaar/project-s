/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author agangula
 */
public class TempFileHandler 
{
    String fileName = "C:\\sync\\cache.list";
    String DirName = "C:\\sync";
    public void createFile() throws IOException
    {
       File file = new File (DirName);
       if(!file.exists())
       {
           if(file.mkdir())
           {
               System.out.println("Dir created");
           }
       }
       
       File file1 = new File(fileName);
       if(!file1.exists())
            if(file1.createNewFile())
                System.out.println("File created");
    }
}
