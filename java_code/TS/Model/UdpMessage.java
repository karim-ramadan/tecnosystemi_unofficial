package it.tecnosystemi.TS.Model;

public class UdpMessage {
    String ip;
    String porta;
    String seriale;

    public String getIp() {
        return this.ip;
    }

    public void setIp(String str) {
        this.ip = str;
    }

    public String getPorta() {
        return this.porta;
    }

    public void setPorta(String str) {
        this.porta = str;
    }

    public String getSeriale() {
        return this.seriale;
    }

    public void setSeriale(String str) {
        this.seriale = str;
    }
}
