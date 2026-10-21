// Заглушка вместо Rust-крейта. Единственная Rust-функция, вызываемая из C++,
// это rust_panic_use_dbg_assert() (см. base/rust.h и engine/shared/engine.cpp).
// Реализуем её через обычный assert/log — поведение для Android-сборки достаточно.
#include <base/rust.h>
#include <base/dbg.h>

extern "C" void rust_panic_use_dbg_assert()
{
    dbg_assert(0, "Rust panic (stub: rust_panic_use_dbg_assert called)");
}
