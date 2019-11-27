package akoamay.cell;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class DataBuffer{

    HashMap<SocketAddress, ByteBuffer> datamap = new HashMap<SocketAddress,ByteBuffer>();

    public DataBuffer(){
    }

    public void create(SocketAddress from, int size){
        datamap.put( from, ByteBuffer.allocate(size) );
    }

    public void put( SocketAddress from, byte[] data ){
        if ( datamap.containsKey(from) ){
            datamap.get(from).put(data);
        }
    }

    public byte[] get( SocketAddress from ){
        ByteBuffer buf = datamap.get(from);
        buf.flip();
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        buf.clear();
        datamap.remove(from);
        return bytes;
    }
}