package com.atguigu.dao;

import com.atguigu.pojo.Member;

public interface MemberDao {

    void add(Member member);

    Member getMemberByTelephone(String telephone);

    int findMemberCountByMonth(String month);

    int getTodayNewMember(String today);

    int getTotalMember();

    int getThisWeekAndMonthNewMember(String weekMonday);
}
