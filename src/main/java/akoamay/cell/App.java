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

        int size = 5024;
        try {
            DatagramChannel ch = DatagramChannel.open();
            int cnt = 0;
            if (mode.equals("s")) {
                ch.socket().bind(new InetSocketAddress(1234));

                ByteBuffer buf = ByteBuffer.allocate(size);

                while (true) {
                    buf.clear();
                    ch.receive(buf);
                    buf.flip();
                    byte[] data = new byte[buf.limit()];
                    buf.get(data);
                    System.out.println("received:" + cnt + "\t" + data.length);
                    cnt++;
                }
            } else {
                ch.socket().bind(new InetSocketAddress(9999));

                try {
                    for (int i = 0; i < 1024; i++) {
                        Thread.sleep(10);
                        byte[] map = new byte[size];
                        ByteBuffer buf = ByteBuffer.allocate(size);
                        buf.clear();
                        buf.put(map);
                        buf.flip();
                        // int sent = ch.send(buf, new InetSocketAddress("localhost", 1234));
                        int sent = ch.send(buf,
                                new InetSocketAddress("ec2-18-222-183-235.us-east-2.compute.amazonaws.com", 1234));
                        System.out.println(sent + " sent");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
