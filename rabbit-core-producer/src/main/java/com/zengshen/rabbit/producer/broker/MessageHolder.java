package com.zengshen.rabbit.producer.broker;

import com.google.common.collect.Lists;
import com.zengshen.rabbit.api.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author word
 */
public class MessageHolder {

    private List<Message> messageList = Lists.newArrayList();


    /**
     * 作用是屏蔽一些无关紧要的警告
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final ThreadLocal<MessageHolder> holder = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new MessageHolder();
        }
    };

    public static void add(Message message) {
        holder.get().messageList.add(message);
    }

    public static List<Message> clear() {
        ArrayList<Message> messages = Lists.newArrayList(holder.get().messageList);
        holder.remove();
        return messages;
    }
}
