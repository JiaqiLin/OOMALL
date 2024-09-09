package cn.edu.xmu.oomall.freight.service;

import cn.edu.xmu.oomall.freight.dao.RegionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionService {

    private RegionDao regionDao;
    @Autowired
    public RegionService(RegionDao regionDao){
        this.regionDao=regionDao;
    }



}
