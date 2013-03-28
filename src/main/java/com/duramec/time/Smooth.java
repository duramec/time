package com.duramec.time;

//
//  - SLS
//    - stretch  (negative seconds)
//    - compress (positive seconds)
//  - Spread T60 gen with a range to a TAI
//  - Cosine?
//  - Spread T60 over the range of TAI
//  - Spread TAI over the range of T60
//  - Spread T72 over the range of TAI
//  - Spread TAI over the range of T72
//
//  global flag to indicate if SLS enabled in OS
//    - this is to eliminate the slew conversion in the 
//      event that FreeBSD ever fixes this or adds a kernel
// option for it.