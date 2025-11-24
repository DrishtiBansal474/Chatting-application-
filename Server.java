import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    static Vector<ClientHandler> clients = new Vector<>();
    static int clientCount = 0;

    public static void main(String[] args) {
        try {
            ServerSocket skt = new ServerSocket(6001);
            System.out.println(" Server started on port 6001...");

            while (true) {
                Socket s = skt.accept();
                System.out.println("🟢 New client connected: " + s);

                DataInputStream din = new DataInputStream(s.getInputStream());
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());

                ClientHandler handler = new ClientHandler(s, "User" + clientCount, din, dout);
                clients.add(handler);

                Thread t = new Thread(handler);
                t.start();

                clientCount++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {

    String name;
    DataInputStream din;
    DataOutputStream dout;
    Socket s;
    boolean isLoggedIn;

    public ClientHandler(Socket s, String name, DataInputStream din, DataOutputStream dout) {
        this.s = s;
        this.name = name;
        this.din = din;
        this.dout = dout;
        this.isLoggedIn = true;
    }

    @Override
    public void run() {
        String received;

        while (true) {
            try {
                received = din.readUTF();
                System.out.println(name + ": " + received);

                for (ClientHandler ch : Server.clients) {
                    if (ch.isLoggedIn && ch != this) {
                        ch.dout.writeUTF(name + ": " + received);
                    }
                }

            } catch (Exception e) {
                try {
                    this.isLoggedIn = false;
                    this.s.close();
                } catch (IOException ex) {}
                break;
            }
        }

        try {
            din.close();
            dout.close();
        } catch (IOException e) {}
    }
}