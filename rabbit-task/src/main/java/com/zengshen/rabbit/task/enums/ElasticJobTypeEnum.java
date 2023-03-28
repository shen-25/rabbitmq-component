package com.zengshen.rabbit.task.enums;

public enum ElasticJobTypeEnum {
    /**
     * 消息类型枚举
     */
    SIMPLE("SimpleJob", "简单消息"),
    DATAFLOW("DataflowJob", "流式消息"),
    SCRIPT("ScriptJob", "脚本类型消息");

    private String type;

    private String desc;

    private ElasticJobTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
