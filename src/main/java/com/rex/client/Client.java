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

    public void connect() throws IOException, InterruptedException {
        this.selector.select();

        Iterator<SelectionKey> keyIt = this.selector.selectedKeys().iterator();

        while (keyIt.hasNext()){
            SelectionKey key = keyIt.next();

            keyIt.remove();

            if (key.isConnectable()){
                SocketChannel channel = (SocketChannel) key.channel();


                // Finish the connection. If the connection operation failed
                // this will raise an IOException.
                try {
                    channel.finishConnect();
                } catch (IOException e) {
                    // Cancel the channel's registration with our selector
                    System.out.println(e);
                    key.cancel();
                    return;
                }
                key.interestOps(SelectionKey.OP_WRITE);

                channel.configureBlocking(false);
                sendMsg(channel);

                Thread.sleep(5000);
                sendMsg(channel);

                channel.socket().close();
                channel.close();

            }
        }
    }

    public void sendMsg(SocketChannel channel) throws IOException {
        channel.write(ByteBuffer.wrap(new String("Hello world111111111111111").getBytes()));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.init("127.0.0.1", 8080);
        client.connect();
    }
}
