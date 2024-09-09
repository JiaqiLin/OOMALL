package cn.edu.xmu.oomall.freight.controller;


import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.freight.controller.vo.ConfirmExpressVo;
import cn.edu.xmu.oomall.freight.controller.vo.ExpressVo;
import cn.edu.xmu.oomall.freight.dao.bo.Express;
import cn.edu.xmu.oomall.freight.service.ExpressService;
import cn.edu.xmu.oomall.freight.service.dto.ExpressDto;
import cn.edu.xmu.oomall.freight.service.dto.SimpleExpressDto;
import org.apache.ibatis.javassist.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/internal",produces = "application/json;charset=UTF-8")
public class InternalExpressController {

    private final Logger logger = LoggerFactory.getLogger(InternalExpressController.class);

    private ExpressService expressService;

    @Autowired
    public InternalExpressController(ExpressService expressService){
        this.expressService=expressService;
    }

    @PostMapping("/shops/{shopId}/packages")
    public ReturnObject createExpress(@PathVariable Long shopId,@Validated @RequestBody ExpressVo expressVo, @LoginUser UserDto user){
        Express express=new Express(expressVo);
        SimpleExpressDto simpleExpressDto=null;
        if(express.getShopLogisticsId()==0){
            simpleExpressDto=expressService.createExpressByPriority(shopId,express,user);
        }
        else {
            simpleExpressDto=expressService.createExpress(shopId,express,user);
        }
        return new ReturnObject(ReturnNo.CREATED,simpleExpressDto);
    }

    @GetMapping("/shops/{shopId}/packages")
    public ReturnObject searchExpressByBillCode(@PathVariable Long shopId,@RequestParam String billCode,@LoginUser UserDto user){
        return new ReturnObject(expressService.searchExpressByBillCode(shopId,billCode,user));
    }

    @GetMapping("/packages/{id}")
    public ReturnObject searchExpressByExpressId(@PathVariable Long id,@LoginUser UserDto user){
        return new ReturnObject(expressService.searchExpressByExpressId(id,user));
    }

    @PutMapping("/shops/{shopId}/packages/{id}/confirm")
    public ReturnObject confirmExpressByExpressId(@PathVariable Long shopId, @PathVariable Long id, @RequestBody ConfirmExpressVo confirmExpressVo, @LoginUser UserDto user){
        expressService.confirmExpressByExpressId(shopId,id,confirmExpressVo.getStatus(),user);
        return new ReturnObject();
    }

    @PutMapping("/shops/{shopId}/packages/{id}/cancel")
    public ReturnObject cancelExpressByExpressId(@PathVariable Long shopId,@PathVariable Long id,@LoginUser UserDto user){
        expressService.cancelExpressByExpressId(shopId,id,user);
        return new ReturnObject();
    }


}
