package com.atguigu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.constant.MessageConstant;
import com.atguigu.dao.MemberDao;
import com.atguigu.dao.OrderDao;
import com.atguigu.dao.OrderSettingDao;
import com.atguigu.entity.Result;
import com.atguigu.pojo.Member;
import com.atguigu.pojo.Order;
import com.atguigu.pojo.OrderSetting;
import com.atguigu.service.OrderService;
import com.atguigu.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    MemberDao memberDao;

    @Autowired
    OrderSettingDao orderSettingDao;


    @Override
    public Result saveOrder(Map map) throws Exception {

//        1. 判断当前的日期是否可以预约(根据orderDate查询t_ordersetting, 能查询出来可以预约;查询不出来,不能预约)
        String orderDate =(String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        OrderSetting orderSetting =  orderSettingDao.getOrderSettingByOrderDate(date);
        if (orderSetting ==null){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }else{
//        2. 判断当前日期是否预约已满(判断reservations（已经预约人数）是否等于number（最多预约人数）)
            //如果有，说明可以预约
            //可预约人数
            int number = orderSetting.getNumber();
            //以预约人数
            int reservations = orderSetting.getReservations();
            if (reservations>=number){
                return new Result(false,MessageConstant.ORDER_FULL);
            }
        }
//        3. 判断是否是会员(根据手机号码查询t_member)
        //获取手机号
        String telephone = (String) map.get("telephone");
        //根据手机号查询是否是会员
        Member member = memberDao.getMemberByTelephone(telephone);
//                - 如果是会员(能够查询出来), 防止重复预约(根据member_id,orderDate,setmeal_id查询t_order)

        if (member != null){
            //获取套餐id
            int setmealId = Integer.parseInt((String) map.get("setmealId"));
            //获取会员id
            Integer memberId = member.getId();
            Order order = new Order(memberId,date,null,null,setmealId);
            //根据预约信息查询是否已经预约
            List<Order> list = orderDao.findByCondition(order);
            //判断是否已经预约，list不等于null，说明已经预约，不能重复预约
            if (list!=null && list.size()>0){
                //已经预约，不能重复预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }else {
//                - 如果不是会员(不能够查询出来) ,自动注册为会员(直接向t_member插入一条记录)
            member = new Member();
            member.setName((String) map.get("name"));
            member.setSex((String) map.get("sex"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setRegTime(new Date()); //注册会员时间，当前时间
            memberDao.add(member);
        }
//        4.进行预约
//     - 向t_order表插入一条记录
//     - t_ordersetting表里面预约的人数reservations+1
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);


        //5.可以进行预约，向预约表中添加一条数据
        Order order = new Order();
        order.setMemberId(member.getId());//会员id
        order.setOrderDate(date);
        order.setOrderType(order.ORDERTYPE_WEIXIN);
        order.setOrderStatus(order.ORDERSTATUS_YES);
        order.setSetmealId(Integer.parseInt((String)map.get("setmealId")));
        orderDao.add(order);

        return new Result(true,MessageConstant.ORDER_SUCCESS,order);
    }

    @Override
    public Map<String, Object> findById(Integer orderId) {
        return orderDao.findById(orderId);
    }
}
