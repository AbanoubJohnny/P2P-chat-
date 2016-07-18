package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientIF extends Remote{
	
	     public void ReplyAck(Message Msg, int NodeNum) throws RemoteException , Exception;
	     public void broadcastMessage(Message Msg) throws RemoteException;
	

}
