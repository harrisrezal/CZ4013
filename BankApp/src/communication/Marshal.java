package communication;

import response.ResponseMessage;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Marshal {

    public Marshal() {

    }

    //TODO prepare UUID and Method.
    public static void marshal(Object obj, ByteBuffer buf, UUID uuid, String method) throws IllegalAccessException {
        resetBuffer(buf);

        //Prepare the headers
        writeToByteBuffer(uuid, buf);
        writeToByteBuffer(method, buf);

        Class classOfObject = obj.getClass();
        Field[] fieldList = classOfObject.getDeclaredFields();


        for (Field field : fieldList) {
            writeToByteBuffer(field.get(obj), buf);
        }

    }

    //Marshal Server to client. [Done on Server]
    public static void marshalResponse(ResponseMessage msg, ByteBuffer buf) throws IllegalAccessException {
        resetBuffer(buf);

        //Prepare the headers
        writeToByteBuffer(msg.id, buf);
        writeToByteBuffer(msg.method, buf);
        writeToByteBuffer(msg.status, buf);
        System.out.println("[Server] Sending UUID : " + msg.id.toString());
        System.out.println("[Server] Sending Method : " + msg.method);
        System.out.println("[Server] Sending Status : " + msg.status.toString());
        System.out.println("[Server] Sending Status boolean : " + msg.status.getClass().isEnum());

        Class classOfObject = msg.obj.getClass();
        Field[] fieldList = classOfObject.getDeclaredFields();


        for (Field field : fieldList) {
            writeToByteBuffer(field.get(msg.obj), buf);
        }

    }

    public static void writeToByteBuffer(Object valueOfObject, ByteBuffer buf) throws IllegalAccessException {
        if (valueOfObject.getClass().isEnum()) {
            Enum<?> e = (Enum) valueOfObject;
            buf.put((byte) e.ordinal());
        } else if (valueOfObject instanceof Byte) {
            buf.put((Byte) valueOfObject);
        } else if (valueOfObject instanceof Boolean) {
            buf.put((byte) ((Boolean) valueOfObject ? 1 : 0));
        } else if (valueOfObject instanceof Integer) {
            buf.putInt((Integer) valueOfObject);
        } else if (valueOfObject instanceof Double) {
            buf.putDouble((Double) valueOfObject);
        } else if (valueOfObject instanceof String) {
            byte[] utf8 = ((String) valueOfObject).getBytes(StandardCharsets.UTF_8);
            buf.putInt(utf8.length); //Set the length of the String
            buf.put(utf8); //Then put the string
        } else if (valueOfObject instanceof UUID) {
            write((UUID) valueOfObject, buf);
        }

    }

    public static void write(UUID x, ByteBuffer buf) {
        buf.putLong(x.getMostSignificantBits());
        buf.putLong(x.getLeastSignificantBits());
    }

    public static void resetBuffer(ByteBuffer buf) {
        buf.clear();
        buf.order(ByteOrder.BIG_ENDIAN);
    }
}
