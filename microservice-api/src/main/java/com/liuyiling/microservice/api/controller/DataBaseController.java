package com.liuyiling.microservice.api.controller;

import com.liuyiling.microservice.api.apiversion.ApiVersion;
import com.liuyiling.microservice.api.util.ResultBean;
import com.liuyiling.microservice.core.storage.dao.entity.TbEipRequirePoolDO;
import com.liuyiling.microservice.core.storage.dao.mapper.TbEipRequirePoolDOMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@RestController
@ApiVersion(1)
@RequestMapping(value = "/database")
@Api(value = "数据库接口", description = "数据库操作示例")
public class DataBaseController {

    @Autowired
    private TbEipRequirePoolDOMapper tbEipRequirePoolDOMapper;

    @ApiOperation(value = "非事务操作")
    @GetMapping(value = "/normal")
    public ResultBean<Integer> normal() {
        List<TbEipRequirePoolDO> tbEipRequirePoolDOList = tbEipRequirePoolDOMapper.selectAll();
        return new ResultBean<>(tbEipRequirePoolDOList.size());
    }

    @ApiOperation(value = "事务操作")
    @GetMapping(value = "/transactional")
    @Transactional(readOnly = false, timeout = 1000, rollbackFor = RuntimeException.class)
    public ResultBean<Integer> transactional() {
        List<TbEipRequirePoolDO> tbEipRequirePoolDOList = tbEipRequirePoolDOMapper.selectAll();
        tbEipRequirePoolDOMapper.deleteByPrimaryKey(tbEipRequirePoolDOList.get(0).getRequireId());
        tbEipRequirePoolDOList = tbEipRequirePoolDOMapper.selectAll();
        //手动触发异常
        int test = 1 / 0;
        return new ResultBean<>(tbEipRequirePoolDOList.size());
    }
}
