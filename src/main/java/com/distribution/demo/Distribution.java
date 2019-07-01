package com.distribution.demo;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution {

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            System.err.println("Usage: Driver <inputfile>");
            return;
        }
        final File file = new File(args[0]);
        if (!file.isFile()) {
            System.out.println("配置文件没有找到，请输入正确的配置文件");
            return;
        }
        InputStream is = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String serverName;
        while (true) {
            serverName = reader.readLine();
            if (serverName != null) {
                startUserInfoServer(serverName);
                System.out.println("server_name=" + serverName);
            } else
                break;
        }
        is.close();
    }


    /**
     * 串行
     * 启动一个服务器
     *
     * @param serverName
     */
    public static void startUserInfoServer(String serverName) {
        try {
            List<ServerNode> serverNodes = getServerNode(serverName);
            serverNodes.forEach(node -> new Thread(new Server(node)).start());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器
     */
    static class Server implements Runnable {
        private ServerNode node;

        public Server(ServerNode node) {
            this.node = node;
        }

        @Override
        public void run() {
            System.out.println(String.format("server start begin ip=%s,port=%s", node.getIp(), node.getPort()));
            SocketAddress socketAddress = new InetSocketAddress(node.getIp(), node.getPort());
            ServerSocket serverSocket;
            try {
                String result = process(node.getName());
                System.out.println(result);
                serverSocket = new ServerSocket(node.getPort(), 521, ((InetSocketAddress) socketAddress).getAddress());
                serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startRpcServer(int port, String ip) {

    }

    public static String process(String infoType) {
        String info = "";
        switch (infoType) {
            case "userInfoServer":
                info = "user info";
                break;
            case "orderInfoServer":
                info = "order info";
                break;
        }
        return info;
    }


    public static List<ServerNode> getServerNode(String name) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc;
        Map<String, List<ServerNode>> nodeMap = new HashMap<>();
        doc = builder.parse(new File("src/main/resources/rpc-server.xml"));
        NodeList nodeList = doc.getElementsByTagName("server");
        //对所有的sever进行遍历
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node servers = nodeList.item(i);
            NamedNodeMap attrs = servers.getAttributes();
            ServerNode serverNode = new ServerNode();
            for (int m = 0; m < attrs.getLength(); m++) {
                Node attr = attrs.item(m);
                Field f = serverNode.getClass().getDeclaredField(attr.getNodeName());
                Class<?> type = f.getType();
                String value = attr.getNodeValue();
                if (Number.class.isAssignableFrom(type)) {
                    f.set(serverNode, Integer.parseInt(value));
                } else {
                    f.set(serverNode, value);
                }
                f.setAccessible(true);
            }
            List<ServerNode> list = nodeMap.get(serverNode.getName());
            if (list == null) {
                list = new ArrayList<ServerNode>();
                list.add(serverNode);
                nodeMap.put(serverNode.getName(), list);
            } else {
                list.add(serverNode);
                nodeMap.put(serverNode.getName(), list);
            }
            System.out.println(serverNode.toString());
        }
        return nodeMap.get(name);
    }

    public static class ServerNode {
        public Integer id;
        public String name;
        public Integer port;
        public Integer rpc;
        public String ip;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getRpc() {
            return rpc;
        }

        public void setRpc(Integer rpc) {
            this.rpc = rpc;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        @Override
        public String toString() {
            return "ServerNode{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", port=" + port +
                    ", rpc=" + rpc +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }

}
