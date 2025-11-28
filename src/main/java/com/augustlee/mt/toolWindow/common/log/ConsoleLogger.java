package com.augustlee.mt.toolWindow.common.log;

import com.augustlee.mt.toolWindow.common.config.ConfigUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 控制台日志工具类
 * 包装 IntelliJ Platform 的 Logger，同时输出到日志文件和控制台
 * 
 * <p>使用方式：</p>
 * <pre>{@code
 * private static final ConsoleLogger LOG = ConsoleLogger.getInstance(MyClass.class);
 * 
 * LOG.info("这是一条信息日志");
 * LOG.error("这是一条错误日志", exception);
 * }</pre>
 * 
 * <p>特性：</p>
 * <ul>
 *   <li>自动输出到 IntelliJ Platform 日志文件（通过 Logger）</li>
 *   <li>同时输出到 IDEA 控制台（System.out/System.err）</li>
 *   <li>支持日志级别过滤（DEBUG/INFO/WARN/ERROR）</li>
 *   <li>可以通过系统属性控制是否输出到控制台</li>
 *   <li>性能优化：支持异步输出，避免阻塞业务线程</li>
 *   <li>性能优化：使用缓冲输出，减少系统调用次数</li>
 * </ul>
 * 
 * <p>性能优化说明：</p>
 * <ul>
 *   <li>默认启用异步输出（-Dmt.tool.console.log.async=true），日志写入队列，由后台线程处理</li>
 *   <li>使用 PrintWriter 缓冲，减少系统调用开销</li>
 *   <li>队列满时自动降级为同步输出，避免阻塞</li>
 *   <li>可以通过 -Dmt.tool.console.log.async=false 禁用异步输出</li>
 * </ul>
 * 
 * @see ConsoleLogger
 * @author August Lee
 * @since 2025/11/28 10:09
 */
public class ConsoleLogger {
    
    /**
     * 是否启用控制台输出
     * 从 ConfigUtil 工具类读取配置
     */
    private static final boolean ENABLE_CONSOLE_OUTPUT = ConfigUtil.isConsoleLogEnabled();
    
    /**
     * 是否启用异步输出
     * 从 ConfigUtil 工具类读取配置
     */
    private static final boolean ENABLE_ASYNC_OUTPUT = ConfigUtil.isAsyncLogEnabled();
    
    /**
     * 日期时间格式化器
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * 日志队列（用于异步输出）
     */
    private static final BlockingQueue<LogEntry> LOG_QUEUE = new LinkedBlockingQueue<>(1000);
    
    /**
     * 异步输出线程是否已启动
     */
    private static final AtomicBoolean ASYNC_THREAD_STARTED = new AtomicBoolean(false);
    
    /**
     * 缓冲的 PrintWriter（用于同步输出时的缓冲）
     * 使用 autoFlush=true 确保及时输出，但利用 PrintWriter 的缓冲机制减少系统调用
     */
    private static final PrintWriter OUT_WRITER = new PrintWriter(System.out, true);
    private static final PrintWriter ERR_WRITER = new PrintWriter(System.err, true);
    
    /**
     * 底层的 IntelliJ Platform Logger
     */
    private final Logger logger;
    
    /**
     * 类名（用于日志输出）
     */
    private final String className;
    
    /**
     * 日志条目（用于异步输出）
     */
    private static class LogEntry {
        final String level;
        final String message;
        final Throwable throwable;
        final boolean isError;
        
        LogEntry(String level, String message, Throwable throwable, boolean isError) {
            this.level = level;
            this.message = message;
            this.throwable = throwable;
            this.isError = isError;
        }
    }
    
