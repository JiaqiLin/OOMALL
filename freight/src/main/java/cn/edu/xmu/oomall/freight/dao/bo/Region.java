package cn.edu.xmu.oomall.freight.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.freight.dao.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ClassName Region
 * Description  TODO
 *
 * @author Mr_X
 * @version 1.0
 * @date 2022/11/28 18:39
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Region extends OOMallObject implements Serializable {
    /**
     * 有效
     */
    public static Byte VALID = 0;
    /**
     * 暂停
     */
    public static Byte SUSPENDED = 1;
    /**
     * 废弃
     */
    public static Byte ABANDONED = 2;

    /**
     * 上级地区id
     */
    private Long pid;

//    private Integer id;

    /**
     * 地区名称
     */
    private String name;

    /**
     * 地区状态
     */
    private Byte status;

    /**
     * 地区级别
     */
    private Byte level;

    /**
     * 地区简称
     */
    private String shortName;

    /**
     * 地区全称
     */
    private String mergerName;

    /**
     * 地区拼音
     */
    private String pinyin;

    /**
     * 地区经度
     */
    private BigDecimal lng;

    /**
     * 地区纬度
     */
    private BigDecimal lat;

    /**
     * 地区码
     */
    private String areaCode;

    /**
     * 邮政编码
     */
    private String zipCode;

    /**
     * 电话区号
     */
    private String cityCode;

    @JsonIgnore
    @ToString.Exclude
    private Region parentRegion;
    @Setter
    @JsonIgnore
    @ToString.Exclude
    private RegionDao regionDao;

    public Region getParentRegion() {
        if (!this.pid.equals(Long.valueOf("-1")) && null == this.parentRegion && null != this.regionDao) {
            this.parentRegion = this.regionDao.findById(pid);
        }
        return this.parentRegion;
    }
}
