package com.atguigu.dao;

import com.atguigu.pojo.TravelGroup;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TravelGroupDao {
    void add(TravelGroup travelGroup);

    void addTravelGroupAndTravelItem(Map<String, Integer> paramData);

    Page findPage(@Param("queryString") String queryString);

    TravelGroup getById(Integer id);

    List<Integer> getTravelitemIdsByTravelGroupId(Integer travelGroupId);

    void edit(TravelGroup travelGroup);

    List<TravelGroup> findAll();

    /**
     * 帮助封装套餐对象的travelGroups属性方法
     * @return
     */
    List<TravelGroup> findTravelGroupById(Integer id);

    long findCountByTravelgroupId(Integer id);

    void deleteTravelGroup(Integer travelGroupId);

    void delete(Integer travelGroupId);
}
