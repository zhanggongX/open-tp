package cn.opentp.server.network.restful.mapping;

public class EndpointMappingParam {

    private String name;

    private Class<?> dataType;

    private EndPointMappingParamType type;

    private boolean required = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public EndPointMappingParamType getType() {
        return type;
    }

    public void setType(EndPointMappingParamType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
