package client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Screen {
    public static void capture(){
        int index=0;
        File f=null;
        while(index<=Flag.MaxCaptureIndex){
            try{
                BufferedImage screenShot = (new Robot()).createScreenCapture(new Rectangle(0, 0, 1500, 1000));
                f=new File("captureCache/screenShot"+String.valueOf(index)+".jpg");
                ImageIO.write(screenShot,"jpg",f);
                if(!Flag.isWorking){
                    System.out.println("capture exit!");
                    break;
                }
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            index++;
        }
        System.out.println("capture finish");
    }

}
