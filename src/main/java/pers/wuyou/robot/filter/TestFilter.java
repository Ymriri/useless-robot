package pers.wuyou.robot.filter;

import pers.wuyou.robot.core.FilterData;
import pers.wuyou.robot.core.ListenerFilter;

import java.util.Objects;

public class TestFilter implements ListenerFilter {
    @Override
    public boolean test(FilterData filterData){
        return Objects.requireNonNull(filterData.getGroupMsg().getMsg()).contains("a");
    }

}
