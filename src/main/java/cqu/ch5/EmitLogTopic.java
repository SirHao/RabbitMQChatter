package cqu.ch5;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            System.out.print("[format xx.xx.xx]>>");
            Scanner sc=new Scanner(System.in);
            while(sc.hasNext()){

                String routingKey = sc.nextLine();
                String Rmessage = routingKey+" message";

                channel.basicPublish(EXCHANGE_NAME, routingKey, null, Rmessage.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + routingKey + "':'" + Rmessage + "'");
                System.out.print("[format xx.xx.xx]>>");
            }


        }
    }
    //..
}
