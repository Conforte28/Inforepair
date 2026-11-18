package bj.inforepair.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class SchemaConfig implements BeanPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource dataSource && environment != null) {
            String jdbcUrl = environment.getProperty("spring.datasource.url", "");
            int idx = jdbcUrl.indexOf("currentSchema=");
            if (idx != -1) {
                String rest = jdbcUrl.substring(idx + "currentSchema=".length());
                int amp = rest.indexOf('&');
                String schema = (amp != -1 ? rest.substring(0, amp) : rest).trim();
                if (!schema.isEmpty() && !"public".equalsIgnoreCase(schema) && schema.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                    try (Connection conn = dataSource.getConnection();
                         Statement st = conn.createStatement()) {
                        st.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
                        st.execute("SET search_path TO " + schema);
                    } catch (Exception e) {
                        throw new IllegalStateException("Impossible de créer le schéma PostgreSQL " + schema, e);
                    }
                }
            }
        }
        return bean;
    }
}
