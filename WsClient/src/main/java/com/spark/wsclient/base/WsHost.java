package com.spark.wsclient.base;

/**
 * ================================================
 * 作    者：v1ncent
 * 版    本：1.0.0
 * 创建日期：2019/5/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class WsHost {
    public static String wsHost = "wss://ws.exchief.com";//正式
//    public static String wsHost = "ws://ws.bitotc.bench.bitpay.com";//测试


    public static String klineWssUrl = wsHost + "/kline";         //K线
    public static String bbTradeWssUrl = wsHost + "/trade";       //BB交易
    public static String cfdTradeWssUrl = wsHost + "/cfdTrade";   //CFD交易
    public static String handicapWssUrl = wsHost + "/quote";      //盘口
    //    public static String chatWssUrl = "ws://192.168.2.12:28905/ws";                     //聊天测试
    public static String chatWssUrl = "wss://ws.exchief.com/ws";                     //聊天正式
}
