# CipherTool v1.0

Lightweight, extensible, and memory-efficient Java hashing library built with clean OOP architecture and Java Security best practices.

---

## ✨ Features

- Clean OOP design using `Interface + Abstract Class`
- Supports 13 modern and widely used hashing algorithms
- Efficient file hashing with `DigestInputStream` streaming
- Low memory usage even for very large files
- Built-in `hashToHex()` utility method
- Factory pattern support via `HashFactory.getInstance()`
- Secure hash comparison using `MessageDigest.isEqual()`
- Safe resource management with `try-with-resources`
- Easy to extend with custom hashing algorithms

---

## 🔐 Supported Algorithms

```text
MD2
MD5
SHA-1
SHA-224
SHA-256
SHA-384
SHA-512
SHA-512/224
SHA-512/256
SHA3-224
SHA3-256
SHA3-384
SHA3-512
