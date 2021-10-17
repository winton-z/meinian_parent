package com.atguigu.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.entity.PageResult;
import com.atguigu.entity.QueryPageBean;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Address;
import com.atguigu.service.AddressService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    AddressService addressService;


    @RequestMapping("/deleteById")
    public Result deleteById(Integer id){
        addressService.deleteById(id);
        return new Result(true,"已删除地址");
    }


    @RequestMapping("/addAddress")
    public Result addAddress(@RequestBody Address address){
        //System.out.println(address.toString());
        addressService.addAddress(address);
        return new Result(true,"地址保存成功");
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        PageResult pageResult = null;
        try {
            pageResult = addressService.findPage(queryPageBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageResult;
    }

    @RequestMapping("/findAllMaps")
    public Map findAllMaps(){
        Map map = new HashMap();

       List<Address> list = addressService.findAllMaps();
       //取到坐标
        List<Map> gridnMaps = new ArrayList<>();
        //取到分店名称
        List<Map> nameMaps = new ArrayList<>();
        for (Address address : list) {
            //将获取到的坐标放入到一个map中
            Map gridnMap = new HashMap();
            gridnMap.put("lng",address.getLng());
            gridnMap.put("lat",address.getLat());
            //将存放了经纬度的map放入到坐标集合中
            gridnMaps.add(gridnMap);
            Map nameMap = new HashMap();
            //将分店名称放入到分店名称的map集合中
            nameMap.put("addressName",address.getAddressName());
            nameMaps.add(nameMap);

        }

        //将取到的所有值都放入到返回前台的一个map对象中
        map.put("gridnMaps",gridnMaps);
        map.put("nameMap",nameMaps);


       return map;
    }

}
