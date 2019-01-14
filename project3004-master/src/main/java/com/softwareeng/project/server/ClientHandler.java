package com.softwareeng.project.server;

import com.softwareeng.project.commands.ClientCommand;
import com.softwareeng.project.commands.ServerCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private static int nextId = 0;

    private Socket socket;
    private BufferedReader in;
    private int id;
    public PrintWriter out;

    public ClientHandler(Socket socket) throws Exception {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        id = ++nextId;

        if (id == 1) {
            this.out.println(ServerCommand.SHOW_PLAYER_SELECTION_VIEW.toString());
        } else {
            this.out.println(ServerCommand.WAIT_FOR_SETUP.toString());
        }

    }

    @Override
    public void run() {
    	/*
        try {
        	Scanner scanner = new Scanner(in.readLine());

            String cmd = scanner.next();
                
            if(cmd.equals((ClientCommand.NUMBER_OF_PLAYERS.toString()))){
            	int numOfPlayers = scanner.nextInt();
          	}


        }
        catch(SocketException e){
            System.out.println("a Client disconnected!");
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            // This client is going down!  Remove its name and its print
            // writer from the sets, and close its socket.
            if (out != null) {
                server.removeClientHandler(this);
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }*/
    }

    public int checkForMessageInt() {
        int result = -1;
        try {
            if (in.ready()) {
                Scanner scanner = new Scanner(in.readLine());

                String cmd = scanner.next();

                if (cmd.equals((ClientCommand.NUMBER_OF_PLAYERS.toString()))) {
                    int numOfPlayers = scanner.nextInt();
                    result = numOfPlayers;
                } else if (cmd.equals(ClientCommand.PROMPT_BIDS_REPLY.toString())) {
                    int bids = scanner.nextInt();
                    result = bids;
                }

                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean checkForMessageBool() {
        try {
            if (in.ready()) {
                Scanner scanner = new Scanner(in.readLine());

                String cmd = scanner.next();

                if (cmd.equals((ClientCommand.STORY_DECK_CLICKED.toString()))) {
                    scanner.close();
                    return true;
                }

                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String checkForMessageString() {
        String res = "";
        try {
            if (in.ready()) {
                Scanner scanner = new Scanner(in.readLine());
                String cmd = scanner.next();

                if (cmd.equals((ClientCommand.PROMPT_YES_NO_REPLY.toString()))) {
                    boolean result = scanner.nextBoolean();
                    if (result) {
                        res = "true";
                    } else {
                        res = "false";
                    }
                } else if (cmd.equals((ClientCommand.PROMPT_PLAY_CARDS_REPLY.toString()))) {
                    String cards = scanner.nextLine();
                    res = cards;
                } else if (cmd.equals((ClientCommand.PROMPT_DISCARD_REPLY.toString()))) {
                    String cards = scanner.nextLine();
                    res = cards;
                } else if (cmd.equals((ClientCommand.PROMPT_TEST_DISCARD_REPLY.toString()))) {
                    String cards = scanner.nextLine();
                    res = cards;
                } else if (cmd.equals((ClientCommand.PROMPT_END_TURN.toString()))) {
                    boolean result = scanner.nextBoolean();
                    if (result) {
                        res = "true";
                    } else {
                        res = "false";
                    }
                }

                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public void showGameView(int numOfPlayers) {
        out.println(ServerCommand.SHOW_GAMEVIEW + " " + numOfPlayers);
    }

    public int getId() {
        return id;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

}
