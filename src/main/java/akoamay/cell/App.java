package akoamay.cell;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import ch.qos.logback.classic.AsyncAppender;

public class App {

    public static void main(String[] args) {
        new App(args[0]);
    }

    public App(String mode) {

        int size = 65000;
        try {
            DatagramChannel ch = DatagramChannel.open();
            if (mode.equals("s")) {
                ch.socket().bind(new InetSocketAddress(1234));

                ByteBuffer buf = ByteBuffer.allocate(size);
                buf.clear();

                System.out.println("waiting");
                ch.receive(buf);
                System.out.println("received");
                buf.flip();
                byte[] data = new byte[buf.limit()];
                buf.get(data);
                System.out.println("received:" + data.length);
            } else {
                ch.socket().bind(new InetSocketAddress(9999));

                byte[] map = new byte[size];
                ByteBuffer buf = ByteBuffer.allocate(size);
                buf.clear();
                buf.put(map);
                buf.flip();

                System.out.println("sending");
                int sent = ch.send(buf, new InetSocketAddress("localhost", 1234));
                System.out.println(sent + " sent");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
