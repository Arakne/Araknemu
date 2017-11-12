///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package fr.quatrevieux.araknemu.network.realm.out;
//
//import org.peakemu.common.CryptManager;
//
///**
// *
// * @author Vincent Quatrevieux <quatrevieux.vincent@gmail.com>
// */
//public class SelectServerCrypt {
//    private String ip;
//    private int port;
//    private String key;
//
//    public String getIp() {
//        return ip;
//    }
//
//    public void setIp(String ip) {
//        this.ip = ip;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    @Override
//    public String toString() {
//        return "AXK" + CryptManager.CryptIP(ip) + CryptManager.CryptPort(port) + key;
//    }
//}
