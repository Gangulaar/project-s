/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sync;

/**
 *
 * @author agangula
 */
public class Progress extends Thread
{
    static String fileName;
    static double DestFileSize=0;
    Progress(String fileName, double sourceFileSize)
    {
        Progress.fileName = fileName;
        this.start();
    }
    public void run()
    {
        FileSize fs = new FileSize();
        DestFileSize = fs.getFileSize(fileName);
    }
}
