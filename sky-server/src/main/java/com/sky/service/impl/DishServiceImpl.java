package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品和对应的口味数据
     * @param dishDTO
     */
    @Transactional//事务注解，因为有两次操作的，一次修改菜品表，一次是口味表
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据，id封装
        dishMapper.insert(dish);
        //获取insert语句生产的主键值
        Long id = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0) {
            //设置DishID因为新增菜品，还没增加，前端肯定返回不了对应的DishID
            //而我们现在已经添加了菜品，就能获取对应的id值了（mapper的自动赋值）
            for (DishFlavor flavor:flavors
                 ) {
                flavor.setDishId(id);
            }
        dishFlavorMapper.insertBatch(flavors);

        }
    }

    /**
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //select * from employee limit 0,10(开始记录数，一共几条) 页码和一页记录数
        //对应关系limit (页码-1)*每页记录数，一页记录数,通过pageHelper
        //自己学一下PageHelper插件
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();

        List<DishVO> result = page.getResult();

        return new PageResult(total,result);

    }

    /**
     *  菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除--查询启售状态
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品出于起售中,不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否被套餐关联-关联也不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null&&setmealIds.size()>0){
            //当前菜品被套餐关联了,不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中菜品数据 和 删除菜品表中口味数据
        for (Long id : ids) {
            dishMapper.deleteById(id);

            dishFlavorMapper.deleteByDishId(id);
        }



    }

    /**
     * 根据id获取菜品对象
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品
        Dish byId = dishMapper.getById(id);
        //查询菜品id查询对应口味
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        //不用根据菜品的id查询对应的分类名，这个因为分页查询数据已经回显了，前端给我们进行了操作
       //如果做的话，新增一个CategoryMapper（写根据dihsid查询分类名的方法）


        DishVO dishVO = new DishVO();

        BeanUtils.copyProperties(byId,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;

    }

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {//这里为什么不修改分类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品基本信息
        dishMapper.update(dish);
        //口味对应的表，是一个口味对应一条数据，不是一个update就能解决多条数据的！！！（你增加一个原来的行数肯定不够）
        //可能增加口味，可能修改口味也可能删除口味，这样的话就多种情况了，所以我们先删除对应菜品所有口味，再增添
        //需要用到事务
        //删除原有口味，菜品批量删除写过
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据,之前插入写过

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0) {
            //这里我有点蒙，id按说修改是不用改的，但老师还是说可能是新增，那就这样写吧
            for (DishFlavor flavor:flavors
            ) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorMapper.insertBatch(flavors);


        }

    }
}
