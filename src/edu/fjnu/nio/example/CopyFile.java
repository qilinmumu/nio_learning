package edu.fjnu.nio.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyFile {
	public static void main(String args[]) throws Exception {
		String inFile = "e://infile.txt";
		String outFile = "e://outfile.txt";
		
		//创建输入流和输入流，分别关联要读取的文件和要写入的文件
		FileInputStream in = new FileInputStream(inFile);
		FileOutputStream out = new FileOutputStream(outFile);
		
		//获得通道，一个用户读取数据，一个用于写入数据
		FileChannel fcin = in.getChannel();
		FileChannel fcout = out.getChannel();
		//创建一个缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (true) {
			//在读取数据之前重设缓冲区，使它可以接受读入的数据
			buffer.clear();

			int count = fcin.read(buffer);
			//表示文件的数据已经全部读完，则退出循环
			if (count == -1) {
				break;
			}

			buffer.flip();
			//将缓冲区的数据写入通道
			fcout.write(buffer);
		}
		
		//关闭输入和输出流
		in.close();
		out.close();
	}
}
