package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerIF extends Remote{

	public ArrayList<Integer> conn(int port) throws RemoteException;
}
