package at.yawk.fimficiton;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker annotation used to describe fields that are account-specific. One
 * example of this is {@link Story#readLater}.
 * 
 * @author Yawkat
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface AccountSpecific {}
