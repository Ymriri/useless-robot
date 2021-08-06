package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import pers.wuyou.robot.entity.BootStateInfo;
import pers.wuyou.robot.mapper.BootStateInfoMapper;
import pers.wuyou.robot.service.BootStateInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-05
 */
@Service
public class BootStateInfoServiceImpl extends ServiceImpl<BootStateInfoMapper, BootStateInfo> implements BootStateInfoService {
    private final BootStateInfoMapper bootStateInfoMapper;

    @Autowired
    public BootStateInfoServiceImpl(BootStateInfoMapper bootStateInfoMapper) {
        this.bootStateInfoMapper = bootStateInfoMapper;
    }

    @Override
    public void bootAndShutDown(String groupCode, boolean state) {
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
