package com.internshala.helloworld.ongcattendance;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MyClient {
    public static String ip=null;
    public MyClient()
    {}
    public void run1(String x)
    {
        try
        {
            DatagramSocket clientSocket = new DatagramSocket();
            int a=x.lastIndexOf('.');
            String y=x.substring(0,a);
            //y.concat(".255");
            System.out.println(y+".255");
            InetAddress IPAddress = InetAddress.getByName("172.29.180.255");//to put 192.168.1.255
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "Get Server IP";
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            //it creates a datagram packet. This constructor is used to send the packets.
            clientSocket.send(sendPacket);


            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            // it creates a datagram packet. This constructor is used to receive the packets.
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            ip=new String("http://"+modifiedSentence+"/naya2/");
            System.out.println("FROM SERVER:" + ip);
            clientSocket.close();
        }
        catch(Exception e)
        {e.printStackTrace();}
    }

    public String getIP()
    {
        return ip;
    }
}
