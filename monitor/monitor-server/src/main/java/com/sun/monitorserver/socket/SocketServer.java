package com.sun.monitorserver.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.monitorserver.server.handler.MinitorServerHandler;
import com.sun.monitorserver.socket.model.ReceveModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author zcm
 */
@ServerEndpoint("/websocket/mac")
@Component
@Slf4j
public class SocketServer {

    /**
     *     连接客户端数量
     */
    private static int onlineCount;
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private static CopyOnWriteArraySet<SocketServer> webSocketSet = new CopyOnWriteArraySet<SocketServer>();

    /**
     * 连接建立成功调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        log.info("有新窗口连接,当前在线人数为" + getOnlineCount());
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }
    /**
    * 连接关闭调用的方法
    */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 接收消息,并群发
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口的信息:"+message);
        try {
           ReceveModel receveModel =  JSON.parseObject(message, ReceveModel.class);
           String param = receveModel.getParam();
           if ("all".equals(param)){
               sendAllMess();
           }
           return;
        }catch (Exception e){
            e.printStackTrace();
        }
        //群发消息
        System.out.println(webSocketSet.size());
        for (SocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 异常
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    /**
     * 实现服务器主动推送
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SocketServer.onlineCount++;
    }
    public static synchronized void subOnlineCount() {
        SocketServer.onlineCount--;
    }
    public static CopyOnWriteArraySet<SocketServer> getWebSocketSet() {
        return webSocketSet;
    }

    public void sendAllMess(){
        Map<String,Object> macResMap = new HashMap<>();
        //数量
        macResMap.put("onlinecount", MinitorServerHandler.allMsgMap.size());

        //机器信息
        List<JSONObject> list = new ArrayList<>();
        MinitorServerHandler.allMsgMap.forEach((key,value)->{
            list.add((JSONObject) value);
        });
        macResMap.put("macInfoList",list);
        String msg = JSON.toJSONString(macResMap);
        try {
            sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void sendAllMsg(){

        Map<String,Object> macResMap = new HashMap<>();
        //数量
        macResMap.put("onlinecount", MinitorServerHandler.allMsgMap.size());

        //机器信息
        List<JSONObject> list = new ArrayList<>();
        MinitorServerHandler.allMsgMap.forEach((key,value)->{
            list.add((JSONObject) value);
        });
        macResMap.put("macInfoList",list);
        String msg = JSON.toJSONString(macResMap);

        for (SocketServer item : webSocketSet) {
            try {
                item.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
