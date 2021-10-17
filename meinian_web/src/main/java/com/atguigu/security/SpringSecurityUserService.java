package com.atguigu.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.pojo.Permission;
import com.atguigu.pojo.Role;
import com.atguigu.pojo.User;
import com.atguigu.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component("SpringSecurityUserService")
public class SpringSecurityUserService  implements UserDetailsService {

    @Reference
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            //1.查询用户信息，以及用户对应角色，以及角色对应权限
        User user = userService.findUserByUsername(username);

        if (user == null){ //不存在用户
            return null; //返回null，框架会抛出异常，
        }

        //2.构建权限集合
        Set<GrantedAuthority> authorities = new HashSet<>();

        Set<Role> roles = user.getRoles(); //集合数据 由 RoleDao帮忙查询得到
        for (Role role :roles){
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions ){
                authorities.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        org.springframework.security.core.userdetails.User  securityUser = new
                org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), authorities);
        return securityUser;  //框架提供的user 实现了UserDetails 接口
    }
}
