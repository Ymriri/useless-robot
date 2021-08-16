package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.mapper.DefaultValueMapper;
import pers.wuyou.robot.mapper.ListenerValuesMapper;
import pers.wuyou.robot.service.DefaultValueService;
import pers.wuyou.robot.web.vo.DefaultValueVO;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-04
 */
@Service
@Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class DefaultValueServiceImpl extends ServiceImpl<DefaultValueMapper, DefaultValue> implements DefaultValueService {
    private final DefaultValueMapper defaultValueMapper;
    private final ListenerValuesMapper listenerValuesMapper;

    @Autowired
    public DefaultValueServiceImpl(DefaultValueMapper defaultValueMapper, ListenerValuesMapper listenerValuesMapper) {
        this.listenerValuesMapper = listenerValuesMapper;
        this.defaultValueMapper = defaultValueMapper;
    }

    @Override
    public List<DefaultValueVO> selectPage(Page<DefaultValueVO> pageParam, String name) {
        long st = System.currentTimeMillis();
        List<DefaultValueVO> defaultValue = defaultValueMapper.getDefaultValue(pageParam, name);
        for (DefaultValueVO defaultValueVO : defaultValue) {
            System.out.println(System.currentTimeMillis() - st);
            defaultValueVO.setValues(listenerValuesMapper.selectByDefaultValueId(defaultValueVO.getId()));
        }
        System.out.println(System.currentTimeMillis() - st);
        return defaultValue;
    }
}
