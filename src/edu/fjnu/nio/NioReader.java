package edu.fjnu.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioReader {
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		//关联要读取的文件
		FileInputStream fin = new FileInputStream("e://nioTest.txt");
		//获取通道
		FileChannel channel = fin.getChannel();
		//创建缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		//将数据从通道读入缓冲区
		while (channel.read(buffer) != -1) {
			buffer.flip();
			System.out.println(new String(buffer.array(), 0, buffer.limit(), "UTF-8"));
			buffer.clear();
		}
		
		fin.close();
	}
}
