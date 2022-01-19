package me.albert.amazingbot.bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.utils.BotConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Bot {
    private static final AtomicBoolean close = new AtomicBoolean(false);
    private static final AtomicBoolean starting = new AtomicBoolean(false);
    private static final AtomicLong startingTime = new AtomicLong();
    private static Boolean connected = false;
    public static net.mamoe.mirai.Bot bot = null;
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(8,Integer.MAX_VALUE,Long.MAX_VALUE, TimeUnit.MINUTES,new LinkedBlockingDeque<>());
    public static void start(long qq ,String password) {
        if (starting.get() && System.currentTimeMillis() - startingTime.get() < 1000 * 10) {
            System.out.println("§c机器人正在启动中,请稍后再试");
            return;
        }
        starting.set(true);
        startingTime.set(System.currentTimeMillis());
        executor.execute(() -> {
            if (bot != null && close.get()) {
                close.set(false);
                bot.close(new Throwable());
            }
            // 使用自定义的配置
            BotConfiguration configuration = new BotConfiguration() {
                {
                    fileBasedDeviceInfo("deviceInfo.json");
                }
            };
            //删除缓存
            configuration.setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);

            bot = BotFactory.INSTANCE.newBot(qq, password, configuration);
            close.set(true);
            bot.login();
            connected = true;
            bot.getEventChannel().subscribeAlways(net.mamoe.mirai.event.Event.class, event -> {
                if (event instanceof NewFriendRequestEvent) {
                    return;
                }
                if (event instanceof MemberJoinEvent) {
                    return;
                }
                if (event instanceof MemberJoinRequestEvent) {
                    return;
                }
                if (event instanceof MessageEvent) {
                    if (event instanceof GroupMessageEvent) {
                    }

                    if (event instanceof FriendMessageEvent) {
                    }

                    if (event instanceof GroupTempMessageEvent) {
                    }
                    MessageEvent messageEvent = (MessageEvent) event;
                }
            });
            starting.set(false);
        });
    }
    public static void sleep(@NotNull long mic){
        try {
            Thread.sleep(mic);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static Boolean getConnected() {
        return connected;
    }
    public static void sendGroupMsg(String groupID, String msg) {
        System.out.println(msg);
        executor.execute( () -> {
            try {
                bot.getGroupOrFail(Long.parseLong(groupID)).sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void sendPrivateMsg(String userID, String msg) {
        executor.execute(() -> {
            try {
                bot.getFriendOrFail(Long.parseLong(userID)).sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public static void changeTitle(Long groupID, Long userID, String title) {
        executor.execute(() -> {
            try {
                bot.getGroupOrFail(groupID).get(userID).setNameCard(title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Nullable
    public static Group getGroup(Long groupID) {
        try {
            return bot.getGroupOrFail(groupID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Nullable
    public static Friend getFriend(Long userID) {
        try {
            return bot.getFriendOrFail(userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void setConnected(Boolean connected) {
        Bot.connected = connected;
    }
}