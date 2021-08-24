package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pers.wuyou.robot.common.StringPool;
import pers.wuyou.robot.entity.DefaultValue;
import pers.wuyou.robot.entity.ListenerValues;
import pers.wuyou.robot.mapper.DefaultValueMapper;
import pers.wuyou.robot.mapper.ListenerValuesMapper;
import pers.wuyou.robot.service.ListenerValuesService;
import pers.wuyou.robot.web.vo.ListenerValuesVO;

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
public class ListenerValuesServiceImpl extends ServiceImpl<ListenerValuesMapper, ListenerValues> implements ListenerValuesService {
    private final ListenerValuesMapper listenerValuesMapper;
    private final DefaultValueMapper defaultValueMapper;

    @Autowired
    public ListenerValuesServiceImpl(ListenerValuesMapper listenerValuesMapper, DefaultValueMapper defaultValueMapper) {
        this.listenerValuesMapper = listenerValuesMapper;
        this.defaultValueMapper = defaultValueMapper;
    }

    @Override
    public void saveByName(ListenerValuesVO listenerValues) {
        QueryWrapper<DefaultValue> wrapper = new QueryWrapper<>();
        wrapper.eq(StringPool.NAME, listenerValues.getName());
        DefaultValue defaultValue = defaultValueMapper.selectOne(wrapper);
        listenerValuesMapper.insert(new ListenerValues(defaultValue.getId(), listenerValues.getValue()));
    }
}
