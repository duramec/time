package com.duramec.time;

// implements an extremely strict version of RFC3339

// - requires millisecond precision at minimum
// - handles a .60 leap second properly
//
//   - parseUTC(string) -> Instant
//   - parseTAI(string) -> TAI, which is Instant + offset
//                         GPS can be easily figured out from this

public class RFC3339 {

}