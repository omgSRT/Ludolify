package com.omgsrt.Ludolify.v1.notification;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class SocketIOConfiguration implements DisposableBean {
    private SocketIOServer socketIOServer;

    @EventListener(ApplicationReadyEvent.class)
    public void startSocketIO() {
        socketIOServer.start();
        System.out.println("Socket.IO server started on "
                + socketIOServer.getConfiguration().getHostname()
                + ":" + socketIOServer.getConfiguration().getPort());
    }

    @Override
    public void destroy() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            System.out.println("✅ Socket.IO server stopped gracefully.");
        }
    }

    @Bean
    public SocketIOServer socketIOServer(CustomSocketIOEventHandler customSocketIOEventHandler,
                                         CustomSocketIOExceptionListener customSocketIOExceptionListener) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(16234);
        config.setUpgradeTimeout(15000);
        config.setAllowCustomRequests(true);
        config.setEnableCors(true);
        config.setOrigin("*");
        config.setAllowHeaders("*");

//        config.setPingInterval(5000);
//        config.setPingTimeout(10000);

        config.setExceptionListener(customSocketIOExceptionListener);
        //whether or not to be authorized before connect to socketIO server
        //config.setAuthorizationListener(new CustomAuthorizationListener());
        //set socket.io to accept Date-related data
        config.setJsonSupport(new CustomSocketIOJsonSupportHandler());

        //an example to config wss for socketIO to go along with https
//        config.setHostname("fifoforumonline.click");
//        InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream("keystore.p12");
//        if (keystoreStream == null) {
//            throw new IllegalStateException("Keystore file not found!");
//        }
//        config.setKeyStore(keystoreStream);
//        config.setKeyStoreFormat("PKCS12");
//        config.setKeyStorePassword("password");

        config.setHostname(null);

        SocketIOServer server = new SocketIOServer(config);
        server.addListeners(customSocketIOEventHandler);
        this.socketIOServer = server;

        return server;
    }
}
