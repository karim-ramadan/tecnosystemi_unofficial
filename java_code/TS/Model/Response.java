package it.tecnosystemi.TS.Model;

public class Response {
    byte[] ByteResponsePayload;
    int httpResponceCode;
    String httpResponcePayload;

    public String getHttpResponcePayload() {
        return this.httpResponcePayload;
    }

    public void setHttpResponcePayload(String str) {
        this.httpResponcePayload = str;
    }

    public int getHttpResponceCode() {
        return this.httpResponceCode;
    }

    public void setHttpResponceCode(int i) {
        this.httpResponceCode = i;
    }

    public byte[] getByteResponsePayload() {
        return this.ByteResponsePayload;
    }

    public void setByteResponsePayload(byte[] bArr) {
        this.ByteResponsePayload = bArr;
    }
}
