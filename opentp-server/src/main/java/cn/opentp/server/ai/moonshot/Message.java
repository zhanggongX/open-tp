package cn.opentp.server.ai.moonshot;

public class Message {

    public Message() {
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    private String role;

    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}