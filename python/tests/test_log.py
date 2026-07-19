# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

import logging
import os
import unittest
from unittest.mock import patch

from cassandragargoyle.api.log import LogFactory, Logging


class TestLogFactory(unittest.TestCase):
    """Tests for LogFactory."""

    def test_get_logger_with_name(self):
        logger = LogFactory.get_logger("test.module")

        self.assertIsInstance(logger, logging.Logger)
        self.assertEqual(logger.name, "test.module")

    def test_get_logger_without_name_uses_caller_module(self):
        logger = LogFactory.get_logger()

        self.assertIsInstance(logger, logging.Logger)
        # Should use this test module's __name__
        self.assertEqual(logger.name, __name__)

    def test_log_debug_with_trace_when_condition_met(self):
        with patch.dict(os.environ, {"CASSANDRAGARGOYLE_DEBUG": "true"}):
            # LogFactory uses its own __name__ for the logger
            logger_name = f"{LogFactory.__module__}.{LogFactory.__name__}"
            logger = logging.getLogger(logger_name)
            logger.setLevel(logging.DEBUG)

            with self.assertLogs(logger, level=logging.DEBUG) as cm:
                LogFactory.log_debug_with_trace("test debug message")

            self.assertTrue(
                any("test debug message" in msg for msg in cm.output)
            )

    def test_log_debug_with_trace_skipped_when_condition_false(self):
        with patch.dict(os.environ, {"CASSANDRAGARGOYLE_DEBUG": "true"}):
            logger = logging.getLogger("cassandragargoyle.api.log.factory.LogFactory")
            logger.setLevel(logging.DEBUG)

            # Should not log when log_condition is False
            LogFactory.log_debug_with_trace("should not appear", log_condition=False)


class TestLogging(unittest.TestCase):
    """Tests for Logging."""

    def setUp(self):
        # Clean up root logger handlers before each test
        root_logger = logging.getLogger(Logging.ROOT_LOG)
        for handler in root_logger.handlers[:]:
            root_logger.removeHandler(handler)
        Logging._console_handler = None
        Logging._file_handler = None

    def test_initialize_configures_console_handler(self):
        Logging.initialize(log_level="INFO", log_on_console=True, log_to_file=False)

        root_logger = logging.getLogger(Logging.ROOT_LOG)
        self.assertTrue(len(root_logger.handlers) > 0)
        self.assertIsNotNone(Logging._console_handler)

    def test_initialize_configures_file_handler(self):
        Logging.initialize(log_level="INFO", log_on_console=False, log_to_file=True)

        try:
            root_logger = logging.getLogger(Logging.ROOT_LOG)
            self.assertTrue(len(root_logger.handlers) > 0)
            self.assertIsNotNone(Logging._file_handler)
        finally:
            # Clean up file handler
            if Logging._file_handler:
                Logging._file_handler.close()
                Logging._file_handler = None

    def test_set_level_and_get_level_round_trip(self):
        Logging.initialize(log_level="INFO", log_on_console=True, log_to_file=False)

        Logging.set_level(logging.DEBUG)
        self.assertEqual(Logging.get_level(), logging.DEBUG)

        Logging.set_level(logging.WARNING)
        self.assertEqual(Logging.get_level(), logging.WARNING)

    def test_set_log_level_alias_trace(self):
        Logging.initialize()
        Logging.set_log_level("TRACE")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_t(self):
        Logging.initialize()
        Logging.set_log_level("T")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_4(self):
        Logging.initialize()
        Logging.set_log_level("4")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_debug(self):
        Logging.initialize()
        Logging.set_log_level("DEBUG")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_d(self):
        Logging.initialize()
        Logging.set_log_level("D")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_3(self):
        Logging.initialize()
        Logging.set_log_level("3")
        self.assertEqual(Logging.get_level(), logging.DEBUG)

    def test_set_log_level_alias_info(self):
        Logging.initialize()
        Logging.set_log_level("INFO")
        self.assertEqual(Logging.get_level(), logging.INFO)

    def test_set_log_level_alias_i(self):
        Logging.initialize()
        Logging.set_log_level("I")
        self.assertEqual(Logging.get_level(), logging.INFO)

    def test_set_log_level_alias_warning(self):
        Logging.initialize()
        Logging.set_log_level("WARNING")
        self.assertEqual(Logging.get_level(), logging.WARNING)

    def test_set_log_level_alias_w(self):
        Logging.initialize()
        Logging.set_log_level("W")
        self.assertEqual(Logging.get_level(), logging.WARNING)

    def test_set_log_level_alias_error(self):
        Logging.initialize()
        Logging.set_log_level("ERROR")
        self.assertEqual(Logging.get_level(), logging.ERROR)

    def test_set_log_level_alias_e(self):
        Logging.initialize()
        Logging.set_log_level("E")
        self.assertEqual(Logging.get_level(), logging.ERROR)

    def test_set_log_level_alias_0(self):
        Logging.initialize()
        Logging.set_log_level("0")
        self.assertEqual(Logging.get_level(), logging.ERROR)

    def test_set_log_level_none_does_nothing(self):
        Logging.initialize(log_level="INFO")
        Logging.set_log_level(None)
        self.assertEqual(Logging.get_level(), logging.INFO)


class TestCustomFormatter(unittest.TestCase):
    """Tests for custom log formatter output."""

    def test_formatter_output_matches_pattern(self):
        Logging.initialize(log_level="INFO", log_on_console=True, log_to_file=False)

        handler = Logging._console_handler
        self.assertIsNotNone(handler)

        record = logging.LogRecord(
            name="test.logger",
            level=logging.INFO,
            pathname="test.py",
            lineno=1,
            msg="test message",
            args=None,
            exc_info=None,
        )

        formatted = handler.formatter.format(record)

        # Verify format: dd/MM/yyyy HH:mm:ss.SSS LEVEL [logger]: message

        pattern = r"\d{2}/\d{2}/\d{4} \d{2}:\d{2}:\d{2}\.\d{3} INFO \[test\.logger\]: test message"
        self.assertRegex(formatted, pattern)


if __name__ == "__main__":
    unittest.main()
