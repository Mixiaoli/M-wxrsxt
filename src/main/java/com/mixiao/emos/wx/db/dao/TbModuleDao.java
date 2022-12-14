package com.mixiao.emos.wx.db.dao;

import com.mixiao.emos.wx.db.pojo.TbModule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbModuleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbModule record);

    int insertSelective(TbModule record);

    TbModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbModule record);

    int updateByPrimaryKey(TbModule record);
}