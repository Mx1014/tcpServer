package cn.usr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Package: cn.usr.dto
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018/10/22 11:09
 */
@Data
@AllArgsConstructor
public class UserDTO {

    /**
     * 用户名
     */
    private final String account;

    /**
     * 用户ID
     */
    private final Integer userId;

    /**
     * 父用户名称
     */
    private final String parentAccount;

    /**
     * 父用户ID
     */
    private final Integer parentId;

}
