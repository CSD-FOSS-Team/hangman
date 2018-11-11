package com.csdfossteam.hangman.net;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//some helpful documentation: https://pastebin.com/iTfj2pXm


//TODO:most of the lines are to be re-ordered


class Server {

    // TODO: Remember to wrap ServerSocket instantiation with try-with-resources and
    // TODO: also remember to use getMessage() (server),
    // TODO: getLocalizedMessage() (Localized),
    // TODO: getStackTrace() (StackTrace),
    // TODO: toString() (String) in catching IOExceptions

    // checks whether the server is already hosting a game
    private boolean isInGame = false;

    // stores the amount of clients
    // currently connected to the server
    private static AtomicInteger clientsCount = new AtomicInteger();
    private int clientId = clientsCount.incrementAndGet();

    //saving this somewhere in case we use getLocalHost()
    //and it goes wrong: https://stackoverflow.com/questions/1881546/inetaddress-getlocalhost-throws-unknownhostexception#1881967

    // holds the IPv4 address the server
    // is using
    private InetAddress addr;

    {
        try {
            addr = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException uhe) {
            System.err.println("Server Error: " + uhe.getMessage());
            System.err.println("Localized: " + uhe.getLocalizedMessage());
            System.err.println("Stack Trace: " + uhe.getStackTrace());
            System.err.println("To String: " + uhe.toString());
        }
    }

    //TODO: or

    //holds the IPv6 address the server
    //is using
    //private InetAddress addr;

    //{
    //try {
    //addr = InetAddress.getByName("::1");          //::1 is IPv6's loophole, just like 127.0.0.1 for IPv^
    //} catch (UnknownHostException e) {
    //System.err.println("Server Error: " + e.getMessage());
    //System.err.println("Localized: " + e.getLocalizedMessage());
    //System.err.println("Stack Trace: " + e.getStackTrace());
    //System.err.println("To String: " + e.toString());;
    //}
    //}

    //getting the addr in the way above makes it so
    //we can create a ServerSocket and pass it an IP
    //without caring whether it is IPv4 or IPv6

    // holds the port number the server
    // is using
    private int port = 1337; //had to

    // since Server is a skeleton class, we will store
    // its object that will be used from our application
    private Server serverObj;

    // holds the socket used for the initial "handshake" between
    // the server and a client
    ServerSocket sSocket = null;  //might be needed:
    //https://stackoverflow.com/questions/2983835/how-can-i-interrupt-a-serversocket-accept-method#2983860

    //TODO: sSocket.setSoTimeout(); and appropriate try/catch

    {
        try {
            sSocket = new ServerSocket(1337);
        } catch (IOException ioe) {
            System.err.println("Server Error: " + ioe.getMessage());
            System.err.println("Localized: " + ioe.getLocalizedMessage());
            System.err.println("Stack Trace: " + ioe.getStackTrace());
            System.err.println("To String: " + ioe.toString());
            //System.out.println("Accept failed: 1337");
            //System.exit(-1);
        }
    }


    //inside run is the code to be executed by the current thread


    private static class ClientThread implements Runnable {
        // holds the socket used for the server - clients
        // communication AFTER the initial "handshake"
        private final Socket clientSocket;

        //keepRunning will be needed for the shutdown hook
        //volatile boolean because more than one thread might have to
        //access and/or change it

        // TODO: continue working on the shutdown hook, remember to utilize .join
        // TODO: and something like interrupt(), wait()/notifyAll() or smth from java.util.concurrent

        //initialising our keepRunning bool
        static volatile boolean keepRunning = true;

        //AtomicBoolean can also be used, maybe paired with Lambda expressions
        //private static AtomicBoolean keepRunning = new AtomicBoolean(true);

        //thread constructor which will be used in runnable implementation
        ClientThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        //method used to start a ClientThread
        static void start() {
            ClientThread.start();
        }

        //method used to stop a ClientThread        (don't use .stop, it's unsafe and deprecated)
        private static void stopThread() {
            keepRunning = false;
        }
        //TODO:maybe calling ClientThread.interrupt() is better
        //TODO:find out

        public void run(){
            //inside here is the code to be
            //executed when a thread starts

            //just a diag print
            System.out.print("ClientThread Runnable running");

            //(can also wrap inputstream to make it a reader)
            BufferedReader inReader = null;

            // holds the PrintWriter used for sending messages from
            // the server to the clients
            PrintWriter outWriter = null;

            //TODO: figure out what exactly reader's gonna read
            //TODO: and initialize it here
            //placeholder example: (also see line 163)
            //String line;

            try {
                inReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //autoFlush can be changed later on, I just don't see a reason to
                outWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException ioe) {
                System.err.println("Server Error: " + ioe.getMessage());
                System.err.println("Localized: " + ioe.getLocalizedMessage());
                System.err.println("Stack Trace: " + ioe.getStackTrace());
                System.err.println("To String: " + ioe.toString());
                //System.out.println("in or out failure");
                //System.exit(-1);
            }

            //while(keepRunning) {                (also see line 147)
            //try {
            //line = in.readLine();
            //out.println(line); // to send data back to client
            //} catch {
            //(IOException ioe) {
            //System.err.println("Server Error: " + ioe.getMessage());
            //System.err.println("Localized: " + ioe.getLocalizedMessage());
            //System.err.println("Stack Trace: " + ioe.getStackTrace());
            //System.err.println("To String: " + ioe.toString());
            //System.out.println("Read failed");
            //System.exit(-1);
            //}
            //}
            //}

            //here we store all the client listening threads
            //as mentioned above (see line   )
            List<Thread> clientThreads = new ArrayList<>();
            Thread clientThread = new Thread(new ClientThread(clientSocket));

            //assuming we want to start the thread
            //before putting it into the ArrayList
            clientThread.start();
            clientThreads.add(clientThread);

            // TODO: add input/output streams + bufferedreader √
        }

    }

    // ArrayList that contains all the client listening threads.
    ArrayList<ClientThread> clientThreads;

    // holds the BufferedReader used for receiving messages
    // from the clients



    // holds the input stream used for our connection
    // TODO: add try-with-resources
    InputStream inStream;

    // holds the output stream used for our connection
    // TODO: add try-with-resources
    OutputStream outStream;

    //constructor
    public Server(String ipAddr, int port) {

        if (!isInGame) {
            this.addr = addr;
            this.port = port;
            clientThreads = new ArrayList<>();
            serverObj = new Server(ipAddr, port);
        } else {
            System.out.println("*** Error: Server is already hosting a game. ***");
        }




    }

    //The finalize() method is called by the Java virtual machine (JVM)*
    //before the program exits to give the program a chance to clean up
    //and release resources. Multi-threaded programs should close all Files
    //and Sockets they use before exiting so they do not face resource starvation.
    //The call to server.close() in the finalize() method closes the Socket connection
    //used by each thread in this program.
    //see             https://www.oracle.com/technetwork/java/socket-140484.html#multi

    //protected void finalize(){
    //Objects created in run method are finalized when
    //program terminates and thread exits

    //try{
    //sSocket.close();
    //} catch (IOException ioe) {
    //System.err.println("Server Error: " + ioe.getMessage());
    //System.err.println("Localized: " + ioe.getLocalizedMessage());
    //System.err.println("Stack Trace: " + ioe.getStackTrace());
    //System.err.println("To String: " + ioe.toString());
    //System.out.println("Could not close socket");
    //System.exit(-1);
    //}
    //}





}