Apache Mina IoFilter - WebSocket
==============

WebSocket and SSL implementers to be used in the form of IoFilter in Apache Mina.

Apache Mina에서 IoFilter형태로 사용될 WebSocket와 SSL 구현체 입니다.

#

This plug-in is implemented to provide Web Sockets functionality to Apache Mina, and WebSocket and SSL code are written to comply with rfc6455.

이 플러그인은 Apache Mina에 웹 소켓 기능을 제공하기 위해 구현되었으며, WebSocket과 SSL 코드는 rfc6455를 준수하도록 작성되었습니다.

http://tools.ietf.org/html/rfc6455

https://mina.apache.org/mina-project/userguide/ch11-ssl-filter/ch11-ssl-filter.html

#

Examples can be found in the Example folder in Repo.

예제는 레포의 Example폴더에서 볼 수 있습니다.

WebSocket Example
--------------
```
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {//메시지를 받았을 경우
        IoBuffer buffer = (IoBuffer) message;
        String Data = buffer.getString(Charset.forName("UTF-8").newDecoder());
        sendData(session, Data);//Echo
    }
```
```
    public void sendData(IoSession session, String Data) throws Exception{
        IoBuffer buffer = IoBuffer.allocate(Data.getBytes("UTF-8").length);
        buffer.putString(Data, Charset.forName("UTF-8").newEncoder());
        buffer.flip();
        session.write( WebSocketCodecPacket.buildPacket(buffer) );
    }
```

SSL Exaple
--------------
```
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
```
