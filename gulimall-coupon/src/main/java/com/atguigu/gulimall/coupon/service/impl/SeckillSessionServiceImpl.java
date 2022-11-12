package com.atguigu.gulimall.coupon.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atguigu.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.atguigu.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.coupon.dao.SeckillSessionDao;
import com.atguigu.gulimall.coupon.entity.SeckillSessionEntity;
import com.atguigu.gulimall.coupon.service.SeckillSessionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaysSession() {
        // 1. 计算最近三天，查出这三天的活动
        String startTime = this.startTime();
        String endTime = this.endTime();
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time",startTime,endTime));

        // 2. 查出这三天活动关联的商品
        if(list != null && list.size() != 0){
            List<SeckillSessionEntity> collect = list.stream().map(session -> {
                Long id = session.getId();
                List<SeckillSkuRelationEntity> promotion_session_id = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                session.setRelationSkus(promotion_session_id);
                return session;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    private String startTime(){
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
//        System.out.println(start + "~~~~~~~~~~~~~~~~~~~~");
        start.format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        return start.toString();
    }

    private String endTime(){
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalDateTime end = LocalDateTime.of(localDate, LocalTime.MAX);
//        System.out.println(end + "~~~~~~~~~~~~~~~~~~~~");
        end.format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        return end.toString();
    }
}