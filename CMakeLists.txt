cmake_minimum_required(VERSION 3.22)
project(kinetix C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -fexceptions -frtti -O2")
set(CMAKE_C_FLAGS   "${CMAKE_C_FLAGS}   -fPIC -O2")

if(ANDROID)
  add_definitions(-DANDROID -DCONF_PLATFORM_ANDROID=1)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -D__ANDROID__")
endif()

find_package(SDL2 REQUIRED)
include_directories(${SDL2_INCLUDE_DIRS})

set(EXT_SRC
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

add_library(ddnet_rust STATIC IMPORTED)
set_target_properties(ddnet_rust PROPERTIES
  IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/rust-libs/arm64-v8a/libddnet_rust.a
)

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

target_include_directories(main PRIVATE
  ${CMAKE_SOURCE_DIR}
  ${CMAKE_SOURCE_DIR}/engine/external
  ${CMAKE_SOURCE_DIR}/engine/external/sol2
)

target_link_libraries(main
  kinetix_ext ddnet_rust
  ${SDL2_LIBRARIES}
  log android GLESv3 EGL z
)
