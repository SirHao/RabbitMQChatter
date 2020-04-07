package cqu.ChatSystem.Protocal;

public abstract class Packet {
    private byte version=1;

    public byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }

    public abstract byte getCommend();
}
