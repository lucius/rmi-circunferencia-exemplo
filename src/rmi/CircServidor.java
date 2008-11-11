package rmi;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CircServidor extends UnicastRemoteObject implements ICircServidor {

    protected CircServidor() throws RemoteException {
    }

    private static final long serialVersionUID = -8061101079589896457L;

    public byte[][] getCircles(byte[][] pic, int IMG_SIZE) throws RemoteException {
	byte[][] npic = new byte[IMG_SIZE / 2][IMG_SIZE];

	int X = IMG_SIZE / 2;
	int Y = IMG_SIZE;
	int[][] A = new int[X][Y];
	int pixel;
	int r;
	int maxA = 0;

	for (int y = 0; y < Y; y++) {
	    for (int x = 0; x < X; x++) {
		pixel = pic[x][y];

		if (pixel > 0) {
		    for (int x0 = 0; x0 < X; x0++) {
			for (int y0 = 0; y0 < Y; y0++) {
			    r = (int) Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));

			    if ((r >= 3) && (r < 50)) {
				A[x0][y0]++;

				if (A[x0][y0] > maxA) {
				    maxA = A[x0][y0];
				}
			    }
			}
		    }
		}
	    }
	}

	for (int y = 0; y < Y; y++) {
	    for (int x = 0; x < X; x++) {
		npic[x][y] = (byte) ((A[x][y] * 127) / maxA);
	    }
	}

	System.out.println("Servidor repondeu...");
	return (npic);
    }

    public static void main(String[] args) {

	System.setSecurityManager(new RMISecurityManager());

	if (args.length != 1) {
	    System.err.println();
	    System.err.println("Uso: CircServidor ServidorRmi");
	    System.exit(-1);
	}
	try {
	    Naming.rebind(args[0], new CircServidor());
	} catch (Exception e) {

	    System.err.println();
	    System.err.println("Erro: Excessao lancada enquanto se registrava o servico.");
	    System.err.println(e);
	    System.exit(-1);
	}
	
	System.out.println();
	System.out.println("Servidor CircServidor iniciado com o nome: " + args[0]);
    }
}
