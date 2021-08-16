package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pers.wuyou.robot.common.GlobalVariable;
import pers.wuyou.robot.entity.BootStateInfo;
import pers.wuyou.robot.mapper.BootStateInfoMapper;
import pers.wuyou.robot.service.BootStateInfoService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-05
 */
@Service
@Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class BootStateInfoServiceImpl extends ServiceImpl<BootStateInfoMapper, BootStateInfo> implements BootStateInfoService {
    private final BootStateInfoMapper bootStateInfoMapper;

    @Autowired
    public BootStateInfoServiceImpl(BootStateInfoMapper bootStateInfoMapper) {
        this.bootStateInfoMapper = bootStateInfoMapper;
    }

    @Override
    public void bootAndShutDown(String groupCode, boolean state) {
        GlobalVariable.getBOOT_MAP().put(groupCode, state);
        QueryWrapper<BootStateInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("group_code", groupCode);
        BootStateInfo bootStateInfo = bootStateInfoMapper.selectOne(wrapper);
        if (bootStateInfo == null) {
            bootStateInfo = new BootStateInfo();
            bootStateInfo.setGroupCode(groupCode);
            bootStateInfo.setState(state);
            bootStateInfoMapper.insert(bootStateInfo);
            return;
        }
        bootStateInfo.setState(state);
        bootStateInfoMapper.update(bootStateInfo, wrapper);

    }

}
