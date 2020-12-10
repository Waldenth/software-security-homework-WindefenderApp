package client;

public class Flag {
    public static volatile boolean isWorking=false;
    public static volatile int MaxCaptureIndex=200;
    public static void setIsWorking(boolean set){
        isWorking=set;
    }
}
