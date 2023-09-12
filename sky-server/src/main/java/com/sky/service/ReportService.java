package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计指定时间内的营业额
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    /**
     *统计指定时间的订单数量
     * @return
     */
    OrderReportVO getOrderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);

    /**
     * 统计销量排名前十
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end);
}
