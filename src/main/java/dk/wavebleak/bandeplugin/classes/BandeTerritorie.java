package dk.wavebleak.bandeplugin.classes;

public class BandeTerritorie {
    private String ownedBandeID;
    private int x;
    private int y;
    private int z;
    private int minBread;
    private int maxBread;
    private int breadInterval;

    public BandeTerritorie(String ownedBandeID, int x, int y, int z, int minBread, int maxBread, int breadInterval) {
        this.ownedBandeID = ownedBandeID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.minBread = minBread;
        this.maxBread = maxBread;
        this.breadInterval = breadInterval;
    }

    public String getOwnedBandeID() {
        return ownedBandeID;
    }

    public void setOwnedBandeID(String ownedBandeID) {
        this.ownedBandeID = ownedBandeID;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getMinBread() {
        return minBread;
    }

    public void setMinBread(int minBread) {
        this.minBread = minBread;
    }

    public int getMaxBread() {
        return maxBread;
    }

    public void setMaxBread(int maxBread) {
        this.maxBread = maxBread;
    }

    public int getBreadInterval() {
        return breadInterval;
    }

    public void setBreadInterval(int breadInterval) {
        this.breadInterval = breadInterval;
    }
}
