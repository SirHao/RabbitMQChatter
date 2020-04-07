package cqu.ChatSystem.Protocal.PacketImp.Request;


import cqu.ChatSystem.Protocal.Packet;

import static cqu.ChatSystem.Protocal.Command.MESSAGE_REQUEST;

public class MessageRequestPacket extends Packet {
    private String message;
    private String userName;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public byte getCommend() {
        return MESSAGE_REQUEST;
    }

    public MessageRequestPacket(String message, String userName) {
        this.message = message;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
