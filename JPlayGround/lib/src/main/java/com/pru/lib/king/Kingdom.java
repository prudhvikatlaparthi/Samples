package com.pru.lib.king;

public class Kingdom {
    private final String life;
    private final String nonLife;
    private final int lifeSpan;
    Kingdom(String life, String nonLife, int lifeSpan){
        this.life = life;
        this.nonLife = nonLife;
        this.lifeSpan = lifeSpan;
    }

    public String getLife() {
        return life;
    }

    public String getNonLife() {
        return nonLife;
    }

    public int getLifeSpan() {
        return lifeSpan;
    }

    @Override
    public String toString() {
        return "Kingdom{" +
                "life='" + life + '\'' +
                ", nonLife='" + nonLife + '\'' +
                ", lifeSpan=" + lifeSpan +
                '}';
    }
}
