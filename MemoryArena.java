public class MemoryArena {
    private final byte[] memory;
    private int offset = 0;

    public MemoryArena(int size) {
        memory = new byte[size];
    }

    public int alloc(int size) {
        if (size + offset > memory.length) {
            throw new RuntimeException("Out of memory!");
        }

        int start = offset;
        offset += size;
        return start;
    }

}
