//package opentp.client.spring.boot.starter.configuration;
//
//import org.springframework.context.annotation.DeferredImportSelector;
//import org.springframework.core.Ordered;
//import org.springframework.core.type.AnnotationMetadata;
//
///**
// * load opentp OpentpSpringBeanRegister bean
// */
//public class OpentpSpringBeanImportSelector implements DeferredImportSelector, Ordered {
//
//    @Override
//    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//        return new String[]{OpentpSpringBeanRegister.class.getName()};
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.HIGHEST_PRECEDENCE;
//    }
//}
