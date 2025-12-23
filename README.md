# JVM Custom Memory Arena

This project is a from-scratch, educational implementation of a manual memory arena in Java, built to understand how low-level memory, pointers, and data structures actually work beneath high-level language abstractions.

 

## Motivation

After ~4 years of programming in Java, a persistent and sizable chunk of its functionality as a language still feels opaque and abstract because of how it manages things under the hood:

- Objects appear without visible allocation
- Memory is automatically managed
- References feel disconnected from actual memory

This project aims to remove that abstraction.

Everything here is built on top of a single `byte[]`, and all structures, safety measures, and meaning are implemented manually.

The design emphasizes explicit control and fail-fast behavior so that incorrect assumptions about memory usage surface immediately.

 

## Project Overview

At its core, this project implements a **manual memory arena**:

- Memory is allocated explicitly
- All reads and writes are bounds-checked
- Data structures are constructed by defining layouts over raw bytes
- Pointers are represented as integer offsets
- Invalid memory access fails immediately

The project incrementally builds higher-level behavior on top of this foundation in a controlled and well-defined manner.

 

## Core Concepts Implemented

### 1. Memory Arena

The arena is a contiguous block of memory represented by a `byte[]` and a single allocation pointer (`offset`).

```
[ allocated memory | unallocated memory ]
                 ^
               offset
```

Rules:
- Memory must be allocated before use
- Reads and writes are only allowed inside the allocated region
- Resetting the arena invalidates all previously allocated addresses

This mirrors modern arena allocators used in professional systems programming.

 

### 2. Strict Allocation Model

This project uses a strict safety model:

- All memory access is validated against the current allocation boundary
- Reading or writing outside allocated memory throws immediately
- There is no silent memory corruption

This forces correct reasoning and makes bugs obvious.

 

### 3. Primitive Storage (Big Endian)

All primitive types are stored manually with explicit byte-level encoding.

Supported types:
- `byte` (1 byte)
- `short` (2 bytes)
- `int` (4 bytes)
- `long` (8 bytes)
- `char` (2 bytes, UTF-16)
- `boolean` (1 byte: 0 = false, 1 = true)

All values are stored in **big-endian** order:
- Most significant byte first
- Bit shifting and masking used explicitly
- Reconstruction performed byte-by-byte

Example: storing an `int`
- An `int` occupies 4 bytes
- Values are stored in big-endian order: `[byte0][byte1][byte2][byte3]`
- Bit shifting: `(x >>> 24) & 0xFF` for most significant byte
- Reconstruction: `(byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3`

This makes primitive representation and endianness explicit instead of implicit.

 

### 4. Struct-like Layouts (Nodes)

On top of raw memory, the project defines structured layouts through the `NodeStore` class.

A Node is defined as:

```
Node (8 bytes total):
+------------------+
| value (int)      |  offset + 0
+------------------+
| next (int)       |  offset + 4
+------------------+
```

Nodes are not Java objects.

A node is simply:
- an integer address
- a fixed memory layout
- helper methods that interpret bytes at that address

The `NodeStore` class encapsulates all node operations, separating structure logic from raw memory management. This mirrors C-style structs and pointers while maintaining clean separation of concerns.

 

### 5. Pointers and Sentinel Values

Pointers are represented as integer offsets into the arena.

Rules:
- A pointer is either:
  - a valid address inside allocated memory
  - `-1`, used as a null sentinel
- All pointers are validated before use
- Invalid pointers throw immediately

This makes pointer semantics explicit and visible.

 

### 6. Data Structures Built from Raw Memory

Using the node layout, the project implements a linked list:

- Nodes allocated from the arena
- `next` pointers stored as raw addresses
- Traversal implemented manually
- No Java collections or object allocation involved

This demonstrates how high-level data structures emerge from low-level memory rules.

 

## Safety Mechanisms

Multiple layers of validation are implemented with detailed error reporting.

### Custom Exception Classes

All memory errors throw specific exceptions with diagnostic information:

- **`OutOfMemoryException`**: Includes requested size, available bytes, capacity, and current offset
- **`InvalidAddressException`**: Includes address attempted, bytes needed, allocated boundary, and capacity
- **`InvalidPointerException`**: Includes pointer value, node size, allocated boundary, and capacity

This makes debugging memory issues immediate and clear.

### `checkAddr(addr, bytesNeeded)`

Ensures:
- The address is non-negative
- The requested memory range fits entirely inside allocated memory

Used for primitive reads and writes. Throws `InvalidAddressException` with full context on failure.

### `checkNodePtr(ptr)` (in NodeStore)

Ensures:
- The pointer is `-1` (null), or
- The pointer references a full node inside allocated memory

Used for all node-based operations. Throws `InvalidPointerException` with full context on failure.

### Memory Alignment

The arena supports aligned allocation:
- `align(addr, alignment)`: Calculates next aligned address
- `allocAligned(size, alignment)`: Allocates memory at aligned boundaries
- Tracks alignment waste for memory efficiency analysis

This demonstrates how real systems handle memory alignment requirements.

 

## Scope and Design Constraints

- Not a production allocator
- Not optimized for performance
- Not concurrent
- Not using JVM internals or Unsafe
- Not a garbage collector replacement

The implementation prioritizes clarity, correctness, and explicit control over performance or feature completeness.

 

## What This Project Teaches

- How memory is laid out at the byte level
- Why bounds checking matters
- How pointers actually work
- How struct layout is a design decision
- How data structures are built from raw memory
- Why high-level languages feel safe

 

## Current Capabilities

### Memory Management
- Manual memory allocation (`alloc`, `allocAligned`)
- Memory alignment support with waste tracking
- Strict bounds checking on all operations
- Arena reset functionality

### Primitive Types (All Big-Endian)
- `byte` (1 byte)
- `short` (2 bytes)
- `int` (4 bytes)
- `long` (8 bytes)
- `char` (2 bytes, UTF-16)
- `boolean` (1 byte)

### Data Structures
- `NodeStore`: Separated node logic with clean API
- Struct-like node layouts (8 bytes: value + next pointer)
- Pointer validation with null sentinel (`-1`)
- Linked list creation and traversal

### Error Handling
- Custom exception hierarchy (`MemoryException` base)
- Detailed error messages with diagnostic context
- Fail-fast behavior for all invalid operations

 

## Planned Next Steps

### Phase 3: Structured Data Types
- Fixed-size arrays (`ArrayStore`)
- Dynamic arrays/vectors with growth
- String storage with UTF-16 encoding
- Hash table implementation (advanced)

### Phase 4: Advanced Memory Management
- Memory regions/segments
- Free list allocator (optional)
- Memory statistics and visualization
- Memory layout diagram generation

### Phase 5: Data Structure Operations
- Linked list operations (insert, delete, find, reverse)
- Stack implementation
- Queue implementation

### Phase 6: Testing & Documentation
- Comprehensive unit tests
- Memory layout visualization tools
- Interactive examples and demos

### Future: Frontend
- Interactive web application
- Visual memory allocation demonstration
- Educational visualization of memory operations
- Other under-the-hood Java operations

 

 