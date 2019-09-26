package cn.usr.dao;

import cn.usr.entity.TGroup;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Set;


/**
 * @author Administrator
 */
@Repository
public interface TGroupDao {
    /**
     * 查询设备所在分组的对应组id
     *
     * @param did
     * @return
     */
    List<String> queryTargetGroupIdsByDid(String did);


    /**
     * 查询组内设备列表
     *
     * @param gids
     * @return
     */
    Set<String> queryDidListByGroupIds(@Param("gids") List<String> gids);

}
