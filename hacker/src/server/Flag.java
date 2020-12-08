package server;

public class Flag {
    public static volatile boolean isWorking=false;
    public static void setFlag(boolean flag){
        isWorking=flag;
    }
}
