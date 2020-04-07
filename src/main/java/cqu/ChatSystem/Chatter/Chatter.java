package cqu.ChatSystem.Chatter;

import java.util.Scanner;

public class Chatter {
    public static void main(String[] argv) throws Exception {
        Scanner scanner=new Scanner(System.in);
        ChatterModule chatter=new ChatterModule();
        chatter.welcomeModule();
        boolean loginStatus=chatter.loginModule(scanner,3);
        if(loginStatus){
            chatter.StartChat(scanner);
        }
    }
}
