import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.bot.BotFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static List<BotFrame> botfs = new ArrayList<>();
    public static List<Bot> bots = new ArrayList<>();
    public static long group = 000000L;
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(8,Integer.MAX_VALUE,Long.MAX_VALUE, TimeUnit.DAYS,new LinkedBlockingDeque<>());
    public static ThreadPoolExecutor executor2 = new ThreadPoolExecutor(8,Integer.MAX_VALUE,Long.MAX_VALUE, TimeUnit.DAYS,new LinkedBlockingDeque<>());
    public static void init(){
        for (BotFrame fs:botfs){
            Bot bot = new Bot();
            bot.start(fs.qqID, fs.qqPassword);
            bots.add(bot);
        }
    }
    public static void prob(String message,int threads,int count){
        for(int i=0;i<threads;i++){
            executor.execute(()->{
                for (Bot bot : bots){
                    executor2.execute(()-> {
                        System.out.println("Probing...");
                        while (!bot.getConnected()) {
                            bot.sleep(1);
                        }
                        for (int a=0;a<count;a++){
                            System.out.println("Sending message...");
                            bot.getGroup(group).sendMessage(message);
                        }
                    });
                }
            });
        }
    }
   public static void main(String[] strings) throws InterruptedException {
        group=563566605L;
        botfs.add(new BotFrame("",2));
        init();
        Thread.sleep(5000);
        prob("test",1,4);
   }
}
