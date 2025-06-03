package com.chess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileTest {
    public static void main(String[] args) {
        // 测试文件访问
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        
        // 测试访问test1.cmd
        File file = new File("test1.cmd");
        System.out.println("文件存在? " + file.exists());
        System.out.println("文件路径: " + file.getAbsolutePath());
        System.out.println("文件大小: " + file.length() + " 字节");
        System.out.println("是否可读: " + file.canRead());
        
        if (file.exists()) {
            try (Scanner scanner = new Scanner(new FileInputStream(file), StandardCharsets.UTF_8.name())) {
                System.out.println("文件内容:");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    System.out.println("行: [" + line + "]");
                }
            } catch (Exception e) {
                System.out.println("无法打开文件: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("文件不存在");
            
            // 列出当前目录所有文件
            File dir = new File(".");
            System.out.println("当前目录中的文件:");
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    System.out.println(f.getName());
                }
            }
        }
    }
} 