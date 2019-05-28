Apache Mina IoFilter - WebSocket
==============

Apache Mina에서 IoFilter형태로 사용될 WebSocket 구현체 입니다.

이 플러그인은 Apache Mina에 웹 소켓 기능을 제공하기 위해 구현되었으며, 이 코드는 rfc6455를 준수하도록 작성되었습니다.

http://tools.ietf.org/html/rfc6455

예제
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
