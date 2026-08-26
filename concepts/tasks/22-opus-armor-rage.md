# Task 22 — Opus armor rage and balance

## Design

The complete Titan-plate set retains its active V-key shockwave and releases the suit's stored combat memory as **Resonant Rage** at the same moment. Rage is a bounded state, not a permanent passive:

- 6 seconds of Strength IV, one level over the set's balanced Strength III.
- +35% attack speed through a transient attribute modifier; it does not alter unrelated movement controls.
- 12-second shared shockwave/rage cooldown prevents permanent uptime.
- Server owns full-set validation, cooldown, modifier lifetime and cleanup on armor removal/disconnect.
- The base set remains exactly two additional health bars: Health Boost X (+40 health).

## QA

- Repeated packets cannot bypass cooldown or stack attack-speed modifiers.
- Removing any armor piece immediately removes rage and caps health safely.
- Expiry restores Strength III instead of silently losing the set's baseline Strength effect.
- Build, source-level constant audit and dedicated startup smoke test.

## Result — 2026-08-25

- Resonant Rage is activated by the armor's existing V-key shockwave only after the server verifies the full set and cooldown.
- A UUID-stable transient modifier supplies +35% attack speed; Strength rises from III to IV for 120 ticks and is restored correctly on expiry.
- Armor removal and disconnect clean up rage; repeated packets cannot stack the attribute modifier.
- EN/RU/UK/PL/DE feedback added. Language JSON, full build and current-source dedicated-server startup passed; live-player input remains a manual QA item.
