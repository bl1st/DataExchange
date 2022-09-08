package com.example.dataexchange;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
public class Message {
    public String IP;  //493 balanin
    public String Port;
    public String Name;
    public String Text;
    public Date DateTime;

    public byte[] BufferMessage(){
        int timeStamp = (int) (this.DateTime.getTime() / 1000);
        byte[] buffer = (this.IP + "%" + this.Port + "%" + this.Name + "%" +this.Text + "%" + timeStamp).getBytes(StandardCharsets.UTF_8);
        return buffer;
    }
    public String toString() {
        return this.Name + ": " + this.Text + "\n" + "IP: "+ this.IP + ":"+this.Port  +"   Date: " + this.DateTime.toString();
    }
}
