# MagicSMP Sell Fix v4
Uses the uploaded MagicSMP(5).jar as the exact baseline.

This revision fixes the previous guard itself: it no longer restores/replaces the cursor, no longer assumes slots 45-53 are protected in confirm mode, and never opens/rebuilds menus. The original MagicSMP SellMenu remains responsible for opening multiplier progression and returning from it.
