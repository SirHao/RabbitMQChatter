package cqu.ChatSystem.LoginRPCSever;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import cqu.ChatSystem.Protocal.PacketImp.Request.LoginRequestPacket;
import cqu.ChatSystem.Protocal.PacketImp.Response.LoginResponsePacket;
import cqu.ChatSystem.Utils.Serializer.Serializer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCLoginClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCLoginClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public boolean login(String userName,String password) {
        boolean isSucess=false;
        try  {
            LoginRequestPacket loginRequestPacket=new LoginRequestPacket();
            loginRequestPacket.setUsername(userName);
            loginRequestPacket.setPassword(password);
            String i_str = Serializer.DEFAULT.serialize(loginRequestPacket);
            System.out.print(" [RPC MQ] Requesting login(" + loginRequestPacket.getUsername() + ")");
            isSucess = RPCcall(i_str);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            return isSucess;
        }
    }

    public boolean RPCcall(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = response.take();
        LoginResponsePacket loginResponsePacket=Serializer.DEFAULT.deserialize(LoginResponsePacket.class,result);
        channel.basicCancel(ctag);
        return loginResponsePacket.isSuccess();
    }

    public void close() throws IOException {
        connection.close();
    }
}
