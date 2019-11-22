package akoamay.cell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.qos.logback.classic.AsyncAppender;

public class App {
    public static final byte START = 0x00;
    public static final byte DATA = 0x01;
    public static final byte FINISH = 0x02;

    int m = 5;
    int mb10 = 10485760;
    String host = "localhost";
    //String host = "13.231.146.48";

    public static void main(String[] args) {
        new App(args[0], args[1], args[2]);
    }

    byte[] shorts2bytes(short[] input) {
        int index;
        int iterations = input.length;

        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);

        for (index = 0; index != iterations; ++index) {
            bb.putShort(input[index]);
        }

        return bb.array();
    }

    public static byte[] zipBytes(byte[] input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            ZipEntry entry = new ZipEntry("hoge");
            zos.putNextEntry(entry);
            zos.write(input);
            zos.closeEntry();
            zos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] unzipBytes(byte[] input){
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

        try{
            ZipInputStream zis = new ZipInputStream(bais);
            zis.getNextEntry();

            byte[] buf = new byte[1024];

            int len = zis.read(buf);

            while (len > 0) {
                baos.write(buf, 0, len);

                len = zis.read(buf);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public void client(int m, int w) throws IOException, InterruptedException {
        int len = 1024;
        int cnt = 0;
        short[] smap = new short[len * len];
        for (int i = 0; i < len * len; i++) {
            smap[i] = (short) (Math.random() * 10);
        }
        System.out.println("smap.len=" + smap.length);
        byte[] map = shorts2bytes(smap);
        System.out.println("omap.len=" + map.length);
        byte[] zmap = zipBytes(map);
        System.out.println("zmap.len=" + zmap.length);

        int size = 1024 * m;
        int send_cnt = zmap.length / (size-1);

        int p = (int) Math.random() * 65535;
        DatagramChannel ch = DatagramChannel.open();
        ch.socket().bind(new InetSocketAddress(p));

        int bs = zmap.length;
        int ks = zmap.length / 1024;
        int ms = zmap.length / 1024 / 1024;
        System.out.println("data size=" + bs + "byte " + ks + "kb " + ms + " mb");

        ByteBuffer buf = ByteBuffer.allocate(size);
        int ttl = 0;
        for (int i = 0; i < send_cnt; i++) {
            buf.clear();
            buf.put(DATA);
            buf.put(zmap, (size-1) * i, size-1);
            buf.flip();
            int sent = ch.send(buf, new InetSocketAddress(host, 1234));
            System.out.println(sent + " sent");
            ttl += size;
            Thread.sleep(w);
            cnt++;
        }

        if (zmap.length % (size-1) != 0) {
            buf.clear();
            buf.put(FINISH);
            buf.put(zmap, (size-1) * (send_cnt - 1), zmap.length % (size-1));
            buf.flip();
            int sent = ch.send(buf, new InetSocketAddress(host, 1234));
            System.out.println(sent + " sent");
            ttl += zmap.length % size;
            cnt++;
        }

        bs = ttl;
        ks = ttl / 1024;
        ms = ttl / 1024 / 1024;
        System.out.println("total sent= " + bs + "byte " + ks + "kb " + ms + "mb \tcnt=" + cnt);
    }

    public void server(int m) throws IOException {
        DatagramChannel ch = DatagramChannel.open();
        int cnt = 0;
        int ttl = 0;
        ch.socket().bind(new InetSocketAddress(1234));

        int size = 1024 * m;
        ByteBuffer buf = ByteBuffer.allocate(size);

        DataBuffer db = new DataBuffer(1024*m);

        while (true) {
            buf.clear();
            SocketAddress from = ch.receive(buf);
            buf.flip();
            byte[] data = new byte[buf.limit()];
            buf.get(data);
            byte header = data[0];
            if ( header == DATA ){
                db.put(from,data);
                System.out.println("received:" + from + "\t" + data.length );
            }else if ( header == FINISH ){
                db.put(from,data);
                byte[] tdata = db.get( from );
                ttl = tdata.length;
                int bs = ttl;
                int ks = ttl / 1024;
                int ms = ttl / 1024 / 1024;
                System.out.println("total received:" + from + "\t" + data.length + "\t" + bs + "byte " + ks + "kb " + ms + "mb");
            }

        }
    }

    public App(String mode, String s, String s2) {

        int w = 5;

        try {
            m = Integer.parseInt(s);
            w = Integer.parseInt(s2);
        } catch (Exception e) {
        }

        try {
            if (mode.equals("s")) {
                server(m);
            } else {
                client(m, w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
