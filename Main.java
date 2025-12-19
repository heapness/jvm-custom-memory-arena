public class Main {
    public static void main(String[] args) {
        MemoryArena arena = new MemoryArena(128);

        int a = arena.alloc(4);
        int b = arena.alloc(4);
        int c = arena.alloc(4);

        System.out.println("address of a: " + a);
        System.out.println("address of b: " + b);
        System.out.println("address of c: " + c);
    }
}
