package com.mixiao.emos.wx.db.dao;

import com.mixiao.emos.wx.db.pojo.TbAction;

public interface TbActionDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbAction record);

    int insertSelective(TbAction record);

    TbAction selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbAction record);

    int updateByPrimaryKey(TbAction record);
}