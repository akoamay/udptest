package akoamay.cell;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import ch.qos.logback.classic.AsyncAppender;

public class App {

    int m = 10;
    int mb10 = 10485760;

    public static void main(String[] args) {
        new App(args[0], args[1]);
    }

    public App(String mode, String s) {

        int ttl = 0;

        try {
            m = Integer.parseInt(s);
        } catch (Exception e) {
        }

        int size = 1024 * m;
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
                    int mttl = ttl / (1024 * 1024);
                    System.out.println("received:" + cnt + "\t" + data.length + "\t" + mttl);
                    ttl += data.length;
                    cnt++;
                }
            } else {
                ch.socket().bind(new InetSocketAddress(9999));

                for (int i = 0; i < mb10 / (1024 * m); i++) {
                    byte[] map = new byte[size];
                    ByteBuffer buf = ByteBuffer.allocate(size);
                    buf.clear();
                    buf.put(map);
                    buf.flip();
                    // int sent = ch.send(buf, new InetSocketAddress("localhost", 1234));
                    int sent = ch.send(buf,
                            new InetSocketAddress("ec2-18-218-244-32.us-east-2.compute.amazonaws.com", 1234));
                    System.out.println(sent + " sent");
                    ttl += size;
                    Thread.sleep(5);
                }

            }
            ttl /= 1024 * 1024;
            System.out.println("total sent= " + ttl + "MB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
