package cn.com.easyerp.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

@Configuration
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter {
    private static final String REMEMBER_ME_KEY = "dxRememberMe";
    private static final String LOGIN_URL = "/auth/login.view";
    private static final String LOGIN_WITH_ERROR_URL = "/auth/login.view?error";
    private AuthEntryPoint authEntryPoint = new AuthEntryPoint();
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    @Autowired(required = false)
    private DxAuthenticationProvider customProvider;
    private SessionRegistry sessionRegistry = new SessionRegistryImpl();
    private AuthenticationSuccessHandler authenticationSuccessHandler = new SimpleLoginSuccessHandler();
    private SimpleLogOutSuccessHandler simpleLogOutSuccessHandler = new SimpleLogOutSuccessHandler();
    private ConcurrentSessionFilter sessionFilter = new ConcurrentSessionFilter(this.sessionRegistry, LOGIN_URL);

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter() {
        return this.sessionFilter;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return this.sessionRegistry;
    }

    protected UserDetailsService userDetailsService() {
        return this.authService;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (this.customProvider == null) {
            auth.userDetailsService(this.authService);
        } else {
            auth.authenticationProvider(this.customProvider);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void configure(HttpSecurity http) throws Exception {
        this.sessionFilter.setRedirectStrategy(new DxInvalidSessionStrategy());
        ConcurrentSessionControlAuthenticationStrategy cscas = new ConcurrentSessionControlAuthenticationStrategy(
                this.sessionRegistry);
        cscas.setMaximumSessions(-1);
        RegisterSessionAuthenticationStrategy rsas = new RegisterSessionAuthenticationStrategy(this.sessionRegistry);
        List<SessionAuthenticationStrategy> beans = new ArrayList<SessionAuthenticationStrategy>();
        beans.add(cscas);
        beans.add(rsas);
        CompositeSessionAuthenticationStrategy sas = new CompositeSessionAuthenticationStrategy(beans);
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/dashboard/getHomeSubscribes.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/widget/message/getUnreadCount.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/approve/outsideSubmitApprove.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/externalForm/externalForm.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/operation/checkOperationAuth.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/externalForm/getExternalFormData.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/externalForm/selectExternalFormData.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/detail/getOldParam.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/approve/approveBack.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/approve/approve.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/detail/next.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/approve/takeBackApprove.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/data/codeStrOut.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/data/decodeStrOut.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/login2.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/loginVerification.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/register.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/phoneCode.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/phoneReg.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/emailReg.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/forgetPassword.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/weixin.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/alipay.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/microblog.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/changSubject.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/findpassword.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/checkUserName.do" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/selectcorp.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/auth/addCompany.view" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/reg-mobile/index.vm" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/**/*.do" })).hasAnyRole(new String[] { "authenticated_user" });
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/**/*.view" })).hasAnyRole(new String[] { "authenticated_user" });
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/widget/**" })).hasAnyRole(new String[] { "authenticated_user" });
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/index.view" })).hasAnyRole(new String[] { "authenticated_user" });
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/index/*.do" })).hasAnyRole(new String[] { "authenticated_user" });
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/webservice/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/js/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/tmp/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/storage/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/css/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/icons/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/img/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry
                .antMatchers(new String[] { "/reg-mobile/**" })).permitAll();
        registry = ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl) registry.anyRequest()).denyAll();
        ((HttpSecurity) ((HttpSecurity) ((HttpSecurity) ((HttpSecurity) ((FormLoginConfigurer) ((FormLoginConfigurer) ((FormLoginConfigurer) ((FormLoginConfigurer) ((FormLoginConfigurer) ((HttpSecurity) ((HttpSecurity) registry
                .and()).exceptionHandling().accessDeniedHandler(this.authAccessDeniedHandler)
                        .authenticationEntryPoint(this.authEntryPoint).and()).formLogin().loginPage(LOGIN_URL)
                                .defaultSuccessUrl("/index.view", true)).failureUrl(LOGIN_WITH_ERROR_URL))
                                        .loginProcessingUrl("/auth/login.do"))
                                                .successHandler(this.authenticationSuccessHandler)).permitAll()).and())
                                                        .logout().logoutSuccessUrl(LOGIN_URL)
                                                        .logoutUrl("/auth/logout.do")
                                                        .logoutSuccessHandler(this.simpleLogOutSuccessHandler)
                                                        .permitAll().and()).rememberMe().key(REMEMBER_ME_KEY).and())
                                                                .csrf().disable()).addFilter(this.sessionFilter)
                                                                        .sessionManagement()
                                                                        .sessionAuthenticationStrategy(sas);
    }
}