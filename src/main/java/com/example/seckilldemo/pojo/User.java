package com.example.seckilldemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author sht
 * @since 2023-02-12
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，手机号码
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("nickname")
    private String nickname;

    /**
     * MD5(MD5(pass明文+固定salt)+salt)
     */
    @TableField("password")
    private String password;

    @TableField("salt")
    private String salt;

    /**
     * 头像
     */
    @TableField("head")
    private String head;

    /**
     * 注册时间
     */
    @TableField("register_date")
    private Date registerDate;

    /**
     * 最后一次登录时间
     */
    @TableField("last_login_date")
    private Date lastLoginDate;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;


}
