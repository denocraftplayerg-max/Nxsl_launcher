package com.movtery.zalithlauncher.utils.path

import com.movtery.zalithlauncher.feature.log.Logging
import java.io.File

/**
 * Manages custom native library loading through Java arguments
 * Supports loading custom .so files from designated folder
 */
class LibraryManager {
    companion object {
        // Directory for custom native libraries
        fun getCustomLibraryDir(): File {
            return File(PathManager.DIR_GAME_HOME, "libs_custom")
        }

        // Directory for custom renderer libraries
        fun getCustomRendererDir(): File {
            return File(PathManager.DIR_GAME_HOME, "renderers_custom")
        }

        /**
         * Initialize library directories
         */
        fun initLibraryDirectories() {
            val libDir = getCustomLibraryDir()
            val rendererDir = getCustomRendererDir()
            
            if (!libDir.exists()) {
                libDir.mkdirs()
                Logging.i("LibraryManager", "Created libs_custom directory: ${libDir.absolutePath}")
            }
            
            if (!rendererDir.exists()) {
                rendererDir.mkdirs()
                Logging.i("LibraryManager", "Created renderers_custom directory: ${rendererDir.absolutePath}")
            }
        }

        /**
         * Get all custom .so files from libs_custom directory
         */
        fun getCustomLibraries(): List<File> {
            val libDir = getCustomLibraryDir()
            if (!libDir.exists()) return emptyList()
            
            return libDir.listFiles { file ->
                file.isFile && file.name.endsWith(".so")
            }?.toList() ?: emptyList()
        }

        /**
         * Get all custom renderer .so files
         */
        fun getCustomRenderers(): List<File> {
            val rendererDir = getCustomRendererDir()
            if (!rendererDir.exists()) return emptyList()
            
            return rendererDir.listFiles { file ->
                file.isFile && file.name.endsWith(".so")
            }?.toList() ?: emptyList()
        }

        /**
         * Generate Java library path argument for native libraries
         * Format: -Djava.library.path=/path/to/libs_custom:/path/to/renderers_custom
         */
        fun generateLibraryPathArgument(): String {
            val paths = mutableListOf<String>()
            
            val customLibDir = getCustomLibraryDir()
            if (customLibDir.exists()) {
                paths.add(customLibDir.absolutePath)
            }
            
            val rendererDir = getCustomRendererDir()
            if (rendererDir.exists()) {
                paths.add(rendererDir.absolutePath)
            }
            
            return if (paths.isNotEmpty()) {
                "-Djava.library.path=${paths.joinToString(":")}"
            } else {
                ""
            }
        }

        /**
         * Get all library paths for java.library.path
         */
        fun getLibraryPaths(): String {
            val paths = mutableListOf<String>()
            
            // Add native lib directory from app
            paths.add(PathManager.DIR_NATIVE_LIB)
            
            // Add custom libraries
            val customLibDir = getCustomLibraryDir()
            if (customLibDir.exists()) {
                paths.add(customLibDir.absolutePath)
            }
            
            // Add custom renderers
            val rendererDir = getCustomRendererDir()
            if (rendererDir.exists()) {
                paths.add(rendererDir.absolutePath)
            }
            
            return paths.joinToString(":")
        }

        /**
         * Log available libraries
         */
        fun logAvailableLibraries() {
            val customLibs = getCustomLibraries()
            val customRenderers = getCustomRenderers()
            
            if (customLibs.isNotEmpty()) {
                Logging.i("LibraryManager", "Custom libraries found: ${customLibs.size}")
                customLibs.forEach { 
                    Logging.d("LibraryManager", "  - ${it.name} (${it.length} bytes)")
                }
            }
            
            if (customRenderers.isNotEmpty()) {
                Logging.i("LibraryManager", "Custom renderers found: ${customRenderers.size}")
                customRenderers.forEach { 
                    Logging.d("LibraryManager", "  - ${it.name} (${it.length} bytes)")
                }
            }
        }
    }
}
