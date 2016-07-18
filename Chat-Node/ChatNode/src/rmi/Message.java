package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Message implements Comparable<Message>, java.io.Serializable {

    private static final long serialVersionUID = 8218008257715913027L;
    private int LClock;
    private int[] VClock;
    private String content;
    private String user;
    private int NodeNum;
    boolean printed;
    private boolean[] Ackts;

    // constructor for the message
    public Message() {
        VClock = new int[5];
        for (int i = 0; i < 5; i++) {
            VClock[i] = 0;
        }
        printed=false;
        Ackts = new boolean[5];
        for (int i = 0; i < 5; i++) {
            Ackts[i] = false;
        }

    }

    public boolean EqualinVectorsOf(Message m) {
        for (int i = 0; i < 5; i++) {
            if (this.getVClock(i) != (m.getVClock(i))) {
                return false;
            }
        }
        return true;

    }

    // setter and getter for the number of the node who created this message
    public int getNodeNum() {
        return NodeNum;
    }

    public void setNodeNum(int num) {
        this.NodeNum = num;
    }

    // setter And getter for the Acknowledgements of the Message
    public boolean getAckts() {
        for (int i = 0; i < Ackts.length; i++) {
            if (!Ackts[i]) {
                try {
                    Registry registry = LocateRegistry.getRegistry(2000 + i);
                    registry.list();
                    //System.out.println("port Alive  = "+(2000 + i));
                    return false;
                } catch (RemoteException e) {
                    
                }
            }
        }
        return true;
    }

    public void setAckt(int i) {
        Ackts[i] = true;
    }

    // setters and getters for the Message Logical clock
    public int getLClock() {
        return LClock;
    }

    public void setLClock(int lClock) {
        LClock = lClock;
    }

    // setters and getters for Vector clock of the message
    public int getVClock(int num) {
        return VClock[num];
    }

    public void setVClock(int[] clk) {
        this.VClock = clk;
    }

    // setters and getters for the content of the message
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // setters and getters for user who created the message
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // string printed when the message is printed
    @Override
    public String toString() {
        return ">" + user + " : " + content;
    }

    // compare the messages as using the logical counter
    @Override
    public int compareTo(Message msg) {
        if (this.LClock < msg.getLClock() && this.getNodeNum() < msg.getNodeNum()) {
            return -1;
        } else if (this.LClock == msg.getLClock() && this.getNodeNum() < msg.getNodeNum()) {
            return -1;
        } else if (this.LClock == msg.getLClock() && this.getNodeNum() > msg.getNodeNum()) {
            return 1;
        } else {
            return 1;
        }
    }
}
