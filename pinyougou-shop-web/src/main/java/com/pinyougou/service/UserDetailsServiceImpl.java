package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //先到数据库查一遍
        TbSeller tbSeller = sellerService.findOne(username);
        if (tbSeller != null) {
            if (tbSeller.getStatus().equals("1")) {
                return new User(username, tbSeller.getPassword(), list);
            } else {
                return null;
            }
        } else {
            return null;
        }


    }


}
