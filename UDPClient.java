package com.example.my_tcp_car;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

public class UDPClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private DataReceivedListener dataListener;
    private volatile boolean receiving = true;
    private static final int BUFFER_SIZE = 1024;
    private static final int LISTEN_PORT = 5207; // 固定监听端口

    public interface DataReceivedListener {
        void onDataReceived(String data, String ip);
    }

    public void setDataReceivedListener(DataReceivedListener listener) {
        this.dataListener = listener;
    }

    public boolean initialize(String serverIp, int serverPort) {
        try {
            this.serverPort = serverPort;
            serverAddress = InetAddress.getByName(serverIp);

            // 创建绑定到指定端口的Socket
            socket = new DatagramSocket(LISTEN_PORT);
            socket.setBroadcast(true); // 允许广播
            socket.setReuseAddress(true);

            startReceiving();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startReceiving() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (receiving && !socket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // 阻塞接收

                    String data = new String(packet.getData(), 0, packet.getLength());
                    String senderIp = packet.getAddress().getHostAddress();

                    if (dataListener != null) {
                        dataListener.onDataReceived(data, senderIp);
                    }
                }
            } catch (Exception e) {
                if (!receiving) return;
                e.printStackTrace();
            }
        }).start();
    }

    public boolean sendData(String data) {
        try {
            byte[] sendData = data.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(
                    sendData,
                    sendData.length,
                    serverAddress,
                    serverPort
            );
            socket.send(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        receiving = false;
        if (socket != null) {
            socket.close();
        }
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
}