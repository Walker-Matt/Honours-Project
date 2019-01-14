package com.softwareeng.project.server;

import com.softwareeng.project.server.ClientHandler;
import com.softwareeng.project.server.Controllers.GameController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Server {
	final static Logger log = Logger.getLogger(Server.class);
    private ArrayList<ClientHandler> playerClientHandlers = new ArrayList<>();
    private ServerSocket listener;
    private int numPlayers = -1;

    public Server() throws IOException {
    	log.info("starting server on: localhost at port 9876");
    	System.out.println("starting server on: localhost at port 9876");
    	listener = new ServerSocket(9876);
    }
    
    public Server(String ip, int port) throws Exception {
    	log.info("starting server on: " + ip + " at port " + port);
    	System.out.println("starting server on: " + ip + " at port " + port);
        listener = new ServerSocket(port, 100, InetAddress.getByName(ip));
        //listener = new ServerSocket(port);
    }

    public void removeClientHandler(ClientHandler clientHandler) {
        playerClientHandlers.remove(clientHandler);
    }


    public void start() throws Exception {
        try {
            System.out.println("server started");
            while (true) {
                System.out.println("Accepting next Client..");
                ClientHandler clientHandler = new ClientHandler(listener.accept());
                log.info("Client connected!");
                System.out.println("Wait for socket...");
                playerClientHandlers.add(clientHandler);

                int tempNum = clientHandler.checkForMessageInt();

                while (playerClientHandlers.size() == 1 && tempNum == -1) {
                    tempNum = clientHandler.checkForMessageInt();
                }


                if (tempNum != -1) {
                    numPlayers = tempNum;
                }

                if (playerClientHandlers.size() == numPlayers) {
                    for (ClientHandler cH : playerClientHandlers) {
                        cH.showGameView(numPlayers);
                    }
                    break;
                }
            }

            GameController gameController = new GameController(playerClientHandlers);

            gameController.initGame(numPlayers, 3); //CHANGE THIS NUMBER HERE TO RIG (-1 for regular, 1 for scenario 1, 2 for scenario 2)

            gameController.startGame();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (ClientHandler cH : playerClientHandlers) {
                cH.closeSocket();
            }
            listener.close();
        }
    }
}
