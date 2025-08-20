package com.sinse.websocket.config;

import com.sinse.websocket.util.ApplicationContextProvider;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.ApplicationContext;


public class HttpSessionConfiguration extends ServerEndpointConfig.Configurator{

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) {
        ApplicationContext context = ApplicationContextProvider.getContext();
        return context.getBean(clazz);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
      HttpSession session = (HttpSession) request.getHttpSession();
      if (session != null){
          sec.getUserProperties().put(HttpSession.class.getName(), session);
      }
    }
}
