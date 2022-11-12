package com.atguigu.gulimall.secondkill.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 秒杀场次信息
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillSessionsWithSkusVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 16:22
 */
@Data
public class SecKillSessionsWithSkusVo {

    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    List<SecKillSkuVo> relationSkus;
}
