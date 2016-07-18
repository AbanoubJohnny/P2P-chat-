package rmi;

import java.util.List;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import org.omg.CORBA.FREE_MEM;

public class traffic extends UnicastRemoteObject implements ServerIF {

    private static final long serialVersionUID = 1L;

    public traffic() throws RemoteException {
    }
    private static List<Integer> clients;
    private static ArrayList<Integer> Aliveclients = new ArrayList<Integer>();
    private static ArrayList<Integer> Deadclients = new ArrayList<Integer>();
    private static int port = 3001;

    public static void main(String[] args) throws RemoteException {
        clients = Arrays.asList(2000, 2001, 2002, 2003, 2004);

        try {
            traffic obj = new traffic();
            System.out.println("HERE WE START");
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("TrafficServer", obj);
        //    System.out.println("HERE WE ENDED");
        } catch (Exception e) {
            System.out.println("sever err: " + e.getMessage());
            e.printStackTrace();
        }
        int i = JOptionPane.YES_OPTION;
        while (i == JOptionPane.YES_OPTION) {

            i = JOptionPane.showConfirmDialog(null,
                    "Really want to see the serverr !!", "Traffic ", JOptionPane.YES_NO_OPTION);
            if (i == JOptionPane.YES_OPTION) {
                read();
            } else {
                System.exit(0);
            }
        }
        /*  Timer timer = new Timer();
         timer.scheduleAtFixedRate(new TimerTask() {
         @Override
         public void run() {
         check();
         }
         }, 0, 2000);
         */
    }

    public ArrayList<Integer> conn(int port) {
        System.out.println("it might be connected with " + port);
        check();
        return Aliveclients;
    }

    public ArrayList<Integer> getAlive() {
        return Aliveclients;
    }

    public ArrayList<Integer> getDead() {
        return Deadclients;
    }

    public String sayHello() {
        return "Hello world!";
    }

    public static void check() {
        int index = 0;
        Aliveclients.clear();
        Deadclients.clear();
        while (index < clients.size()) {
            try {
                Registry registry = LocateRegistry.getRegistry(clients.get(index));
                registry.list();
                Aliveclients.add(clients.get(index));
                index++;
            } catch (RemoteException e) {
                Deadclients.add(clients.get(index));
                index++;
            }
        }
    }

    public static void read() {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("\n\nHere the new checking of time : " + dateFormat.format(date) + "\n");
        check();
        String s = "";
        for (int i = 0; i < Aliveclients.size(); i++) {
            s += Aliveclients.get(i) + "''";
        }
        String s2 = "";
        for (int i = 0; i < Deadclients.size(); i++) {
            s2 += Deadclients.get(i) + "''";
        }
        if (s == "") {
            System.out.println("no connections!!");
        } else {
            System.out.println("here is the Alive :   " + s);
        }
        if (s2 == "") {
            System.out.println("no Dead Al Alive!!");
        } else {
            System.out.println("here is the dead :   " + s2);
        }

    }
}
