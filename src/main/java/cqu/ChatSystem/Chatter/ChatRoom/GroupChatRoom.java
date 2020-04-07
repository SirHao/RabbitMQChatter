package cqu.ChatSystem.Chatter.ChatRoom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class GroupChatRoom {
    private Connection connection;
    private Channel SendChannel;
    private Channel ReceiveChannel;
    private String ReceiveQueueName;
    private String GroupName;
    private String UserName;
    public  int isLeave;

    public GroupChatRoom(String groupName,String userName) {
        isLeave=0;
        this.UserName=userName;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            SendChannel = connection.createChannel();
            ReceiveChannel = connection.createChannel();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        GroupName=groupName;
        init();
    }
    private void init(){
        try {
            //sender init
            SendChannel.exchangeDeclare(GroupName, "fanout");
            //receiver init
            ReceiveChannel.exchangeDeclare(GroupName, "fanout");
            ReceiveQueueName = ReceiveChannel.queueDeclare().getQueue();
            ReceiveChannel.queueBind(ReceiveQueueName, GroupName, "");

            System.out.println("加入聊天室："+GroupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(Scanner sc){

        if(isLeave==0) {
            Thread receiver = new Thread(new Runnable() {
                public void run() {
                    try{
                        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                            String message = new String(delivery.getBody(), "UTF-8");
                            if(!isMyself(message)){
                                if(isLeave%2==1)
                                    System.out.println("=======================================你有一条其他聊天室的消息："+message);
                                else
                                    System.out.println(message);
                            }
                        };
                        ReceiveChannel.basicConsume(ReceiveQueueName, true, deliverCallback, consumerTag -> { });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        }
        String joinmsg="["+this.GroupName+"-"+this.UserName+"] join the group!";
        try {
            this.SendChannel.basicPublish(this.GroupName, "", null, joinmsg.getBytes("UTF-8"));
            //System.out.println(" [x] Sent '" + message + "'");
        }catch (Exception e){
            e.printStackTrace();
        }

        while(sc.hasNextLine()){
            String message=sc.nextLine();
            if(message.equals("quitChat")) {
                isLeave++;
            }
            message="["+this.GroupName+"-"+this.UserName+"] "+message;
            try {
                this.SendChannel.basicPublish(this.GroupName, "", null, message.getBytes("UTF-8"));
                //System.out.println(" [x] Sent '" + message + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isLeave%2!=0) break;
        }
    }
    private boolean isMyself(String msg){
        int i=0;
        String tmp="["+this.GroupName+"-"+this.UserName+"]";
        while(msg.charAt(i)!=']'){
            if(msg.charAt(i)!=tmp.charAt(i)) return false;
            i++;
        }
        return true;
    }

}
