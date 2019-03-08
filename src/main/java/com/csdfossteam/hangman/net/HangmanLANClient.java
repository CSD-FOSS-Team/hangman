package com.csdfossteam.hangman.net;


import java.io.*;
import java.net.Socket;

/**
 * The Client class for sending socket packages to a server
 *
 * Implementation is generic enough to apply to other application
 *
 * Note: Every object send, or every object the send object contains MUST be serializable.
 *       For custom classes, if every class they contain is serializable add "implements serializable" to it
 *       It should serialize automatically if it meets the above prerequisite
 *
 * @author nasioutz
 */
public class HangmanLANClient
{
    Socket remoteServerSocket;

    /**
     * Create a client socket and attempt connection to server socket with ip and port
     * @param ip
     * @param port
     * @throws IOException
     */
    public HangmanLANClient(String ip, int port) throws IOException {

        remoteServerSocket = new Socket(ip,port);
    }

    /**
     * Send String to server
     * @param str
     * @throws IOException
     */
    public void sendToServer(String str) throws IOException
    {
        PrintWriter out = new PrintWriter(remoteServerSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(remoteServerSocket.getInputStream()));

        out.println(str);
        out.flush();
    }

    /**
     * Send an Object to Server
     * @param ob
     * @throws IOException
     */
    public void sendObjectToServer(Object ob) throws IOException
    {
        ObjectOutputStream os = new ObjectOutputStream(remoteServerSocket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(remoteServerSocket.getInputStream());

        os.writeObject(ob);
    }

    /**
     * Wait for Server to send String
     * @return String
     * @throws IOException
     */
    public String receiveFromServer() throws IOException
    {
        PrintWriter out = new PrintWriter(remoteServerSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(remoteServerSocket.getInputStream()));
        String str = in.readLine();

        return str;
    }

    /**
     * Wait for Server to send a Object
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object receiveObjectFromServer() throws IOException, ClassNotFoundException {
        ObjectOutputStream os = new ObjectOutputStream(remoteServerSocket.getOutputStream());
        ObjectInputStream is = new ObjectInputStream(remoteServerSocket.getInputStream());

        return is.readObject();
    }

    /**
     * Close client socket
     */
    public void freeClient() {
        try {remoteServerSocket.close();}
        catch (IOException e) {System.out.println("ERROR: "+e);}
    }

    /**
     * Check if client socket is connected to server
     * @return boolean
     */
    public boolean isConnected()
    {
        return remoteServerSocket.isConnected();
    }



}
