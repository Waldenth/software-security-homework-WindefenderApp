package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;

import client.GlobalVarClientRepo;

public class Receiver {
	public static void receive(int num) {
		int curIndex=0;
        while(curIndex<=GlobalVarServerRepo.MaxCaptureIndex) {
        	FileOutputStream fileWriter=null;
            Socket receiver=null;
            InputStream inReader=null;
            try {
            	receiver=new Socket(GlobalVarServerRepo.clientIP,GlobalVarServerRepo.port);
            	inReader=receiver.getInputStream();
            	fileWriter=new FileOutputStream(
            			GlobalVarServerRepo.repoFolderStr+String.valueOf(num)+GlobalVarServerRepo.screenShotPrefixStr
            			+String.valueOf(curIndex)+"."+GlobalVarServerRepo.screenShotTypeStr);
            	byte[]buffer=inReader.readAllBytes();
            	fileWriter.write(buffer);
            }catch (Exception e) {
				// TODO: handle exception
            	e.printStackTrace();
            }
            try {
            	inReader.close();
            	receiver.close();
            	fileWriter.close();
            }catch (Exception e) {
				// TODO: handle exception
            	e.printStackTrace();
            }
            
            curIndex++;
        }
	}
	public static void check() {
		File repoFolder0=new File(GlobalVarServerRepo.repoFolderStr+String.valueOf(0));
		File repoFolder1=new File(GlobalVarServerRepo.repoFolderStr+String.valueOf(1));
		 if(repoFolder0.exists()) {
	     	   if(repoFolder0.isDirectory()) {
	     		   System.out.println("[Log]: repo0 is normal");
	     	   }else {
	     		   System.out.println("[Log]: repo0 is occupied by a file");
	     		   repoFolder0.delete();
	     		   System.out.println("[Log]: delete file and try create folder");
	     		   repoFolder0.mkdir();
	     	   }
	     	   
	     }else {
	    	 System.out.println("[Log]: repo is not exist, try to create folder");
	    	 repoFolder0.mkdir();
	     }
		 
        if(repoFolder1.exists()) {
     	   if(repoFolder1.isDirectory()) {
     		   System.out.println("[Log]: repo1 is normal");
     	   }else {
     		   System.out.println("[Log]: repo1 is occupied by a file");
     		   repoFolder1.delete();
     		   System.out.println("[Log]: delete file and try create folder");
     		   repoFolder1.mkdir();
     	   }
     	   
        }else {
        	System.out.println("[Log]: repo is not exist, try to create folder");
     	   	repoFolder1.mkdir();
        }
	}
	
	
	public static void main(String[]args) {
		check();
		receive(0);
	}

}
