package com.sanywind.batchinlfluxdb.util;

public class Message {
	private String messageText;
    private MsgType type;

    private Message(String messageText, MsgType type) {
        super();
        this.messageText = messageText;
        this.type = type;
    }

    public static Message error(String msg) {
        return new Message(msg, MsgType.ERROR);
    }

    public static Message warn(String msg) {
        return new Message(msg, MsgType.WARN);
    }

    public static Message info(String msg) {
        return new Message(msg, MsgType.INFO);
    }

    public static Message question(String msg) {
        return new Message(msg, MsgType.QUESTION);
    }

    public String getMessageText() {
        return messageText;
    }

    public MsgType getType() {
        return type;
    }

    public String getTypeDes() {
        return type.getDes();
    }

    public String getTypeChineseDes() {
        return type.getChineseDes();
    }

    private enum MsgType {
        ERROR("error", "错误"), WARN("warn", "警告"), INFO("info", "信息"), QUESTION("question", "问题");

        private String des;
        private String chineseDes;

        MsgType(String des, String chineseDes) {
            this.des = des;
            this.chineseDes = chineseDes;
        }

        public String getDes() {
            return des;
        }

        public String getChineseDes() {
            return chineseDes;
        }
    }
}
