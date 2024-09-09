//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.OnsaleDto;
import cn.edu.xmu.oomall.order.dao.openfeign.dto.FullProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "goods-service")
public interface GoodsDao {

    @GetMapping("/shops/{shopId}/onsales/{id}")
    InternalReturnObject<OnsaleDto> getOnsaleById(@PathVariable Long shopId, @PathVariable Long id);

    @GetMapping("/product/{id}")
    InternalReturnObject<FullProductDto> getProductById(@PathVariable Long id);

    @PostMapping("/shops/{shopId}/products/{id}/onsales")
    void updateOnsale(@PathVariable Long shopId, @PathVariable Long id, @RequestBody OnsaleDto onsaleDto);

}
