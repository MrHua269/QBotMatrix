package org.novau233.qbm.manager;

import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.novau233.qbm.bot.BotConfigEntry;
import org.novau233.qbm.bot.BotEntry;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BotManager {
    public static final List<BotConfigEntry> botEntries = new Vector<>();
    public static final List<Bot> bots = new Vector<>();
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Executor executor = Executors.newCachedThreadPool();

    public static void init(){
        try {
            File file = new File("bots.json");
            if (!file.exists()) {
                LOGGER.error("Bot config not found!Exiting..");
                System.exit(0);
            }
            FileInputStream stream = new FileInputStream(file);
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            BotConfigEntryArray array = BotConfigEntryArray.botConfigEntryArrayFromString(new String(buffer));
            CountDownLatch latch = new CountDownLatch(array.entries.length);
            for (BotConfigEntry configEntry : array.entries){
                executor.execute(()->{
                    try{
                        BotEntry entry = new BotEntry() {
                            @Override
                            public void processEvent(Event event) {}
                        };
                        entry.runBot(configEntry);
                        bots.add(entry.getBot());
                        botEntries.add(configEntry);
                    }finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            stream.close();
        }catch (Exception e){
            LOGGER.error(e);
        }
    }

    private static class BotConfigEntryArray{
        private static final Gson GSON = new Gson();
        public BotConfigEntry[] entries;

        public String getJson(){
            return GSON.toJson(this);
        }

        public static BotConfigEntryArray botConfigEntryArrayFromString(String jsonIn){
            return GSON.fromJson(jsonIn,BotConfigEntryArray.class);
        }
    }
}
