package Example.Handler;

import Factory.WebSocketCodecPacket;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/*
 *
 *
 * @author syudal syudal.tistory.com
 */
public class MainServerHandler extends IoHandlerAdapter {
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        IoBuffer buffer = (IoBuffer) message;
        String Data = buffer.getString(Charset.forName("UTF-8").newDecoder());
        sendData(session, Data);//Echo
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("sessionCreated : " + session.getRemoteAddress());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("sessionClosed : " + session.getRemoteAddress());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println( "sessionIdle : " + session.getIdleCount( status ));
    }

    public void sendData(IoSession session, String Data) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(Data.getBytes("UTF-8").length);
        buffer.putString(Data, Charset.forName("UTF-8").newEncoder());
        buffer.flip();
        session.write( WebSocketCodecPacket.buildPacket(buffer) );
    }
}
