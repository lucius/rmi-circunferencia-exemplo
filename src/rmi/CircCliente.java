package rmi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Date;

import javax.swing.JFrame;

public class CircCliente extends JFrame {

    private static final long serialVersionUID = -7078688831537902019L;
    final int IMG_SIZE = 256;
    byte[][] pic = new byte[IMG_SIZE][IMG_SIZE];
    byte[][] pic1 = new byte[IMG_SIZE / 2][IMG_SIZE];
    byte[][] pic2 = new byte[IMG_SIZE / 2][IMG_SIZE];
    int[] lut = new int[256];

    BufferedImage img = new BufferedImage(IMG_SIZE, IMG_SIZE, BufferedImage.TYPE_INT_BGR);

    public void paint(Graphics g) {

	for (int i = 0; i < IMG_SIZE; i++) {
	    for (int j = 0; j < IMG_SIZE; j++) {
		img.setRGB(i, j, lut[pic[i][j]]);
	    }
	}

	Graphics2D g2 = (Graphics2D) g;
	g2.drawImage(img, 0, 25, this);
    }

    protected Image getImage(String fileName) {

	Image img = getToolkit().getImage(fileName);

	try {
	    MediaTracker tracker = new MediaTracker(this);
	    tracker.addImage(img, 0);
	    tracker.waitForID(0);
	    tracker.removeImage(img, 0);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(-1);
	}

	return img;
    }

    public void iniciar(String servidor1, String servidor2, String imagem) {

	setTitle("Circ");
	setSize(IMG_SIZE, IMG_SIZE + 30);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setLocationRelativeTo(null);
	setVisible(true);

	// Cria mapa de cores em tons cinza
	for (int i = 0; i < 128; i++) {
	    lut[i] = 2 * i + 256 * i * 2 + 256 * 256 * i * 2;
	}

	Image im = getImage(imagem);
	int[] pixels = new int[IMG_SIZE * IMG_SIZE];

	// Converte a imagem para um Array de Bytes
	PixelGrabber pg = new PixelGrabber(im, 0, 0, IMG_SIZE, IMG_SIZE, pixels, 0, IMG_SIZE);
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    System.err.println();
	    System.err.println("Erro: Espera por pixels interrompida.");
	    System.err.println();
	    System.exit(-1);
	}

	for (int i = 0; i < IMG_SIZE; i++) {
	    for (int j = 0; j < IMG_SIZE; j++) {
		if ((pixels[j * IMG_SIZE + i] & 0x80) == 0x80) {
		    pic[i][j] = 127;
		} else {
		    pic[i][j] = 0;
		}
	    }
	}

	for (int i = 0; i < IMG_SIZE / 2; i++) {
	    for (int j = 0; j < IMG_SIZE; j++) {
		pic1[i][j] = pic[i][j];
	    }
	}

	for (int i = 0; i < IMG_SIZE / 2; i++) {
	    for (int j = 0; j < IMG_SIZE; j++) {
		pic2[i][j] = pic[i + IMG_SIZE / 2][j];
	    }
	}

	repaint();

	System.setSecurityManager(new RMISecurityManager());

	String nameServer1 = servidor1;
	String nameServer2 = servidor2;

	ICircServidor server1 = null;
	ICircServidor server2 = null;

	try {
	    server1 = (ICircServidor) Naming.lookup(nameServer1);
	    server2 = (ICircServidor) Naming.lookup(nameServer2);
	} catch (Exception e) {
	    System.err.println();
	    System.err.println("Erro: Excessao lancada durante a resolucao de nomes.");
	    System.err.println(e);
	    System.exit(-1);
	}

	try {
	    double start = new Date().getTime();
	    this.pic1 = server1.getCircles(pic1, IMG_SIZE);
	    System.out.println();
	    System.out.println("Tempo de espera em " + nameServer1 + " de " + (new Date().getTime() - start) + " ms");

	    start = new Date().getTime();
	    System.out.println();
	    this.pic2 = server2.getCircles(pic2, IMG_SIZE);
	    System.out.println("Tempo de espera em " + nameServer2 + " de " + (new Date().getTime() - start) + " ms");

	    for (int i = 0; i < IMG_SIZE; i++) {
		for (int j = 0; j < IMG_SIZE; j++) {
		    if (i < IMG_SIZE / 2) {
			pic[i][j] = pic1[i][j];
		    } else {
			pic[i][j] = pic2[i - IMG_SIZE / 2][j];
		    }
		}
	    }

	    repaint();
	} catch (Exception e) {
	    System.err.println();
	    System.err.println("Erro: Excessao lancada enquanto tentava se comunicar com os servidores:");
	    System.err.println(e);
	    System.exit(-1);
	}

    }

    public static void main(String[] args) {

	if (args.length != 3) {
	    System.err.println();
	    System.err.println("Sintaxe incorreta.");
	    System.err.println("Uso: CircCliente ServidorRmi1 ServidorRmi2 Imagem.jpg");
	    System.exit(-1);
	}

	CircCliente cliente = new CircCliente();
	cliente.iniciar(args[0], args[1], args[2]);
    }
}