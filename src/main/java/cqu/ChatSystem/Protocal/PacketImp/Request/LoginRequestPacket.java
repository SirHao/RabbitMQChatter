package cqu.ChatSystem.Protocal.PacketImp.Request;

import cqu.ChatSystem.Protocal.Packet;

import static cqu.ChatSystem.Protocal.Command.LOGIN_REQUEST;

public class LoginRequestPacket extends Packet {

    private String username;
    private String password;

    @Override
    public byte getCommend(){
        return LOGIN_REQUEST;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
