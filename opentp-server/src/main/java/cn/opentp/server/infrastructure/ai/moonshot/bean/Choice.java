package cn.opentp.server.infrastructure.ai.moonshot.bean;

public class Choice {

    private int index;
    private Delta delta;
    private String finish_reason;
    private Usage usage;
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

    public void setDelta(Delta delta) {
        this.delta = delta;
    }
    public Delta getDelta() {
        return delta;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
    public String getFinish_reason() {
        return finish_reason;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
    public Usage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "index=" + index +
                ", delta=" + delta +
                ", finish_reason='" + finish_reason + '\'' +
                ", usage=" + usage +
                '}';
    }
}
