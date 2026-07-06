# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details

"""Fuzzy string-matching helpers.

Currently exposes Levenshtein edit distance and a derived similarity. Future
algorithms (Damerau-Levenshtein, Jaro-Winkler, n-gram, etc.) will be added
under this same package.
"""

from .levenshtein import distance, similarity

__all__ = ["distance", "similarity"]
