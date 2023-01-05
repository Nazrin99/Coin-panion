package com.example.coin_panion.classes;

public abstract class DbBone{

    private boolean inDatabase;

    protected void setInDatabase(boolean inDatabase) {
        this.inDatabase = inDatabase;
    }

    protected boolean inDatabase() {
        return inDatabase;
    }

    public abstract String insertQuery();
    public abstract String updateQuery();
    public abstract String deleteQuery();
}
