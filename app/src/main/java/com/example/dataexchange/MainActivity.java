package com.example.dataexchange;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DatagramSocket socket;
    TextView tv_messages;
    SocketAddress local_address;
    InetAddress local_network;
    String[] settings = new String[4];
    //settings[0] - name
    //settings[1] - ip
    //settings[2] - sendPort
    //settings[3] - recievePort

    byte[]  recieve_buffer = new byte[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings[0] = "Student";
        settings[1] = "192.168.0.1";
        settings[2] = "9000";
        settings[3] = "9000";

        tv_messages = findViewById(R.id.tv_messages);
        g.messages = new DB(this, "messages.db", null, 1);
        ArrayList<Message> messages = new ArrayList<Message>();
        g.messages.LoadHistory(messages);

        String previuos_text = "";
        for (int i = 0; i < messages.size(); i++){
            previuos_text += "\n" + messages.get(i).toString();
        }
        tv_messages.setText(previuos_text);

        try {
            local_network = InetAddress.getByName("0.0.0.0"); //адрес подсети?
            local_address = new InetSocketAddress(local_network, Integer.parseInt(settings[3]));
            socket = new DatagramSocket(null);
            socket.bind(local_address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        Runnable reciever = new Runnable() {
            @Override
            public void run() {
                Log.e("TEST","RECIEVENG THREAD IS RUNNING");
                DatagramPacket recieve_packet = new DatagramPacket(recieve_buffer,recieve_buffer.length);
                while  (true)
                {
                    try {
                        socket.receive(recieve_packet);
                    } catch (IOException e) { e.printStackTrace(); }

                    String recieved_data = new String(recieve_packet.getData(),0, recieve_packet.getLength());
                    String[] data = recieved_data.split("%",5);

                    Message m = new Message();
                    m.IP = data[0];
                    m.Port = data[1];
                    m.Name = data[2];
                    m.Text = data[3];
                    Timestamp ts = new Timestamp(Long.parseLong(data[4]));
                    Date d = new Date(ts.getTime());
                    m.DateTime =d;
                    Log.e("TEST","RECIEVED PACKET");
                    Log.e("TEST",recieved_data);

                    runOnUiThread(() -> {
                        String previuos_text = tv_messages.getText().toString();
                        previuos_text += "\n" + m.toString();
                        tv_messages.setText(previuos_text);
                    });
                }
            }
        };
        Thread recieving_thread = new Thread(reciever);
        recieving_thread.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("TEST", " Method onActivity result initializing");
        if (requestCode == 555) {
            if (data !=null) {
                settings[0]  = data.getStringExtra("name");
                settings[1]  = data.getStringExtra("ip");
                settings[2]  = data.getStringExtra("sendPort");
                settings[3]  = data.getStringExtra("recievePort");
                ChangeData();
                Log.e("TEST","RECIEVED DATA FROM SETTINGS ACTIVITY");
                Log.e("TEST",settings[0] + settings[1] + settings[2] + settings[3]);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ChangeData() {
        local_address = new InetSocketAddress(local_network, Integer.parseInt(settings[3]));
    }


    DatagramPacket send_packet;
    public void onButtonSendMessage_Click(View v){

        EditText et_msg = findViewById(R.id.et_message);
        String msg = et_msg.getText().toString();

        Message m = new Message();
        m.IP = settings[1];
        m.Port = settings[2];
        m.Name = settings[0];
        m.Text = msg;
        m.DateTime = new Date();

        byte[] send_buffer = m.BufferMessage();

        try {
            InetAddress remote_address = InetAddress.getByName(settings[1]);
            send_packet = new DatagramPacket(send_buffer,send_buffer.length,remote_address,Integer.parseInt(settings[2]));
        } catch ( UnknownHostException e) { e.printStackTrace(); }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.e("TEST","SENDING THREAD IS RUNNING");
                try {
                    socket.send(send_packet);
                    Log.e("TEST","PACKET SENT");
                    g.messages.SaveMessage(m);
                } catch (IOException e) { e.printStackTrace(); }
            }
        };

        Thread send_thread = new Thread(r);
        send_thread.start();

    }



    public void onButtonSettings_Click(View v) {

        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("ip", settings[1]);
        i.putExtra("sendPort", settings[2]);
        i.putExtra("recievePort",settings[3]);
        i.putExtra("name",settings[0]);
        startActivityForResult(i,555);

    }
}