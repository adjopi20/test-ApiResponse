package com.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        List<String> topArticles = TopArticles.TopArticles("olalonde", 1);
        System.out.println(topArticles);
    }
}
