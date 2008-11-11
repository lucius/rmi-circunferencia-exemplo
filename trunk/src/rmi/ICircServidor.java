package rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICircServidor extends Remote {

	public byte[][] getCircles(byte[][] pic, int IMG_SIZE) throws RemoteException;
}
