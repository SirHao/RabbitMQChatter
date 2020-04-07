package cqu.ChatSystem.Chatter.ChatRoom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import cqu.ChatSystem.Chatter.ChatterModule;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class FriendChatRoom {
    private Connection connection;
    private Channel SendChannel;
    private Channel ReceiveChannel;
    private String SendQueueName;
    private String ReceiveQueueName;
    private String Friend;
    public int  isLeave;

    public FriendChatRoom(String sender, String receiver) {
        isLeave=0;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            SendChannel = connection.createChannel();
            ReceiveChannel=connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.SendQueueName=receiver+"->"+sender;
        this.ReceiveQueueName=sender+"->"+receiver;
        this.Friend=receiver;
        init();
    }
    private void init() {
        try {
            this.SendChannel.queueDeclare(SendQueueName, false, false, false, null);
            this.ReceiveChannel.queueDeclare(ReceiveQueueName,false,false,false,null);
            //System.out.println("发送队列："+SendQueueName+"   接收队列："+ReceiveQueueName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(Scanner sc){
        if(isLeave==0){
            Thread receiver = new Thread(new Runnable() {
                public void run() {
                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), "UTF-8");
                        if(isLeave%2==1) System.out.println("=======================================你有一条其他聊天室的消息："+SendQueueName+message);
                        else System.out.println(" 【"+SendQueueName+"】"+ message);
                    };
                    try {
                        ReceiveChannel.basicConsume(ReceiveQueueName, true, deliverCallback, consumerTag -> { });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            receiver.start();
        }
        while(sc.hasNextLine()){
            String message=sc.nextLine();
            if(message.equals("quitChat")) {
                isLeave++;
                message="[系统提醒] 对方退出";
            }
            try {
                SendChannel.basicPublish("", SendQueueName, null, message.getBytes());
                //if(!isLeave) System.out.println("                          me:"+message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isLeave%2==1) {
                break;
            }
        }

    }
}
