package rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node extends UnicastRemoteObject implements ClientIF {

    private static final long serialVersionUID = -2187805802071755797L;
    private int LClock;
    private int[] VCLock;
    public PriorityQueue<Message> PQueue;
    private int NodeNum;
    public ArrayList<Integer> port;
    public Scanner scan = new Scanner(System.in);
    private String username;
    private String message;
    public Message M1;
    public int Notify;

    public Node() throws RemoteException {
        VCLock = new int[5];
        for (int i = 0; i < 5; i++) {
            VCLock[i] = 0;
        }
        port = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            port.add(2000 + i);
        }
        PQueue = new PriorityQueue<Message>();
        setLClock(0);
        M1 = new Message();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        Node nodeobj = new Node();

        boolean pFound = false;
        while (!pFound && !(nodeobj.getNodeNum() >= nodeobj.port.size())) {

            try {

                Registry registry = LocateRegistry.createRegistry(nodeobj.port.get(nodeobj.getNodeNum()));
                registry.rebind("NodeConnection", nodeobj);
                pFound = true;
            } catch (Exception e) {
                nodeobj.setNodeNum(nodeobj.getNodeNum() + 1);
            }
        }

        if (nodeobj.getNodeNum() <= nodeobj.port.size()) {
            System.out.println("your node used port is equal to " + nodeobj.port.get(nodeobj.getNodeNum()));
            try {
                Registry registry = LocateRegistry.getRegistry(3001);
                ServerIF obj2 = (ServerIF) registry.lookup("TrafficServer");
                nodeobj.port = obj2.conn(nodeobj.port.get(nodeobj.getNodeNum()));
            } catch (Exception ex) {
                System.out.println(" cannot connect to server ");
            }
        } else {

        }
        System.out.println("Please enter your nickname :");
        nodeobj.setUsername(nodeobj.scan.nextLine());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                nodeobj.setMessage(nodeobj.scan.nextLine());
                nodeobj.setLClock(nodeobj.getLClock() + 1);
                nodeobj.VCLock[nodeobj.getNodeNum()] = nodeobj.VCLock[nodeobj.getNodeNum()] + 1;
                nodeobj.M1.setNodeNum(nodeobj.getNodeNum());
                nodeobj.M1.setUser(nodeobj.getUsername());
                nodeobj.M1.setContent(nodeobj.getMessage());
                nodeobj.M1.setLClock(nodeobj.getLClock());
                nodeobj.M1.setVClock(nodeobj.VCLock);
                nodeobj.Notify = nodeobj.Notify + 1;

                for (int i = 0; i < nodeobj.port.size(); i++) {
                    try {
                        Registry reg2 = LocateRegistry.getRegistry(nodeobj.port.get(i));
                        ClientIF obj2 = (ClientIF) reg2.lookup("NodeConnection");
                        obj2.broadcastMessage(nodeobj.M1);
                    } catch (Exception e) {
                    }
                }

            }
        }, 0, 100);

        Timer timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!nodeobj.PQueue.isEmpty()) {
                    try {

                        if (!nodeobj.PQueue.peek().printed && nodeobj.PQueue.peek().getAckts()) {
                            System.out.println(nodeobj.PQueue.peek().toString());
                            nodeobj.PQueue.peek().printed = true;
                            nodeobj.PQueue.poll();
                        }
                        for (int i = 0; i < nodeobj.port.size(); i++) {
                            try {
                                Registry reg2 = LocateRegistry.getRegistry(nodeobj.port.get(i));
                                ClientIF obj2 = (ClientIF) reg2.lookup("NodeConnection");

                                obj2.ReplyAck(nodeobj.PQueue.peek(), nodeobj.getNodeNum());
                            } catch (Exception e) {

                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }, 0, 2000);
    }

    @Override

    public void ReplyAck(Message Msg, int NodeNum) throws RemoteException, Exception {
        if (PQueue.isEmpty()) {
            return;
        }
        if (Msg.EqualinVectorsOf(PQueue.peek())) {
            Message nodemsg = PQueue.poll();
            nodemsg.setAckt(NodeNum);
            PQueue.add(nodemsg);
        }

    }

    @Override
    public void broadcastMessage(Message RM) throws RemoteException {

        this.setLClock(Math.max(this.getLClock(), RM.getLClock()) + 1);

        for (int i = 0; i < 5; i++) {
            VCLock[i] = Math.max(VCLock[i], RM.getVClock(i));
        }
        PQueue.add(RM);
    }

    public int getLClock() {
        return LClock;
    }

    public void setLClock(int lClock) {
        LClock = lClock;
    }

    public int getNodeNum() {
        return NodeNum;
    }

    public void setNodeNum(int num) {
        this.NodeNum = num;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
