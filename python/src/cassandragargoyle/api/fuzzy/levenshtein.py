# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

"""Classic Levenshtein edit distance and a derived normalized similarity.

The implementation operates on Python ``str`` values, which are already indexed
by Unicode code point, so supplementary characters and combining-form-free
strings behave consistently with the Java and Go implementations of the same
package.
"""

from __future__ import annotations


def distance(a: str, b: str) -> int:
    """Return the classic Levenshtein edit distance between ``a`` and ``b``.

    Insertions, deletions, and substitutions each cost 1. The function is
    symmetric and operates on Unicode code points.

    Args:
        a: First input string.
        b: Second input string.

    Returns:
        Number of single-code-point edits needed to transform ``a`` into ``b``.

    Raises:
        TypeError: If either argument is not a ``str``.
    """
    if not isinstance(a, str):
        raise TypeError(f"a must be str, got {type(a).__name__}")
    if not isinstance(b, str):
        raise TypeError(f"b must be str, got {type(b).__name__}")

    # Always iterate the longer sequence in the outer loop so the working row
    # holds min(n, m) + 1 entries.
    if len(a) < len(b):
        a, b = b, a

    n = len(a)
    m = len(b)

    if m == 0:
        return n

    previous = list(range(m + 1))
    current = [0] * (m + 1)

    for i in range(1, n + 1):
        current[0] = i
        a_char = a[i - 1]

        for j in range(1, m + 1):
            substitution_cost = 0 if a_char == b[j - 1] else 1

            cost_insert = current[j - 1] + 1
            cost_delete = previous[j] + 1
            cost_replace = previous[j - 1] + substitution_cost

            current[j] = min(cost_insert, cost_delete, cost_replace)

        previous, current = current, previous

    return previous[m]


def similarity(a: str, b: str) -> float:
    """Return a normalized similarity in ``[0.0, 1.0]`` derived from the distance.

    Defined as ``1 - distance / max(len(a), len(b))`` where the lengths are
    measured in Unicode code points. Two empty inputs are considered identical
    and yield ``1.0``.

    Args:
        a: First input string.
        b: Second input string.

    Returns:
        Similarity score in ``[0.0, 1.0]``.

    Raises:
        TypeError: If either argument is not a ``str``.
    """
    if not isinstance(a, str):
        raise TypeError(f"a must be str, got {type(a).__name__}")
    if not isinstance(b, str):
        raise TypeError(f"b must be str, got {type(b).__name__}")

    max_len = max(len(a), len(b))
    if max_len == 0:
        return 1.0
    return 1.0 - distance(a, b) / max_len
