package pers.wuyou.robot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pers.wuyou.robot.entity.ListenerEntity;
import pers.wuyou.robot.mapper.ListenerMapper;
import pers.wuyou.robot.service.ListenerService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2021-08-03
 */
@Service
@Transactional(propagation = Propagation.NESTED, isolation = Isolation.DEFAULT, rollbackFor = Exception.class)
public class ListenerServiceImpl extends ServiceImpl<ListenerMapper, ListenerEntity> implements ListenerService {

}
