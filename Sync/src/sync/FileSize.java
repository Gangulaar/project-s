/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author agangula
 */
public class FileSize
{
    public double getFileSize(String fileName)
    {
        double bytes;
        double kb;
        //System.out.println(fileName);
        File file =new File(fileName);
        //System.out.println(file.getAbsolutePath());
        if(file.exists())
        {
            bytes = file.length();
            kb = (1/1024)*bytes;
            System.out.println(bytes);
            return kb;
        }
        else
        {
            System.out.println("File does not exists!");
            return 0;
        }
    }
    public static void main(String[] args)
    {
        new FileSize().getFileSize("C:\\Users\\agangula\\Desktop\\BS20180530000001.log");
    }

    
}
