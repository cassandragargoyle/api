# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

"""Tests for cassandragargoyle.api.fuzzy.

The ``test_shared_vector_*`` cases mirror the canonical vectors from issue
#010 and must agree byte-for-byte with the Java and Go implementations of
the same package.
"""

from __future__ import annotations

import unittest

from cassandragargoyle.api.fuzzy import distance, similarity


class TestSharedVectors(unittest.TestCase):
    """Cross-language parity vectors from issue #010."""

    def test_shared_vector_both_empty(self):
        self.assertEqual(0, distance("", ""))
        self.assertAlmostEqual(1.0, similarity("", ""))

    def test_shared_vector_first_empty(self):
        self.assertEqual(3, distance("", "abc"))
        self.assertAlmostEqual(0.0, similarity("", "abc"))

    def test_shared_vector_second_empty(self):
        self.assertEqual(3, distance("abc", ""))
        self.assertAlmostEqual(0.0, similarity("abc", ""))

    def test_shared_vector_identical(self):
        self.assertEqual(0, distance("abc", "abc"))
        self.assertAlmostEqual(1.0, similarity("abc", "abc"))

    def test_shared_vector_kitten_sitting(self):
        self.assertEqual(3, distance("kitten", "sitting"))
        self.assertAlmostEqual(1.0 - 3.0 / 7.0, similarity("kitten", "sitting"))

    def test_shared_vector_flaw_lawn(self):
        self.assertEqual(2, distance("flaw", "lawn"))
        self.assertAlmostEqual(0.5, similarity("flaw", "lawn"))

    def test_shared_vector_gumbo_gambol(self):
        self.assertEqual(2, distance("gumbo", "gambol"))
        self.assertAlmostEqual(1.0 - 2.0 / 6.0, similarity("gumbo", "gambol"))

    def test_shared_vector_saturday_sunday(self):
        self.assertEqual(3, distance("Saturday", "Sunday"))
        self.assertAlmostEqual(1.0 - 3.0 / 8.0, similarity("Saturday", "Sunday"))

    def test_shared_vector_cafe_accented(self):
        self.assertEqual(1, distance("café", "cafe"))
        self.assertAlmostEqual(0.75, similarity("café", "cafe"))

    def test_shared_vector_cjk_prefix(self):
        self.assertEqual(1, distance("日本語", "日本"))
        self.assertAlmostEqual(1.0 - 1.0 / 3.0, similarity("日本語", "日本"))


class TestProperties(unittest.TestCase):
    """Algorithmic properties beyond the shared vectors."""

    def test_distance_is_symmetric(self):
        self.assertEqual(distance("kitten", "sitting"), distance("sitting", "kitten"))
        self.assertEqual(distance("café", "cafe"), distance("cafe", "café"))

    def test_emoji_counted_as_single_unit(self):
        # Python str is already code-point indexed. The single grinning-face
        # code point should yield distance 1, not 2.
        emoji = "😀"
        self.assertEqual(1, len(emoji))
        self.assertEqual(1, distance(emoji, ""))
        self.assertAlmostEqual(0.0, similarity(emoji, ""))

    def test_similarity_is_bounded(self):
        s = similarity("kitten", "sitting")
        self.assertGreaterEqual(s, 0.0)
        self.assertLessEqual(s, 1.0)

    def test_rejects_non_str_inputs(self):
        with self.assertRaises(TypeError):
            distance(None, "x")  # type: ignore[arg-type]
        with self.assertRaises(TypeError):
            distance("x", None)  # type: ignore[arg-type]
        with self.assertRaises(TypeError):
            similarity(None, "x")  # type: ignore[arg-type]
        with self.assertRaises(TypeError):
            similarity(b"x", "x")  # type: ignore[arg-type]


if __name__ == "__main__":
    unittest.main()
