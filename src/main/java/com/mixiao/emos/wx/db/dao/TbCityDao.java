package com.mixiao.emos.wx.db.dao;

import com.mixiao.emos.wx.db.pojo.TbCity;

public interface TbCityDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbCity record);

    int insertSelective(TbCity record);

    TbCity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbCity record);

    int updateByPrimaryKey(TbCity record);
}