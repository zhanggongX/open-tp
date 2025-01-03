package cn.opentp.server.network.restful;

import java.util.List;

public class PageResult<T> {

    private List<T> list;
    private Integer total;

    public PageResult() {
    }

    public PageResult(List<T> list) {
        this.list = list;
        this.total = list == null ? 0 : list.size();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
