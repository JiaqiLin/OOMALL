package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.freight.dao.bo.Logistics;
import cn.edu.xmu.oomall.freight.mapper.generator.LogisticsPoMapper;
import cn.edu.xmu.oomall.freight.mapper.generator.po.LogisticsPo;
import cn.edu.xmu.oomall.freight.mapper.generator.po.LogisticsPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;


@Repository
public class LogisticsDao {
    private static final Logger logger = LoggerFactory.getLogger(LogisticsDao.class);

    private LogisticsPoMapper logisticsPoMapper;
    private RedisUtil redisUtil;
    @Value("${oomall.freight.logistics.timeout}")
    private long timeout;

    public static final String KEY = "L%d";


    @Autowired
    public LogisticsDao(LogisticsPoMapper logisticsPoMapper, RedisUtil redisUtil){
        this.logisticsPoMapper=logisticsPoMapper;
        this.redisUtil=redisUtil;
    }

    private Logistics getBo(LogisticsPo po, String redisKey){
        Logistics ret = cloneObj(po, Logistics.class);
        if (null != redisKey) {
            redisUtil.set(redisKey, ret, timeout);
        }
        return ret;
    }

    public Logistics findById(Long id) throws RuntimeException {
        Logistics ret = null;
        if (null != id) {
            String key = String.format(KEY, id);
            if (redisUtil.hasKey(key)) {
                ret = (Logistics) redisUtil.get(key);
            } else {
                LogisticsPo po = logisticsPoMapper.selectByPrimaryKey(id);
                if(po!=null){
                    ret = this.getBo(po, key);
                    redisUtil.set(key, ret, -1);
                }else{
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"logistics",id));
                }

            }
        }
        logger.debug("findObjById: id = " + id + " ret = " + ret);
        return ret;
    }

    public List<Logistics> retrieveAll(){
        List<Logistics> ret = null;
        LogisticsPoExample example = new LogisticsPoExample();
        LogisticsPoExample.Criteria criteria=example.createCriteria();
        List<LogisticsPo> poList= logisticsPoMapper.selectByExample(example);
        if(poList.size()>0){
            ret=poList.stream().map(po->getBo(po,null)).collect(Collectors.toList());
        }
        return ret;
    }
}
