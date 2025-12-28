package com.inventory.app.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Category implements Externalizable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;

    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private static String norm(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeUTF(name == null ? "" : name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.id = in.readInt();
        this.name = in.readUTF();
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category other)) return false;

        if (this.id > 0 && other.id > 0) return this.id == other.id;
        
        return norm(this.name).equals(norm(other.name));
    }

    @Override
    public int hashCode() {
        return (id > 0) ? Integer.hashCode(id) : norm(name).hashCode();
    }
}
