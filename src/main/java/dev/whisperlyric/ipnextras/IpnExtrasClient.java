package dev.whisperlyric.ipnextras;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side entry point for IPN Extras mod.
 */
public class IpnExtrasClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ipnextras");

    @Override
    public void onInitializeClient() {
        LOGGER.info("IPN Extras initialized");
        LOGGER.info("This mod enhances IPN compatibility with GCA fake player containers");
    }
}