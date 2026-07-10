package dev.tauri.jsg.core.common.block.crystal;

public enum CrystalBudType {
    SMALL(3, 4),
    MEDIUM(4, 3),
    LARGE(5, 3),
    CLUSTER(7, 3);

    public final int size;
    public final int offset;

    CrystalBudType(int size, int offset) {
        this.size = size;
        this.offset = offset;
    }
}
