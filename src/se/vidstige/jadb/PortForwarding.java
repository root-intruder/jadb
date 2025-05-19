package se.vidstige.jadb;

public class PortForwarding {
    private String device;
    private String local;
    private String remote;

    private PortForwarding(String device, String local, String remote) {
        this.device = device;
        this.local = local;
        this.remote = remote;
    }

    public static PortForwarding from(String adbOutput, boolean reverse) {
        String[] split = adbOutput.split(" ");
        return new PortForwarding(split[0], reverse ? split[1] : split[2], reverse ? split[2] : split[1]);
    }

    public String getDevice() {
        return device;
    }

    public String getLocal() {
        return local;
    }

    public String getRemote() {
        return remote;
    }
}
