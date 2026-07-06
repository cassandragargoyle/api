// This file is part of CassandraGargoyle Community Project
// Licensed under the MIT License - see LICENSE file for details

package fuzzy

import (
	"math"
	"testing"
)

const eps = 1e-9

// TestDistanceSharedVectors covers the canonical vectors from issue #010.
// These cases must agree byte-for-byte with the Java and Python implementations.
func TestDistanceSharedVectors(t *testing.T) {
	tests := []struct {
		name string
		a, b string
		want int
	}{
		{"both empty", "", "", 0},
		{"first empty", "", "abc", 3},
		{"second empty", "abc", "", 3},
		{"identical", "abc", "abc", 0},
		{"kitten vs sitting", "kitten", "sitting", 3},
		{"flaw vs lawn", "flaw", "lawn", 2},
		{"gumbo vs gambol", "gumbo", "gambol", 2},
		{"Saturday vs Sunday", "Saturday", "Sunday", 3},
		{"café vs cafe", "café", "cafe", 1},
		{"cjk prefix", "日本語", "日本", 1},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := Distance(tt.a, tt.b)
			if got != tt.want {
				t.Errorf("Distance(%q, %q) = %d, want %d", tt.a, tt.b, got, tt.want)
			}
		})
	}
}

func TestSimilaritySharedVectors(t *testing.T) {
	tests := []struct {
		name string
		a, b string
		want float64
	}{
		{"both empty", "", "", 1.0},
		{"first empty", "", "abc", 0.0},
		{"second empty", "abc", "", 0.0},
		{"identical", "abc", "abc", 1.0},
		{"kitten vs sitting", "kitten", "sitting", 1.0 - 3.0/7.0},
		{"flaw vs lawn", "flaw", "lawn", 0.5},
		{"gumbo vs gambol", "gumbo", "gambol", 1.0 - 2.0/6.0},
		{"Saturday vs Sunday", "Saturday", "Sunday", 1.0 - 3.0/8.0},
		{"café vs cafe", "café", "cafe", 0.75},
		{"cjk prefix", "日本語", "日本", 1.0 - 1.0/3.0},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got := Similarity(tt.a, tt.b)
			if math.Abs(got-tt.want) > eps {
				t.Errorf("Similarity(%q, %q) = %v, want %v", tt.a, tt.b, got, tt.want)
			}
		})
	}
}

func TestDistanceIsSymmetric(t *testing.T) {
	pairs := []struct{ a, b string }{
		{"kitten", "sitting"},
		{"café", "cafe"},
		{"日本語", "日本"},
	}
	for _, p := range pairs {
		t.Run(p.a+"<->"+p.b, func(t *testing.T) {
			ab := Distance(p.a, p.b)
			ba := Distance(p.b, p.a)
			if ab != ba {
				t.Errorf("symmetry broken: Distance(%q, %q)=%d but Distance(%q, %q)=%d",
					p.a, p.b, ab, p.b, p.a, ba)
			}
		})
	}
}

// TestRuneVsByteLength is the headline regression case for the Go binding.
// "café" is 4 runes but 5 UTF-8 bytes (the é is 0xC3 0xA9). A byte-based
// implementation would report distance("café", "cafe") = 2 — wrong.
func TestRuneVsByteLength(t *testing.T) {
	if got := Distance("café", "cafe"); got != 1 {
		t.Errorf("Distance(\"café\", \"cafe\") = %d, want 1 — implementation operates on bytes, not runes", got)
	}
	// Emoji is a 4-byte UTF-8 sequence representing a single rune.
	if got := Distance("😀", ""); got != 1 {
		t.Errorf("Distance(\"😀\", \"\") = %d, want 1", got)
	}
}

func TestSimilarityIsBounded(t *testing.T) {
	s := Similarity("kitten", "sitting")
	if s < 0.0 || s > 1.0 {
		t.Errorf("Similarity out of range: %v", s)
	}
}
