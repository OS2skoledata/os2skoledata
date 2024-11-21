package dk.digitalidentity.os2skoledata.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_SCHOOL_EMPLOYEE')")
public @interface RequireSchoolEmployeeRole {

}
