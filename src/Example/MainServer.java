package Example;

import Example.Handler.MainServerHandler;

import Factory.Constant;
import Factory.WebSocketCodecFactory;

import SSL.ClientAuth;
import SSL.SslConfiguration;
import SSL.SslConfigurationFactory;
import java.io.File;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/*
 *
 *
 * @author syudal syudal.tistory.com
 */
public class MainServer {

    public static void main(String[] args) throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();

        if (Constant.UseSSL) {
            SslConfigurationFactory sslConfig = new SslConfigurationFactory();
            sslConfig.setKeystoreFile(new File(Constant.SSLLocation));
            sslConfig.setKeystorePassword(Constant.SSLPassword);
            sslConfig.createSslConfiguration();
            SslConfiguration ssl = sslConfig.createSslConfiguration();

            SslFilter sslFilter;
            try {
                sslFilter = new SslFilter(ssl.getSSLContext());
            } catch (GeneralSecurityException e) {
                throw new IOException("SSL could not be initialized, check configuration");
            }

            if (ssl.getClientAuth() == ClientAuth.NEED) {
                sslFilter.setNeedClientAuth(true);
            } else if (ssl.getClientAuth() == ClientAuth.WANT) {
                sslFilter.setWantClientAuth(true);
            }

            if (ssl.getEnabledCipherSuites() != null) {
                sslFilter.setEnabledCipherSuites(ssl.getEnabledCipherSuites());
            }

            acceptor.getFilterChain().addFirst("sslFilter", sslFilter);
        }

        acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new WebSocketCodecFactory()));
        
        acceptor.setHandler(new MainServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.bind(new InetSocketAddress(Constant.Port));
    }
}
