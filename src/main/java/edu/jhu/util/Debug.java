package edu.jhu.util;


public class Debug {

    private Debug() {}
    
    public static void printStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {            
            e.printStackTrace();
        }
    }
    
}
