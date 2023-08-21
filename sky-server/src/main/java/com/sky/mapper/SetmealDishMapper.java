package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);



    /**
     *
     * @param dishDTO
     */
    @AutoFill(OperationType.UPDATE)
    @Update("update ")
    void updateByDish(DishDTO dishDTO);
}
