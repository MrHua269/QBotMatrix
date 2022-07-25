package org.novau233.qbm.bot;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.utils.BotConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BotEntry {
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile net.mamoe.mirai.Bot bot;
    private BotConfigEntry configEntry;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public void runBot(BotConfigEntry configEntry){
        this.configEntry = configEntry;
        try {
            File infoFile = new File("deviceInfos");
            if (!infoFile.exists()){
                infoFile.mkdir();
            }
        }catch (Exception e){
            LOGGER.error(e);
        }
        BotConfiguration configuration = new BotConfiguration() {
            {
                fileBasedDeviceInfo("deviceInfos\\deviceInfo-"+configEntry.getQid()+".json");
            }
        };
        configuration.setProtocol(configEntry.getProtocol());
        configuration.noBotLog();
        configuration.noNetworkLog();
        this.bot = BotFactory.INSTANCE.newBot(configEntry.getQid(),configEntry.getPassword(), configuration);
        this.bot.login();
        this.bot.getEventChannel().subscribeAlways(net.mamoe.mirai.event.Event.class,this::processEvent);
        this.connected.set(true);
    }

    public abstract void processEvent(Event event);

    public Bot getBot(){
        return this.bot;
    }

    public boolean isConnected(){
        return this.connected.get();
    }

    public long getCurrentQid(){
        return this.configEntry.getQid();
    }
}
