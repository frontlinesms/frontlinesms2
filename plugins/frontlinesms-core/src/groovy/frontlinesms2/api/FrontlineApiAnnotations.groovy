package frontlinesms2.api

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@interface FrontlineApiAnnotations {
    String apiUrl() default "";
}