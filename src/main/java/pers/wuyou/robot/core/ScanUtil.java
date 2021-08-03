package pers.wuyou.robot.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import pers.wuyou.robot.core.annotation.UselessScan;

/**
 * 加载指定包路径下的自定义注解实例
 *
 * @author wuyou
 */
public class ScanUtil implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
//        ClassPathScanningCandidateComponentProvider provider =
//                new ClassPathScanningCandidateComponentProvider(false);
//        provider.addIncludeFilter(new AnnotationTypeFilter(Listener.class));
//        List<String> className = new ArrayList<>();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(UselessScan.class.getName()));
        if (attributes == null) {
            ClassUtil.listenerPackages = new String[]{ClassUtils.getPackageName(annotationMetadata.getClassName())};
//            ClassUtil.filterPackages = new String[]{ClassUtils.getPackageName(annotationMetadata.getClassName())};
            return new String[0];
        }
        ClassUtil.listenerPackages = attributes.getStringArray("listenerPackages");
//        ClassUtil.filterPackages = attributes.getStringArray("filterPackages");
        return new String[0];
//        if (listenerPackages.length == 0) {
//            listenerPackages = new String[]{ClassUtils.getPackageName(annotationMetadata.getClassName())};
//        }
//        for (String aPackage : listenerPackages) {
//            Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(aPackage);
//            beanDefinitionSet.forEach(beanDefinition -> className.add(beanDefinition.getBeanClassName()));
//        }
//        String[] classNameArray = new String[className.size()];
//        return className.toArray(classNameArray);
    }

}