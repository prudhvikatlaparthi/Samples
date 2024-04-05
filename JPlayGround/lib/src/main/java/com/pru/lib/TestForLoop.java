package com.pru.lib;

import java.util.Scanner;

public class TestForLoop {
    public static void main(String arg[]) {
        P p = new C();
        p.mym();
    }
}

class P {
    public void mym(){
        System.out.println("P is c");
    }
}
class C extends P {

}

class V {
    interface My{
        int nu =10;
        static int getNum(){
            return nu;
        }
    }

}



