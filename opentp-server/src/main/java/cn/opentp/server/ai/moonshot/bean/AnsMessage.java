/**
 * Copyright 2024 json.cn
 */
package cn.opentp.server.ai.moonshot.bean;

import java.util.List;

/**
 * Auto-generated: 2024-08-28 14:13:31
 *
 * @author json.cn (i@json.cn)
 */
public class AnsMessage {

    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private String system_fingerprint;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObject() {
        return object;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setSystem_fingerprint(String system_fingerprint) {
        this.system_fingerprint = system_fingerprint;
    }

    public String getSystem_fingerprint() {
        return system_fingerprint;
    }

    @Override
    public String toString() {
        return "AnsMessage{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", choices=" + choices +
                ", system_fingerprint='" + system_fingerprint + '\'' +
                '}';
    }
}