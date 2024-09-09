package cn.edu.xmu.oomall.freight.service.responsibilityChain;

import cn.edu.xmu.oomall.freight.dao.bo.Warehouse;

import java.util.List;

public abstract class Handler {
    protected Handler next;

    public void setNext(Handler next) {
        this.next = next;
    }

    public abstract List<Warehouse> handle(List<Warehouse> warehouses,Long regionId,Long shopId);
}
