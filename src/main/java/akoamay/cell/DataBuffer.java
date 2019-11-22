package akoamay.cell;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class DataBuffer{

    HashMap<SocketAddress, ByteBuffer> datamap = new HashMap<SocketAddress,ByteBuffer>();
    int dfltSize = 1024;

    public DataBuffer(int dfltSize ){
        this.dfltSize = dfltSize;
    }

    public void put( SocketAddress from, byte[] data ){
        if ( datamap.containsKey(from) ){
            datamap.get(from).put(data);
        }else{
            datamap.put( from, ByteBuffer.allocate(dfltSize) );
        }
    }

    public byte[] get( SocketAddress from ){
        ByteBuffer buf = datamap.get(from);
        byte[] bytes = new byte[buf.limit()];
        buf.flip();
        buf.get(bytes);
        buf.clear();
        datamap.remove(from);
        return bytes;
    }
}