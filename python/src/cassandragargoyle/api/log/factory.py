# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

import inspect
import logging
import os
import traceback


class LogFactory:
    """Factory for creating loggers with consistent naming."""

    @staticmethod
    def get_logger(name: str | None = None) -> logging.Logger:
        """Create a logger for the given module/class name.

        If name is None, uses the caller's module name.
        """
        if name is None:
            frame = inspect.stack()[1]
            name = frame[0].f_globals.get("__name__", __name__)
        return logging.getLogger(name)

    @staticmethod
    def log_debug_with_trace(message: str, log_condition: bool = True) -> None:
        """Log message at DEBUG level with stack trace when condition is met
        and cassandragargoyle.debug flag is enabled.
        """
        if log_condition and os.environ.get("CASSANDRAGARGOYLE_DEBUG", "").lower() == "true":
            logger = logging.getLogger(f"{__name__}.{LogFactory.__name__}")
            if logger.isEnabledFor(logging.DEBUG):
                logger.debug(message)
                logger.debug("".join(traceback.format_stack()))
