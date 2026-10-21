cmake_minimum_required(VERSION 3.22)
project(kinetix C CXX)

# --- ЯВНО C++20 для всех файлов, без CMAKE_CXX_STANDARD ---
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++20 -fPIC -fexceptions -frtti -O2")
set(CMAKE_C_FLAGS   "${CMAKE_C_FLAGS}   -fPIC -O2")
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

if(ANDROID)
  add_definitions(-DANDROID -DCONF_PLATFORM_ANDROID=1 -DWAVPACK_NO_ASM=1)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -D__ANDROID__")
endif()

# sol2 иногда требует отключить "safeties"
add_definitions(-DSOL_ALL_SAFETIES_MODES=0)

# --- SDL2 (vendored) ---
add_subdirectory(${CMAKE_SOURCE_DIR}/third_party/SDL ${CMAKE_BINARY_DIR}/SDL)

# --- глобальные include ---
include_directories(
  ${CMAKE_SOURCE_DIR}
  ${CMAKE_SOURCE_DIR}/third_party/SDL/include
  ${CMAKE_SOURCE_DIR}/engine/external
  ${CMAKE_SOURCE_DIR}/engine/external/sol2
  ${CMAKE_SOURCE_DIR}/engine/external/yoga
)

# --- yoga (C++20 уже глобально) ---
file(GLOB YOGA_SRC engine/external/yoga/*.cpp)
add_library(yoga_ext STATIC ${YOGA_SRC})

# --- прочие external libs (C, C++20 не влияет) ---
file(GLOB LUA_SRC     engine/external/lua/*.c)
file(GLOB WAVPACK_SRC engine/external/wavpack/*.c)

set(EXT_SRC
  ${LUA_SRC} ${WAVPACK_SRC}
  engine/external/zlib/adler32.c engine/external/zlib/compress.c
  engine/external/zlib/crc32.c   engine/external/zlib/deflate.c
  engine/external/zlib/infback.c engine/external/zlib/inffast.c
  engine/external/zlib/inflate.c engine/external/zlib/inftrees.c
  engine/external/zlib/trees.c   engine/external/zlib/uncompr.c
  engine/external/zlib/zutil.c
  engine/external/md5/md5.c
  engine/external/json-parser/json.c
)
add_library(kinetix_ext STATIC ${EXT_SRC})

# --- main sources ---
file(GLOB_RECURSE BASE_SRC   base/*.cpp)
file(GLOB_RECURSE ENG_C_SRC  engine/client/*.cpp)
file(GLOB_RECURSE ENG_S_SRC  engine/shared/*.cpp)
file(GLOB_RECURSE GAME_SRC   game/client/*.cpp game/client/components/*.cpp game/client/components/kinetix/*.cpp game/client/components/kinetix/*/*.cpp game/client/prediction/*.cpp game/client/prediction/entities/*.cpp game/*.cpp)
file(GLOB ANDROID_SRC android/*.cpp)

list(REMOVE_ITEM ENG_C_SRC
  ${CMAKE_SOURCE_DIR}/engine/client/backend/vulkan/backend_vulkan.cpp
  ${CMAKE_SOURCE_DIR}/engine/client/backend/opengl/backend_opengl.cpp
  ${CMAKE_SOURCE_DIR}/engine/client/backend/opengl/backend_opengl3.cpp
  ${CMAKE_SOURCE_DIR}/engine/client/backend/opengl/opengl_sl.cpp
  ${CMAKE_SOURCE_DIR}/engine/client/backend/opengl/opengl_sl_program.cpp
)

add_library(main SHARED
  ${BASE_SRC} ${ENG_C_SRC} ${ENG_S_SRC} ${GAME_SRC} ${ANDROID_SRC}
)

# Гарантируем, что SDL_main не выкинется линкером (для SDLActivity)
target_link_options(main PRIVATE "-Wl,--undefined=SDL_main")

target_link_libraries(main
  kinetix_ext
  yoga_ext
  SDL2::SDL2
  log android GLESv3 EGL z
)
