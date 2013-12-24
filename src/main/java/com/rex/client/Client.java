package com.rex.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: hur2
 * Date: 12/23/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    private Selector selector;

    public void init(String ip, int port) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        selector = Selector.open();
        socketChannel.connect(new InetSocketAddress(ip,port));
        socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
    }

    public void connect() throws IOException {
        this.selector.select();

        Iterator<SelectionKey> keyIt = this.selector.selectedKeys().iterator();

        while (keyIt.hasNext()){
            SelectionKey key = keyIt.next();

            keyIt.remove();

            if (key.isConnectable()){
                SocketChannel channel = (SocketChannel) key.channel();

                if (channel.isConnectionPending()){
                    channel.finishConnect();
                }

                channel.configureBlocking(false);
                sendMsg(channel);

                channel.socket().close();
                channel.close();

            }
        }
    }

    public void sendMsg(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap(new String("Hello world").getBytes()));
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.init("127.0.0.1", 7778);
        client.connect();
    }
}
