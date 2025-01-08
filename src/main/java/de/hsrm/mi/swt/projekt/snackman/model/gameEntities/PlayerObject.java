package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

public abstract class PlayerObject extends GameObject {

    private String username;

    public PlayerObject(String username, long objectId, long gameId, float x, float y, float z, float radius, float height) {
        super(objectId, gameId, x, y, z, radius, height);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
