# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

from __future__ import annotations

import logging
import os
import sys
from datetime import datetime

_ROOT_LOG = "cassandragargoyle"

# Log format matching Java CustomFormatter: dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger]: message
_LOG_FORMAT = "%(asctime)s %(levelname)s [%(name)s]: %(message)s"
_DATE_FORMAT = "%d/%m/%Y %H:%M:%S"

# Level alias mappings (Java -> Python)
_LEVEL_ALIASES: dict[str, int] = {
    "TRACE": logging.DEBUG,
    "T": logging.DEBUG,
    "4": logging.DEBUG,
    "DEBUG": logging.DEBUG,
    "D": logging.DEBUG,
    "3": logging.DEBUG,
    "INFO": logging.INFO,
    "I": logging.INFO,
    "2": logging.INFO,
    "WARNING": logging.WARNING,
    "W": logging.WARNING,
    "1": logging.WARNING,
    "ERROR": logging.ERROR,
    "E": logging.ERROR,
    "0": logging.ERROR,
}


class _CustomFormatter(logging.Formatter):
    """Custom formatter matching Java CustomFormatter output.

    Format: dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger.name]: message
    """

    def __init__(self) -> None:
        super().__init__(fmt=_LOG_FORMAT, datefmt=_DATE_FORMAT)

    def formatTime(self, record: logging.LogRecord, datefmt: str | None = None) -> str:
        dt = datetime.fromtimestamp(record.created)
        base = dt.strftime(_DATE_FORMAT)
        millis = int(record.msecs)
        return f"{base}.{millis:03d}"


class Logging:
    """Centralized logging initialization and level management."""

    ROOT_LOG = _ROOT_LOG

    _console_handler: logging.Handler | None = None
    _file_handler: logging.Handler | None = None

    @classmethod
    def initialize(
        cls,
        log_level: str = "INFO",
        log_on_console: bool = True,
        log_to_file: bool = False,
    ) -> None:
        """Initialize logging system with console and/or file handlers.

        Configures root CassandraGargoyle logger with custom formatter.
        """
        root_logger = logging.getLogger(cls.ROOT_LOG)

        # Remove existing handlers
        for handler in root_logger.handlers[:]:
            root_logger.removeHandler(handler)

        formatter = _CustomFormatter()

        if log_on_console:
            cls._console_handler = logging.StreamHandler(sys.stderr)
            cls._console_handler.setFormatter(formatter)
            root_logger.addHandler(cls._console_handler)

        if log_to_file:
            log_dir = os.path.join(os.path.expanduser("~"), ".cassandragargoyle", "var", "log")
            os.makedirs(log_dir, exist_ok=True)
            pid = os.getpid()
            filename = os.path.join(log_dir, f"messages.{pid:06d}.log")
            cls._file_handler = logging.FileHandler(filename, encoding="utf-8")
            cls._file_handler.setFormatter(formatter)
            root_logger.addHandler(cls._file_handler)

        resolved = _LEVEL_ALIASES.get(log_level.upper(), logging.INFO)
        cls.set_level(resolved)

    @classmethod
    def set_level(cls, level: int) -> None:
        """Set log level for all CassandraGargoyle loggers."""
        root_logger = logging.getLogger(cls.ROOT_LOG)
        root_logger.setLevel(level)
        if cls._console_handler is not None:
            cls._console_handler.setLevel(level)
        if cls._file_handler is not None:
            cls._file_handler.setLevel(level)

    @classmethod
    def get_level(cls) -> int:
        """Get current log level."""
        return logging.getLogger(cls.ROOT_LOG).level

    @classmethod
    def set_log_level(cls, log_level: str) -> None:
        """Set log level using aliases.

        Supported aliases:
            TRACE/T/4 -> DEBUG (Python has no TRACE, maps to DEBUG)
            DEBUG/D/3 -> DEBUG
            INFO/I/2  -> INFO
            WARNING/W/1 -> WARNING
            ERROR/E/0 -> ERROR
        """
        if log_level is None:
            return

        resolved = _LEVEL_ALIASES.get(log_level.upper())
        if resolved is not None:
            cls.set_level(resolved)
        else:
            try:
                cls.set_level(getattr(logging, log_level.upper()))
            except AttributeError:
                logging.getLogger(cls.ROOT_LOG).warning(
                    "Invalid log level specified: %s", log_level
                )
