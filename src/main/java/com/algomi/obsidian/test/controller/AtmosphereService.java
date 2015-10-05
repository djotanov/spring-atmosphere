package com.algomi.obsidian.test.controller;

import com.algomi.obsidian.test.model.Message;
import com.algomi.obsidian.test.util.JacksonDecoder;
import com.algomi.obsidian.test.util.JacksonEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Heartbeat;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.*;

import javax.inject.Inject;
import java.io.IOException;

@ManagedService(path = AtmosphereService.PATH)
public class AtmosphereService {
    private final static Log logger = LogFactory.getLog(AtmosphereService.class);

    public static final String PATH = "/async/subscribe";

    @Inject
    private BroadcasterFactory factory;

    @Inject
    private AtmosphereResourceFactory resourceFactory;

    @Inject
    private MetaBroadcaster metaBroadcaster;


    @Heartbeat
    public void onHeartbeat(final AtmosphereResourceEvent event) {
        logger.info("Heartbeat send by " + event.getResource());
    }

    /**
     * Invoked when the connection as been fully established and suspended, e.g ready for receiving messages
     */
    @Ready
    public void onReady(final AtmosphereResource r) {
        logger.info("Browser connected - " + r.uuid());
        logger.info("BroadcasterFactory used " + factory.getClass().getName());
    }

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     */
    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser unexpectedly disconnected - " + event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser closed the connection - " + event.getResource().uuid());
        }
    }

    /**
     * Simple annotated class that demonstrate how {@link org.atmosphere.config.managed.Encoder} and {@link org.atmosphere.config.managed.Decoder
     * can be used.
     */
    @org.atmosphere.config.service.Message(encoders = {JacksonEncoder.class}, decoders = {JacksonDecoder.class})
    public Message onMessage(Message message) throws IOException {
        logger.info("Message sent: " + message.getAuthor() + " - " + message.getMessage());
        return message;
    }
}
