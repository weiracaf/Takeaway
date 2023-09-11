package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 统计指定时间内的营业额
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        //存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        //范围是[begin,end)
        dateList.add(begin);
        while(!begin.equals(end)){//相同是最后一天，也有isbefore这种方法，注意plusDays是返回值
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放明天的营业额
        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //查询date日期对应的营业额，营业额值 status=已完成 的金额合计
            LocalDateTime beginOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            //select sum(Amount) from where order_time > ? and order_time < ? and status = 5
            Map map = new HashMap();
            map.put("begin",beginOfDay);
            map.put("end",endOfDay);
            map.put("status", Orders.COMPLETED);
            Double turnover =orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }


        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }
}
