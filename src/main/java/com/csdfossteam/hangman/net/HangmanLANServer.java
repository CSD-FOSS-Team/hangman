/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.net;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * The Server class for connecting with clients.
 *
 * For each client a thread is created and the main class keeps a list and provides easy access to them.
 *
 * Implementation is generic enough to apply to other application
 *
 * Note: Every object send, or every object the send object contains MUST be serializable.
 *       For custom classes, if every class they contain is serializable add "implements serializable" to it
 *       It should serialize automatically if it meets the above prerequisite
 *
 * @author nasioutz
 */
public class HangmanLANServer extends Thread {

    ServerSocket serverSocket;
    ArrayList<serverThread> clientThreadtList;


    /**
     * Create a Server on a port
     * @param port
     * @throws IOException
     */
    public HangmanLANServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientThreadtList = new ArrayList<>();
    }

    /**
     * Wait for a client to appear, create a thread and add to list
     * @return
     * @throws IOException
     */
    public int findClient() throws IOException {

        clientThreadtList.add(new serverThread(serverSocket.accept()));
        return (clientThreadtList.size() - 1);

    }

    /**
     * Provide access to client socket
     * @param i
     * @return Socket
     */
    public Socket getClient(int i)
    {
        return clientThreadtList.get(i).getClient();
    }

    /**
     * Wait for client with index i to send string
     * @param i
     */
    public String receiveFromClient(int i) throws IOException
    {
        String str = clientThreadtList.get(i).receiveFromClient();
        return str;
    }

    /**
     * Send String to client with index i
     * @param i
     * @param str
     * @throws IOException
     */
    public void sendToClient(int i, String str) throws IOException
    {
        clientThreadtList.get(i).sendToClient(str);
    }

    /**
     * Wait for client with index i to send an object
     * @param i
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object receiveObjectFromClient(int i) throws IOException, ClassNotFoundException
    {
        return clientThreadtList.get(i).receiveObjectFromClient();
    }

    /**
     * Send object to client with index i
     * @param i
     * @param ob
     * @throws IOException
     */
    public void sendObjectToClient(int i,Object ob) throws IOException
    {
        clientThreadtList.get(i).sendObjectToClient(ob);
    }

    /**
     * Get previously selected port
     * @return int
     */
    public int getServerPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * Get current machine's Local IP
     * @return String
     * @throws UnknownHostException
     */
    public String getServerIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Get total number of clients attached to server
     * @return int
     */
    public int getClientNumber() {
        return clientThreadtList.size();
    }

    public void removeClient(int i)
    {
        try {
            clientThreadtList.get(i).close();
        } catch (IOException e) {
            System.out.println("Client Socket Already Closed or Doesn't Exist");
        }
        clientThreadtList.remove(i);
    }

    /**
     * Close all client sockets, clear the list and close server socket
     * @throws IOException
     */
    public void freeClients() throws IOException {
        for (serverThread sThr : clientThreadtList)
        {
            sThr.close();
        }

        clientThreadtList.clear();
        serverSocket.close();
    }

    /**
     * Implements a thread to handle each client.
     *
     * Method names are identical to the main server handler but address the specific client socket
     *
     */
    public static class serverThread {

        private Socket clientSocket;

        public serverThread(Socket client)
        {
            clientSocket = client;
        }

        public Socket getClient()
        {
            return clientSocket;
        }

        public String receiveFromClient() throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String str = in.readLine();

            return str;
        }

        public void sendToClient(String str) throws IOException {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.println(str);
            out.flush();
        }

        public Object receiveObjectFromClient() throws IOException, ClassNotFoundException {
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

            return is.readObject();
        }

        public void sendObjectToClient(Object ob) throws IOException {
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());

            os.writeObject(ob);
        }

        public void close() throws IOException {
            clientSocket.close();
        }

    }


}
