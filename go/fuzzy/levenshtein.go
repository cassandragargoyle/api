// This file is part of CassandraGargoyle Community Project
// Licensed under the MIT License - see LICENSE file for details

// Package fuzzy provides string-similarity helpers.
//
// Currently exposes Levenshtein edit distance and a derived similarity. Future
// algorithms (Damerau-Levenshtein, Jaro-Winkler, n-gram, etc.) will be added
// under this same package.
package fuzzy

// Distance returns the classic Levenshtein edit distance between a and b.
//
// Insertions, deletions, and substitutions each cost 1. The function is
// symmetric and operates on Unicode runes, not bytes, so multi-byte UTF-8
// sequences (accented letters, CJK, emoji) are counted as single units.
func Distance(a, b string) int {
	ra := []rune(a)
	rb := []rune(b)

	// Always iterate the longer sequence in the outer loop so the working row
	// holds min(n, m) + 1 entries.
	if len(ra) < len(rb) {
		ra, rb = rb, ra
	}

	n := len(ra)
	m := len(rb)

	if m == 0 {
		return n
	}

	previous := make([]int, m+1)
	current := make([]int, m+1)

	for j := 0; j <= m; j++ {
		previous[j] = j
	}

	for i := 1; i <= n; i++ {
		current[0] = i
		aRune := ra[i-1]

		for j := 1; j <= m; j++ {
			substitutionCost := 1
			if aRune == rb[j-1] {
				substitutionCost = 0
			}

			costInsert := current[j-1] + 1
			costDelete := previous[j] + 1
			costReplace := previous[j-1] + substitutionCost

			current[j] = min3(costInsert, costDelete, costReplace)
		}

		previous, current = current, previous
	}

	return previous[m]
}

// Similarity returns a value in [0.0, 1.0] derived from Distance.
//
// Defined as 1 - distance / max(len(a), len(b)) where the lengths are measured
// in runes. Two empty inputs are considered identical and yield 1.0.
func Similarity(a, b string) float64 {
	la := runeLen(a)
	lb := runeLen(b)
	max := la
	if lb > max {
		max = lb
	}
	if max == 0 {
		return 1.0
	}
	return 1.0 - float64(Distance(a, b))/float64(max)
}

func runeLen(s string) int {
	n := 0
	for range s {
		n++
	}
	return n
}

func min3(a, b, c int) int {
	m := a
	if b < m {
		m = b
	}
	if c < m {
		m = c
	}
	return m
}
