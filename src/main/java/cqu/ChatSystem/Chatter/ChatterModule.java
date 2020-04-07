package cqu.ChatSystem.Chatter;

import cqu.ChatSystem.Chatter.ChatRoom.FriendChatRoom;
import cqu.ChatSystem.Chatter.ChatRoom.GroupChatRoom;
import cqu.ChatSystem.LoginRPCSever.RPCLoginClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ChatterModule {
    private HashMap<String,FriendChatRoom> currentFriendChats;
    private HashMap<String, GroupChatRoom> currentGroupChats;
    private String username;

    public ChatterModule() {
        currentFriendChats=new HashMap<>();
        currentGroupChats=new HashMap<>();
    }

    public void welcomeModule(){
        System.out.println("=======================================");
        System.out.println("|          Rabbit MQ 聊天系统           |");
        System.out.println("=======================================");
    }
    public void menuModule(){
        System.out.println("=======================================");
        System.out.println("|   'S' :单聊          ‘G’：群聊        |");
        System.out.println("|  'quitChat'退出聊天室;'exit'退出系统   |");
        System.out.println("=======================================");
    }
    public void StartChat(Scanner sc){
        while(true){
            menuModule();
            String choice=sc.nextLine();
            if(choice.equals("exit")) break;
            else if(choice.equals("S")){
                System.out.print("请输入对方昵称：");
                String friend=sc.nextLine();
                FriendChatRoom friendChatRoom;
                if(currentFriendChats.containsKey(friend)){
                    friendChatRoom=currentFriendChats.get(friend);
                    friendChatRoom.isLeave++;
                }else{
                    friendChatRoom=new FriendChatRoom(username,friend);
                    currentFriendChats.put(friend,friendChatRoom);
                }
                friendChatRoom.start(sc);
            }else if(choice.equals("G")){
                System.out.print("请输入群聊名称：");
                String groupName=sc.nextLine();
                GroupChatRoom groupChatRoom;
                if(currentGroupChats.containsKey(groupName)){
                    groupChatRoom=currentGroupChats.get(groupName);
                }else{
                    groupChatRoom=new GroupChatRoom(groupName,username);
                }
                groupChatRoom.start(sc);
            }else{
                inValidCommand();
            }
        }
    }
    public boolean loginModule(Scanner sc,int retry){
        if(retry==0) return false;
        System.out.println("=================请登录=================");
        System.out.print("用户名：");
        username=sc.nextLine();
        System.out.print(" 密码 ：");
        String pwd=sc.nextLine();
        boolean isSuccess=false;
        try {
            RPCLoginClient rpcLoginClient=new RPCLoginClient();
            boolean isValid=rpcLoginClient.login(username,pwd);
            if(isValid) System.out.println("登陆成功");
            else System.out.println("登录失败！请重试");
            isSuccess=isValid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isSuccess) return true;
        else return loginModule(sc,retry-1);
    }
    public void inValidCommand(){
        System.out.println("无法识别，重试！");
    }
}
