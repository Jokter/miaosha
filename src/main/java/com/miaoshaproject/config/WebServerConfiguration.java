package com.miaoshaproject.config;


import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wangyu
 * @created 2020/12/3 10:35 下午
 */
//当Spring容器中没有TomcatEmbeddedServletContainerFactory这个bean时，会把此bean加载进spring容器
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        //使用对应的工厂类t提供给我们的接口定制化我们的tomcat connector
        ((TomcatServletWebServerFactory)factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                //定制化keepAliveTimeOut,30秒没请求则断开连接
                protocol.setKeepAliveTimeout(30000);
                //当客户端发送超过10000个请求则自动断开keepAlive连接
                protocol.setMaxKeepAliveRequests(10000);
            }
        });
    }
}
