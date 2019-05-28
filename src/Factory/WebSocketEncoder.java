/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Factory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Encodes incoming buffers in a manner that makes the receiving client type transparent to the 
 * encoders further up in the filter chain. If the receiving client is a native client then
 * the buffer contents are simply passed through. If the receiving client is a websocket, it will encode
 * the buffer contents in to WebSocket DataFrame before passing it along the filter chain.
 * 
 * Note: you must wrap the IoBuffer you want to send around a WebSocketCodecPacket instance.
 * 
 * @author DHRUV CHOPRA
 */
public class WebSocketEncoder extends ProtocolEncoderAdapter{

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        boolean isHandshakeResponse = message instanceof WebSocketHandShakeResponse;
        boolean isDataFramePacket = message instanceof WebSocketCodecPacket;
        boolean isRemoteWebSocket = (Boolean)session.getAttribute(WebSocketUtils.SessionAttribute);//session.containsAttribute(WebSocketUtils.SessionAttribute) && (true==(Boolean)session.getAttribute(WebSocketUtils.SessionAttribute));
        IoBuffer resultBuffer;
        if(isHandshakeResponse){
            WebSocketHandShakeResponse response = (WebSocketHandShakeResponse)message;
            resultBuffer = WebSocketEncoder.buildWSResponseBuffer(response);
        }
        else if(isDataFramePacket){
            WebSocketCodecPacket packet = (WebSocketCodecPacket)message;
            resultBuffer = WebSocketEncoder.buildWSDataFrameBuffer(packet.getPacket());//isRemoteWebSocket ? WebSocketEncoder.buildWSDataFrameBuffer(packet.getPacket()) : packet.getPacket();
        }
        else{
            throw (new Exception("message not a websocket type"));
        }
        
        out.write(resultBuffer);
    }
    
    // Web Socket handshake response go as a plain string.
    private static IoBuffer buildWSResponseBuffer(WebSocketHandShakeResponse response) {                
        IoBuffer buffer = IoBuffer.allocate(response.getResponse().getBytes().length, false);
        buffer.setAutoExpand(true);
        buffer.put(response.getResponse().getBytes());
        buffer.flip();
        return buffer;
    }
    
    // Encode the in buffer according to the Section 5.2. RFC 6455
    private static IoBuffer buildWSDataFrameBuffer(IoBuffer buf) {
        int frameLen = buf.limit();
        
        IoBuffer buffer = IoBuffer.allocate(frameLen + 2, false);
        buffer.setAutoExpand(true);
        
        byte frameInfo = (byte) (1 << 7);
        frameInfo = (byte) (frameInfo | 1);
        buffer.put(frameInfo);
        
        if(frameLen <= 125){
           buffer.put((byte) ((byte) frameLen & (byte) 0x7F));
        } else if (frameLen > 125 && frameLen <= 65535) {
            buffer.put((byte) ((byte) 126 & (byte) 0x7F));
            buffer.putShort((short) frameLen);
        }else{
            buffer.put((byte) ((byte) 127 & (byte) 0x7F));
            buffer.putLong((int) frameLen);
        }        
        buffer.put(buf);
        buffer.flip();
        return buffer;
    }
    
}
