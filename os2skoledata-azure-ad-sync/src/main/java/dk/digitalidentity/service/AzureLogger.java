package dk.digitalidentity.service;

import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.logger.ILogger;
import com.microsoft.graph.logger.LoggerLevel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Slf4j
public class AzureLogger implements ILogger {
    private LoggerLevel level = LoggerLevel.ERROR;

    @Override
    public void setLoggingLevel(@NotNull LoggerLevel level) {
        this.level = Objects.requireNonNull(level, "parameter level cannot be null");
    }

    @NotNull
    @Override
    public LoggerLevel getLoggingLevel() {
        return level;
    }

    @Override
    public void logDebug(@NotNull String message) {
        log.debug(message);
    }

    @Override
    public void logError(@NotNull String message, @Nullable Throwable throwable) {
        if ((throwable instanceof GraphServiceException) && ((GraphServiceException) throwable).getResponseCode() == 404) {
            log.warn(message, throwable);
        } else {
            log.error(message, throwable);
        }
    }
}
