package cqu.ChatSystem.LoginRPCSever;

import com.rabbitmq.client.*;
import cqu.ChatSystem.Protocal.PacketImp.Request.LoginRequestPacket;
import cqu.ChatSystem.Protocal.PacketImp.Response.LoginResponsePacket;
import cqu.ChatSystem.Utils.Serializer.Serializer;

public class RPCLoginserver {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static LoginResponsePacket isValid(LoginRequestPacket loginRequestPacket){
        LoginResponsePacket loginResponsePacket=new LoginResponsePacket();
        loginResponsePacket.setUserName(loginRequestPacket.getUsername());
        loginResponsePacket.setSuccess(true);
        return loginResponsePacket;

    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] RPC login System Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    LoginRequestPacket loginRequestPacket=
                            (LoginRequestPacket) Serializer.DEFAULT.deserialize(LoginRequestPacket.class,message);
                    System.out.print(" [.] login request from 【" + loginRequestPacket.getUsername() + "】");
                    LoginResponsePacket loginResponsePacket=isValid(loginRequestPacket);
                    response =Serializer.DEFAULT.serialize(loginResponsePacket);
                    if(loginResponsePacket.isSuccess())
                        System.out.println("----->login sucess");
                    else
                        System.out.println("----->login fail");
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
