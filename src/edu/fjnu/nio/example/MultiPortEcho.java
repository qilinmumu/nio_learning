package edu.fjnu.nio.example;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MultiPortEcho {
	private int ports[];	//用于保存端口号
	private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);

	public MultiPortEcho(int ports[]) throws IOException {
		this.ports = ports;
		go();
	}

	private void go() throws IOException {
		//创建一个选择器对象
		Selector selector = Selector.open();

		//在每个端口上开启监听，绑定端口号，并注册到选择器上
		for (int i = 0; i < ports.length; ++i) {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//设置为false表示非阻塞
			ssc.configureBlocking(false);
			ServerSocket ss = ssc.socket();
			InetSocketAddress address = new InetSocketAddress(ports[i]);
			ss.bind(address);
			
			//将新打开的 ServerSocketChannels 注册到 Selector上
			//SelectionKey 代表这个通道在此 Selector 上的这个注册。
			//当某个 Selector 通知您某个传入事件时，它是通过提供对应于该事件的 SelectionKey 来进行的。
			//SelectionKey 还可以用于取消通道的注册。
			SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("Going to listen on " + ports[i]);
		}

		while (true) {
			//这个方法会阻塞，直到至少有一个已注册的事件发生。
			//当一个或者更多的事件发生时， select() 方法将返回所发生的事件的数量。
			int num = selector.select();

			Set selectedKeys = selector.selectedKeys();
			Iterator it = selectedKeys.iterator();

			while (it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();

				if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
					//接受新连接
					ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
					SocketChannel sc = ssc.accept();
					//将新连接设置为非阻塞
					sc.configureBlocking(false);

					//将连接注册到选择器上， OP_READ 参数表示将 SocketChannel注册用于读取而不是接受新连接。
					SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
					//将处理过的 SelectionKey 从选定的键集合中删除
					//如果我们没有删除处理过的键，那么它仍然会在主集合中以一个激活的键出现，这会导致我们尝试再次处理它.
					it.remove();

					System.out.println("Got connection from " + sc);
				} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					//接收数据
					SocketChannel sc = (SocketChannel) key.channel();
					
					int bytesEchoed = 0;
					while (true) {
						echoBuffer.clear();

						int r = sc.read(echoBuffer);

						if (r <= 0) {
							break;
						}

						echoBuffer.flip();

						sc.write(echoBuffer);
						bytesEchoed += r;
					}

					System.out.println("Echoed " + bytesEchoed + " from " + sc);

					it.remove();
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {
		if (args.length <= 0) {
			System.err.println("Usage: java MultiPortEcho port [port port ...]");
			System.exit(1);
		}

		int ports[] = new int[args.length];

		for (int i = 0; i < args.length; ++i) {
			ports[i] = Integer.parseInt(args[i]);
		}

		new MultiPortEcho(ports);
	}
}