    /**
     * 启动异步输出线程（懒加载，线程安全）
     */
    private static void startAsyncThreadIfNeeded() {
        if (ENABLE_ASYNC_OUTPUT && ASYNC_THREAD_STARTED.compareAndSet(false, true)) {
            Thread asyncThread = new Thread(() -> {
                while (true) {
                    try {
                        LogEntry entry = LOG_QUEUE.take();
                        if (entry.isError) {
                            ERR_WRITER.println(entry.message);
                            if (entry.throwable != null) {
                                entry.throwable.printStackTrace(ERR_WRITER);
                            }
                        } else {
                            OUT_WRITER.println(entry.message);
                            if (entry.throwable != null) {
                                entry.throwable.printStackTrace(OUT_WRITER);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "ConsoleLogger-AsyncWriter");
            asyncThread.setDaemon(true);
            asyncThread.start();
        }
    }
    
    /**
     * 私有构造函数
     * 
     * @param clazz 要记录日志的类
     */
    private ConsoleLogger(@NotNull Class<?> clazz) {
        this.logger = Logger.getInstance(clazz);
        this.className = clazz.getSimpleName();
    }
    
    /**
     * 获取 ConsoleLogger 实例
     * 
     * @param clazz 要记录日志的类
     * @return ConsoleLogger 实例
     */
    @NotNull
    public static ConsoleLogger getInstance(@NotNull Class<?> clazz) {
        return new ConsoleLogger(clazz);
    }
    
    /**
     * 记录 DEBUG 级别日志
     * 
     * @param message 日志消息
     */
    public void debug(@NotNull String message) {
        logger.debug(message);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleOutput("DEBUG", message, null);
        }
    }
    
    /**
     * 记录 INFO 级别日志
     * 
     * @param message 日志消息
     */
    public void info(@NotNull String message) {
        logger.info(message);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleOutput("INFO", message, null);
        }
    }
    
    /**
     * 记录 WARN 级别日志
     * 
     * @param message 日志消息
     */
    public void warn(@NotNull String message) {
        logger.warn(message);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleOutput("WARN", message, null);
        }
    }
    
    /**
     * 记录 WARN 级别日志（带异常）
     * 
     * @param message 日志消息
     * @param throwable 异常对象
     */
    public void warn(@NotNull String message, @Nullable Throwable throwable) {
        logger.warn(message, throwable);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleOutput("WARN", message, throwable);
        }
    }
    
    /**
     * 记录 ERROR 级别日志
     * 
     * @param message 日志消息
     */
    public void error(@NotNull String message) {
        logger.error(message);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleError("ERROR", message, null);
        }
    }
    
    /**
     * 记录 ERROR 级别日志（带异常）
     * 
     * @param message 日志消息
     * @param throwable 异常对象
     */
    public void error(@NotNull String message, @Nullable Throwable throwable) {
        logger.error(message, throwable);
        if (ENABLE_CONSOLE_OUTPUT) {
            consoleError("ERROR", message, throwable);
        }
    }
    
    /**
     * 输出到控制台（标准输出）
     * 使用缓冲和异步输出优化性能
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param throwable 异常对象（可选）
     */
    private void consoleOutput(@NotNull String level, @NotNull String message, @Nullable Throwable throwable) {
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String logLine = String.format("[%s] [%s] [%s] %s", timestamp, level, className, message);
        
        if (ENABLE_ASYNC_OUTPUT) {
            // 异步输出：将日志放入队列，由后台线程处理
            startAsyncThreadIfNeeded();
            // 如果队列满了，使用同步输出作为降级方案
            if (!LOG_QUEUE.offer(new LogEntry(level, logLine, throwable, false))) {
                // 队列满时，直接同步输出（避免阻塞）
                OUT_WRITER.println(logLine);
                if (throwable != null) {
                    throwable.printStackTrace(OUT_WRITER);
                }
            }
        } else {
            // 同步输出：使用缓冲的 PrintWriter
            OUT_WRITER.println(logLine);
            if (throwable != null) {
                throwable.printStackTrace(OUT_WRITER);
            }
        }
    }
    
    /**
     * 输出到控制台（标准错误输出）
     * 使用缓冲和异步输出优化性能
     * 
     * @param level 日志级别
     * @param message 日志消息
     * @param throwable 异常对象（可选）
     */
    private void consoleError(@NotNull String level, @NotNull String message, @Nullable Throwable throwable) {
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String logLine = String.format("[%s] [%s] [%s] %s", timestamp, level, className, message);
        
        if (ENABLE_ASYNC_OUTPUT) {
            // 异步输出：将日志放入队列，由后台线程处理
            startAsyncThreadIfNeeded();
            // 如果队列满了，使用同步输出作为降级方案
            if (!LOG_QUEUE.offer(new LogEntry(level, logLine, throwable, true))) {
                // 队列满时，直接同步输出（避免阻塞）
                ERR_WRITER.println(logLine);
                if (throwable != null) {
                    throwable.printStackTrace(ERR_WRITER);
                }
            }
        } else {
            // 同步输出：使用缓冲的 PrintWriter
            ERR_WRITER.println(logLine);
            if (throwable != null) {
                throwable.printStackTrace(ERR_WRITER);
            }
        }
    }
}

