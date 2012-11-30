package frontlinesms2.api

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@interface FrontlineApiAnnotations { // TODO rename this something without "annotations", e.g. FrontlineApiProperties
    String apiUrl() default "";
}
